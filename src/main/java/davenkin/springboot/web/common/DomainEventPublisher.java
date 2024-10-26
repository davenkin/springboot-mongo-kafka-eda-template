package davenkin.springboot.web.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static java.time.Instant.now;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventPublisher {
    private static final String MIN_START_EVENT_ID = "EVT00000000000000001";
    private static final int BATCH_SIZE = 100;
    private static final int MAX_FETCH_SIZE = 10000;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final LockingTaskExecutor lockingTaskExecutor;
    private final PublishingDomainEventDao publishingDomainEventDao;

    private final TaskExecutor taskExecutor;

    public int publishStagedDomainEvents() {
        try {
            //通过分布式锁保证只有一个publisher工作，以此保证消息发送的顺序
            LockingTaskExecutor.TaskResult<Integer> result = lockingTaskExecutor.executeWithLock(this::doPublishStagedDomainEvents,
                    new LockConfiguration(now(), "publish-domain-events", ofMinutes(1), ofMillis(1)));
            Integer publishedCount = result.getResult();
            return publishedCount != null ? publishedCount : 0;
        } catch (Throwable e) {
            log.error("Error happened while publish domain events.", e);
            return 0;
        }
    }


    private int doPublishStagedDomainEvents() {
        int counter = 0;
        String startEventId = MIN_START_EVENT_ID;
        List<CompletableFuture<SendResult<String, DomainEvent>>> futures = new ArrayList<>();

        while (true) {
            List<DomainEvent> domainEvents = publishingDomainEventDao.stagedEvents(startEventId, BATCH_SIZE);
            if (isEmpty(domainEvents)) {
                break;
            }

            for (DomainEvent event : domainEvents) {
                CompletableFuture<SendResult<String, DomainEvent>> future = this.kafkaTemplate.send("domain-event", event.getArId(), event)
                        .whenCompleteAsync((result, ex) -> {
                            String eventId = result.getProducerRecord().value().getId();
                            if (ex == null) {
                                this.publishingDomainEventDao.successPublish(eventId);
                            } else {
                                this.publishingDomainEventDao.failPublish(eventId);
                                log.error("Error publishing domain event [{}].", eventId);
                            }
                        }, taskExecutor);
                futures.add(future);
            }

            counter = domainEvents.size() + counter;
            if (counter >= MAX_FETCH_SIZE) {
                break;
            }

            startEventId = domainEvents.get(domainEvents.size() - 1).getId(); // Start ID for next batch
        }

        futures.forEach(CompletableFuture::join);
        return counter;
    }

}
