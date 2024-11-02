package davenkin.springboot.web.department;

import davenkin.springboot.web.common.domainevent.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

import static davenkin.springboot.web.common.domainevent.DomainEventType.DEPARTMENT_USER_ADDED;
import static lombok.AccessLevel.PRIVATE;

@Getter
@TypeAlias("DEPARTMENT_USER_ADDED_EVENT")
@NoArgsConstructor(access = PRIVATE)
public class DepartmentUserAddedEvent extends DomainEvent {
    private String userId;

    public DepartmentUserAddedEvent(String userId, String departmentId) {
        super(DEPARTMENT_USER_ADDED, departmentId);
        this.userId = userId;
    }
}
