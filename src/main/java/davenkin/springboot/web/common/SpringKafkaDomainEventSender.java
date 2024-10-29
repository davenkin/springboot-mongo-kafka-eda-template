package davenkin.springboot.web.common;

import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringKafkaDomainEventSender implements DomainEventSender {
  private static final String KAFKA_DOMAIN_EVENT_TOPIC_SUFFIX = "_domain_event";
  private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

  @Override
  public CompletableFuture<String> send(DomainEvent event) {
    String topic = event.getArType().name().toLowerCase() + KAFKA_DOMAIN_EVENT_TOPIC_SUFFIX;
    return this.kafkaTemplate.send(topic, event.getArId(), event)
        .thenApply(record -> record.getProducerRecord().value().getId());
  }
}
