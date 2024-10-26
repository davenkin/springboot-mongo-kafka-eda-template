package davenkin.springboot.web.user.domain.event;

import davenkin.springboot.web.common.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

import static davenkin.springboot.web.common.DomainEventType.USER_NAME_UPDATED;
import static lombok.AccessLevel.PRIVATE;

@Getter
@TypeAlias("USER_NAME_UPDATED_EVENT")
@NoArgsConstructor(access = PRIVATE)
public class UserNameUpdatedEvent extends DomainEvent {
    private String oldName;
    private String newName;

    public UserNameUpdatedEvent(String oldName, String newName) {
        super(USER_NAME_UPDATED);
        this.oldName = oldName;
        this.newName = newName;
    }

}
