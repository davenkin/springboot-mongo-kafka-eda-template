package davenkin.springboot.web.common;

public abstract class AbstractDomainEventHandler<T extends DomainEvent> implements DomainEventHandler<T> {
  @Override
  public void handle(T event) {
    doHandle(event);
  }

  protected abstract void doHandle(T event);
}
