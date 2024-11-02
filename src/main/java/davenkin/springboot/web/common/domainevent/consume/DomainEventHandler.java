package davenkin.springboot.web.common.domainevent.consume;

public interface DomainEventHandler<T> {

    default boolean isIdempotent() {
        return false;
    }

    void handle(ConsumingDomainEvent<T> consumingDomainEvent);
}
