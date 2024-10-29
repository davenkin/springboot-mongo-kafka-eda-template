package davenkin.springboot.web.common;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static java.time.Instant.now;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventPublisher {
  private static final String MIN_START_EVENT_ID = "EVT00000000000000001";
  private static final int BATCH_SIZE = 100;
  private static final int MAX_FETCH_SIZE = 10000;
  private final LockingTaskExecutor lockingTaskExecutor;
  private final PublishingDomainEventDao publishingDomainEventDao;
  private final DomainEventSender domainEventSender;
  private final TaskExecutor taskExecutor;

  public void publishStagedDomainEvents() {
    try {
      // Use a distributed lock to ensure only one node get run as a time, otherwise it may easily result in duplicated events
      var result = lockingTaskExecutor.executeWithLock(this::doPublishStagedDomainEvents,
          new LockConfiguration(now(), "publish-domain-events", ofMinutes(1), ofMillis(1)));
      Integer publishedCount = result.getResult();
      int count = publishedCount != null ? publishedCount : 0;
      log.debug("Published {} domain events.", count);
    } catch (Throwable e) {
      log.error("Error happened while publish domain events.", e);
    }
  }

  private int doPublishStagedDomainEvents() {
    int counter = 0;
    String startEventId = MIN_START_EVENT_ID;
    List<CompletableFuture<String>> futures = new ArrayList<>();

    while (true) {
      List<DomainEvent> domainEvents = publishingDomainEventDao.stagedEvents(startEventId, BATCH_SIZE);
      if (isEmpty(domainEvents)) {
        break;
      }

      for (DomainEvent event : domainEvents) {
        var future = this.domainEventSender.send(event)
            .whenCompleteAsync((eventId, ex) -> {
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

      startEventId = domainEvents.get(domainEvents.size() - 1).getId(); // Start event ID for next batch
    }

    futures.forEach(CompletableFuture::join);
    return counter;
  }
}
