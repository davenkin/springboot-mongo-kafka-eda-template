package davenkin.springboot.web.department;

import static davenkin.springboot.web.common.DomainEventType.DEPARTMENT_USER_ADDED;
import static lombok.AccessLevel.PRIVATE;

import davenkin.springboot.web.common.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

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
