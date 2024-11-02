package davenkin.springboot.web.common.domainevent.consume;

import davenkin.springboot.web.common.domainevent.DomainEvent;

public interface DomainEventHandler<T extends DomainEvent> {

    // returning "true" means the event handler can be run repeatedly for the same event and also can handle out of order events
    default boolean isRepeatable() {
        return false;
    }

    void handle(ConsumingDomainEvent<T> consumingDomainEvent);
}
