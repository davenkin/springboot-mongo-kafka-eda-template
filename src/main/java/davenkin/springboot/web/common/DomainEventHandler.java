package davenkin.springboot.web.common;

public interface DomainEventHandler<T extends DomainEvent> {
  void handle(T event);
}
