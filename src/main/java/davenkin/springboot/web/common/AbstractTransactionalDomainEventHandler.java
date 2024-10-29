package davenkin.springboot.web.common;

import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractTransactionalDomainEventHandler<T extends DomainEvent> extends AbstractDomainEventHandler<T> {
  @Override
  @Transactional
  public void handle(T domainEvent) {
    super.handle(domainEvent);
  }
}
