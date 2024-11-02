package davenkin.springboot.web.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractDomainEventHandler<T extends DomainEvent> implements DomainEventHandler<T> {

    @Autowired
    private ConsumingDomainEventDao consumingDomainEventDao;

    @Override
    public void handle(T domainEvent) {
        if (this.isRepeatable() || this.consumingDomainEventDao.checkNotConsumed(domainEvent)) {
            doHandle(domainEvent);
        } else {
            log.warn("Domain event[{}] has been consumed previously and its handler[{}] is not idempotent, skip handling.",
                    domainEvent.getId(), this.getClass().getSimpleName());
        }
    }

    protected abstract void doHandle(T domainEvent);
}
