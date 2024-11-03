package davenkin.springboot.web.common.domainevent.consume;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static davenkin.springboot.web.common.utils.CommonUtils.singleParameterizedArgumentClassOf;

// The entry point for handling domain event, it finds the correct handler for this event and delegate to it
@Component
@RequiredArgsConstructor
public class DomainEventConsumer<T> {
    private final Map<String, Class<?>> handlerEventClassMap = new ConcurrentHashMap<>();

    private final List<DomainEventHandler<T>> handlers;

    public void consume(ConsumingDomainEvent<T> consumingDomainEvent) {
        this.handlers.stream().filter(handler -> canHandle(handler, consumingDomainEvent.getEvent()))
                .findFirst()
                .ifPresent(handler -> handler.handle(consumingDomainEvent));
    }

    private boolean canHandle(DomainEventHandler<T> handler, T event) {
        String handlerClassName = handler.getClass().getSimpleName();

        if (!this.handlerEventClassMap.containsKey(handlerClassName)) {
            Class<?> handlerEventClass = singleParameterizedArgumentClassOf(handler.getClass());
            this.handlerEventClassMap.put(handlerClassName, handlerEventClass);
        }

        Class<?> finalHandlerEventClass = this.handlerEventClassMap.get(handlerClassName);
        return finalHandlerEventClass != null && finalHandlerEventClass.isAssignableFrom(event.getClass());
    }
}
