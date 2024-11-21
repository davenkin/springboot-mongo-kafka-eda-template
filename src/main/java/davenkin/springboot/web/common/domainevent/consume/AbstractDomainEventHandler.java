package davenkin.springboot.web.common.domainevent.consume;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

// This base event handler, before handling event it checks:
// 1. If the handler is idempotent, handle the event and return;
// 2. If the event is not consumed already, handle the event and return;
// 3. If the event is already consumed, do nothing;
// Best practices is to stick to AbstractTransactionalDomainEventHandler and idempotent as mush as possible
@Slf4j
public abstract class AbstractDomainEventHandler<T> implements DomainEventHandler<T> {

    @Autowired
    private ConsumingDomainEventDao<T> consumingDomainEventDao;

    @Override
    public void handle(ConsumingDomainEvent<T> consumingDomainEvent) {
        if (this.isIdempotent() || this.consumingDomainEventDao.recordAsConsumed(consumingDomainEvent)) {
            doHandle(consumingDomainEvent.getEvent());
        } else {
            log.warn("Domain event[{}] has been consumed previously and its handler[{}] is not idempotent, skip handling.",
                    consumingDomainEvent.getId(), this.getClass().getSimpleName());
        }
    }

    protected abstract void doHandle(T domainEvent);
}
