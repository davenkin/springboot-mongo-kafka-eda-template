package davenkin.springboot.web.common.domainevent.consume;

import davenkin.springboot.web.common.domainevent.DomainEvent;
import davenkin.springboot.web.common.domainevent.DomainEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import static davenkin.springboot.web.common.Constants.CONSUMING_DOMAIN_EVENT_COLLECTION;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldNameConstants
@NoArgsConstructor(access = PRIVATE)
@Document(CONSUMING_DOMAIN_EVENT_COLLECTION)
@TypeAlias("CONSUMING_DOMAIN_EVENT")
public class ConsumingDomainEvent<T extends DomainEvent> {
    private String id;
    private String arId;
    private DomainEventType type;
    private Instant consumedAt;

    private T event;

    private boolean isRetry;

    public ConsumingDomainEvent(T event, boolean isRetry) {
        requireNonNull(event, "Domain event must not be null.");
        this.id = event.getId();
        this.arId = event.getArId();
        this.type = event.getType();
        this.consumedAt = Instant.now();
        this.event = event;
        this.isRetry = isRetry;
    }
}
