package davenkin.springboot.web.department;

import static davenkin.springboot.web.common.DomainEventType.DEPARTMENT_CREATED;
import static lombok.AccessLevel.PRIVATE;

import davenkin.springboot.web.common.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

@Getter
@TypeAlias("DEPARTMENT_CREATED_EVENT")
@NoArgsConstructor(access = PRIVATE)
public class DepartmentCreatedEvent extends DomainEvent {
  private String name;

  public DepartmentCreatedEvent(String name, String departmentId) {
    super(DEPARTMENT_CREATED, departmentId);
    this.name = name;
  }
}
