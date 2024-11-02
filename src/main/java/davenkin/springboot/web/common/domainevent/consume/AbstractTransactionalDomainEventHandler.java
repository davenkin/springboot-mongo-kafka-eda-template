package davenkin.springboot.web.common.domainevent.consume;

import davenkin.springboot.web.common.domainevent.DomainEvent;
import org.springframework.transaction.annotation.Transactional;

// Make the event processing and event recording in the same transaction for situations where these two should be atomic
// Usually used when there is DB changes during event processing
public abstract class AbstractTransactionalDomainEventHandler<T extends DomainEvent> extends AbstractDomainEventHandler<T> {

    @Override
    @Transactional
    public void handle(ConsumingDomainEvent<T> consumingDomainEvent) {
        super.handle(consumingDomainEvent);
    }
}
