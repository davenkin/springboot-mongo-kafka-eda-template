package davenkin.springboot.web.common;

import davenkin.springboot.web.user.domain.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventHandler extends AbstractTransactionalDomainEventHandler<UserCreatedEvent> {
    @Override
    protected void doHandle(UserCreatedEvent domainEvent) {
        log.info("Received UserCreatedEvent: {}", domainEvent.getId());
    }
}
