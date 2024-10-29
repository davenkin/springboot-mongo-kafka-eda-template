package davenkin.springboot.web.common;

import static davenkin.springboot.web.common.CommonUtils.singleParameterizedArgumentClassOf;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventConsumer<T extends DomainEvent> {
  private final Map<String, Class<?>> handlerEventClasseMap = new ConcurrentHashMap<>();

  private final List<DomainEventHandler<T>> handlers;

  public void consume(T event) {
    this.handlers.stream().filter(handler -> canHandle(handler, event))
        .findFirst()
        .ifPresent(handler -> handler.handle(event));
  }

  private boolean canHandle(DomainEventHandler<T> handler, T event) {
    String handlerClassName = handler.getClass().getSimpleName();

    if (!this.handlerEventClasseMap.containsKey(handlerClassName)) {
      Class<?> handlerEventClass = singleParameterizedArgumentClassOf(handler.getClass());
      this.handlerEventClasseMap.put(handlerClassName, handlerEventClass);
    }

    Class<?> finalHandlerEventClass = this.handlerEventClasseMap.get(handlerClassName);
    return finalHandlerEventClass != null && finalHandlerEventClass.isAssignableFrom(event.getClass());
  }
}
