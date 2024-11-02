package davenkin.springboot.web.common.domainevent.consume;

import davenkin.springboot.web.common.domainevent.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

// This base event handler, before handling event it checks:
// 1. If the event is for retry, handle the event and return;
// 2. If the handler can be run repeatable, handle the event and return;
// 3. If the event is not consumed already, handle the event and return;
// 4. If the event is already consumed, do nothing;
// For all the above handling paths, the event will also be recorded in DB as consumed
@Slf4j
public abstract class AbstractDomainEventHandler<T extends DomainEvent> implements DomainEventHandler<T> {

    @Autowired
    private ConsumingDomainEventDao<T> consumingDomainEventDao;

    @Override
    public void handle(ConsumingDomainEvent<T> consumingDomainEvent) {
        if (consumingDomainEvent.isRetry()) {
            log.warn("Consuming retried domain event[{}].", consumingDomainEvent.getId());
            this.consumingDomainEventDao.recordAsConsumed(consumingDomainEvent);
            doHandle(consumingDomainEvent.getEvent());
            return;
        }

        if (this.isRepeatable() || this.consumingDomainEventDao.recordAsConsumed(consumingDomainEvent)) {
            doHandle(consumingDomainEvent.getEvent());
        } else {
            log.warn("Domain event[{}] has been consumed previously and its handler[{}] is not repeatable, skip handling.",
                    consumingDomainEvent.getId(), this.getClass().getSimpleName());
        }
    }

    protected abstract void doHandle(T domainEvent);
}
