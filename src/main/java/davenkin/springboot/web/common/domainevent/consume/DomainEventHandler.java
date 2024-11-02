package davenkin.springboot.web.common.domainevent.consume;

import davenkin.springboot.web.common.domainevent.DomainEvent;

public interface DomainEventHandler<T extends DomainEvent> {

    default boolean isIdempotent() {
        return false;
    }

    void handle(ConsumingDomainEvent<T> consumingDomainEvent);
}
