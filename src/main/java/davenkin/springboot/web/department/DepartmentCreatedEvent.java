package davenkin.springboot.web.department;

import davenkin.springboot.web.common.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

import static davenkin.springboot.web.common.DomainEventType.DEPARTMENT_CREATED;
import static lombok.AccessLevel.PRIVATE;

@Getter
@TypeAlias("department_created_event")
@NoArgsConstructor(access = PRIVATE)
public class DepartmentCreatedEvent extends DomainEvent {
    private String name;

    public DepartmentCreatedEvent(String name) {
        super(DEPARTMENT_CREATED);
        this.name = name;
    }
}
