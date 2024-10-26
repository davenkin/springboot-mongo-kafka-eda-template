package davenkin.springboot.web.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static davenkin.springboot.web.common.CommonUtils.requireNonBlank;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
// The no arg constructor is used by Jackson and Spring Data etc. to create objects
@NoArgsConstructor(access = PROTECTED)
public abstract class AggregateRoot {
    private String id;

    // Domain events are stored temporarily in the aggregate root
    // Domain events are not persisted together with the aggregate roots as events will be stored in separately
    // @Transient here is very import for not persisting events with aggregate root, otherwise we need to do this manually by ourselves
    @Transient
    private List<DomainEvent> events;

    private Instant createdAt;

    @Version
    @Getter(PRIVATE)
    private Long _version;

    protected AggregateRoot(String id) {
        requireNonBlank(id, "ID must not be blank.");

        this.id = id;
        this.createdAt = Instant.now();
    }

    // raiseEvent() only stores events in aggregate root temporarily, the events will be persisted into DB by Repository
    // The actual sending of events to Kafka is handled by DomainEventPublisher
    protected void raiseEvent(DomainEvent event) {
        event.setArId(this.id);
        events().add(event);
    }

    private List<DomainEvent> events() {
        if (events == null) {
            this.events = new ArrayList<>();
        }

        return events;
    }
}
