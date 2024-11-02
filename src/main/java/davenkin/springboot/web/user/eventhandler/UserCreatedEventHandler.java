package davenkin.springboot.web.user.eventhandler;

import davenkin.springboot.web.common.domainevent.consume.AbstractDomainEventHandler;
import davenkin.springboot.web.user.domain.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// Sending email should not be called repeatedly, so isRepeatable() should return false
// Also it does not involve DB changes, so no need to extends AbstractTransactionalDomainEventHandler

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventHandler extends AbstractDomainEventHandler<UserCreatedEvent> {

    @Override
    protected void doHandle(UserCreatedEvent domainEvent) {
        // The actually sending of email is omitted here
        log.info("Send email to admin after user[{}] created.", domainEvent.getName());
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }
}
