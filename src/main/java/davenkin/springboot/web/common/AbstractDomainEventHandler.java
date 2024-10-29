package davenkin.springboot.web.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractDomainEventHandler<T extends DomainEvent> implements DomainEventHandler<T> {

    @Autowired
    private ConsumingDomainEventDao consumingDomainEventDao;

    @Override
    public void handle(T domainEvent) {
        if (this.consumingDomainEventDao.checkNotConsumed(domainEvent)) {
            doHandle(domainEvent);
        } else {
            log.warn("Domain event[{}] has already been consumed previously, skip handling.", domainEvent.getId());
        }
    }

    protected abstract void doHandle(T domainEvent);
}
