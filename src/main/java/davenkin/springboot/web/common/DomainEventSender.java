package davenkin.springboot.web.common;

import java.util.concurrent.CompletableFuture;

public interface DomainEventSender {
    CompletableFuture<String> send(DomainEvent domainEvent);
}
