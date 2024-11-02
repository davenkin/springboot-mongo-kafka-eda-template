package davenkin.springboot.web.common.domainevent.consume;

import davenkin.springboot.web.common.domainevent.DomainEvent;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractTransactionalDomainEventHandler<T extends DomainEvent> extends AbstractDomainEventHandler<T> {

    @Override
    @Transactional
    public void handle(ConsumingDomainEvent<T> consumingDomainEvent) {
        super.handle(consumingDomainEvent);
    }
}