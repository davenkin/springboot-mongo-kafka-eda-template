package davenkin.springboot.web.common.domainevent.consume;

import davenkin.springboot.web.common.domainevent.DomainEvent;

public interface DomainEventHandler<T extends DomainEvent> {

    // Can this handler been safely invoked multiple times for a given a domain event
    default boolean isRepeatable() {
        return false;
    }

    void handle(ConsumingDomainEvent<T> consumingDomainEvent);
}
