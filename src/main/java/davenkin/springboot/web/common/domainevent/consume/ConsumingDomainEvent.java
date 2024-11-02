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

// Wrapper for DomainEvent when consuming
// Can add more information(such as if the event is redelivered etc.) if required, but should not be coupled to a specific messaging middleware
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

    public ConsumingDomainEvent(T event) {
        requireNonNull(event, "Domain event must not be null.");
        this.id = event.getId();
        this.arId = event.getArId();
        this.type = event.getType();
        this.consumedAt = Instant.now();
        this.event = event;
    }
}
