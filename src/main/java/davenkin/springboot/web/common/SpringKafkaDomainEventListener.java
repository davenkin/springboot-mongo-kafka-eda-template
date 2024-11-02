package davenkin.springboot.web.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@NonBuildProfile
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringKafkaDomainEventListener {
    private final DomainEventConsumer<DomainEvent> domainEventConsumer;

    @KafkaListener(id = "domain-event-listener", groupId = "changeme", topics = "user_domain_event")
    public void listen(DomainEvent event) {
        ConsumingDomainEvent<DomainEvent> consumingDomainEvent = new ConsumingDomainEvent<>(event, false);
        this.domainEventConsumer.consume(consumingDomainEvent);
    }
}
