package davenkin.springboot.web.department;

import davenkin.springboot.web.common.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

import static davenkin.springboot.web.common.DomainEventType.DEPARTMENT_USER_ADDED;
import static lombok.AccessLevel.PRIVATE;

@Getter
@TypeAlias("department_user_added_event")
@NoArgsConstructor(access = PRIVATE)
public class DepartmentUserAddedEvent extends DomainEvent {
    private String userId;

    public DepartmentUserAddedEvent(String userId) {
        super(DEPARTMENT_USER_ADDED);
        this.userId = userId;
    }
}
