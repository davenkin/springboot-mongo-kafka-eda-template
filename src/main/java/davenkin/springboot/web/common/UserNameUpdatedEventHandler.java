package davenkin.springboot.web.common;

import davenkin.springboot.web.user.domain.event.UserNameUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserNameUpdatedEventHandler extends AbstractDomainEventHandler<UserNameUpdatedEvent> {
  @Override
  protected void doHandle(UserNameUpdatedEvent domainEvent) {
    log.info("Received UserNameUpdatedEvent: {}", domainEvent.getId());
  }

  @Override
  public boolean isRepeatable() {
    return true;
  }
}
