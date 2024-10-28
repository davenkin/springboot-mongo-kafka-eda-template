package davenkin.springboot.web.user.domain.event;

import davenkin.springboot.web.common.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

import static davenkin.springboot.web.common.DomainEventType.USER_CREATED;
import static lombok.AccessLevel.PRIVATE;

@Getter
@TypeAlias("USER_CREATED_EVENT")
@NoArgsConstructor(access = PRIVATE)
public class UserCreatedEvent extends DomainEvent {
    private String name;

    public UserCreatedEvent(String name) {
        super(USER_CREATED);
        this.name = name;
    }
}
