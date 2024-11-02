package davenkin.springboot.web.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
