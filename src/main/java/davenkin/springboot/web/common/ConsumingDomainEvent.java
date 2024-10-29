package davenkin.springboot.web.common;

import static davenkin.springboot.web.common.Constants.CONSUMING_DOMAIN_EVENT_COLLECTION;
import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@FieldNameConstants
@NoArgsConstructor(access = PRIVATE)
@Document(CONSUMING_DOMAIN_EVENT_COLLECTION)
@TypeAlias("CONSUMING_DOMAIN_EVENT")
public class ConsumingDomainEvent {
  private String id;
  private String arId;
  private DomainEventType type;
  private Instant consumedAt;

  public ConsumingDomainEvent(DomainEvent event) {
    this.id = event.getId();
    this.arId = event.getArId();
    this.type = event.getType();
    this.consumedAt = Instant.now();
  }
}
