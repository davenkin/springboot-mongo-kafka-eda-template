package davenkin.springboot.web.common;

import davenkin.springboot.web.user.domain.User;
import davenkin.springboot.web.user.domain.event.UserCreatedEvent;
import davenkin.springboot.web.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventHandler extends AbstractTransactionalDomainEventHandler<UserCreatedEvent> {
  private final UserRepository userRepository;

  @Override
  protected void doHandle(UserCreatedEvent domainEvent) {
    User user = this.userRepository.byId(domainEvent.getArId());
    log.info("Received UserCreatedEvent for user: {}", user.getName());
  }
}
