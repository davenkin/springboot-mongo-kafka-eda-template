package davenkin.springboot.web.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumingDomainEventDao {
  private final MongoTemplate mongoTemplate;

  public boolean checkNotConsumed(DomainEvent domainEvent) {
    try {
      this.mongoTemplate.insert(new ConsumingDomainEvent(domainEvent));
    } catch (Throwable t) {
      return false;
    }
    return true;
  }
}
