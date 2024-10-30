package davenkin.springboot.web.user.domain.event;

import static davenkin.springboot.web.common.DomainEventType.USER_NAME_UPDATED;
import static lombok.AccessLevel.PRIVATE;

import davenkin.springboot.web.common.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

@Getter
@TypeAlias("USER_NAME_UPDATED_EVENT")
@NoArgsConstructor(access = PRIVATE)
public class UserNameUpdatedEvent extends DomainEvent {
  private String oldName;
  private String newName;

  public UserNameUpdatedEvent(String oldName, String newName, String userId) {
    super(USER_NAME_UPDATED, userId);
    this.oldName = oldName;
    this.newName = newName;
  }
}
