package davenkin.springboot.web.department;

import davenkin.springboot.web.common.AggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

import static davenkin.springboot.web.common.CommonUtils.requireNonBlank;
import static davenkin.springboot.web.common.SnowflakeIdGenerator.newSnowflakeId;
import static davenkin.springboot.web.department.Department.DEPARTMENT_AR_NAME;
import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldNameConstants
@Document(DEPARTMENT_AR_NAME)
@TypeAlias(DEPARTMENT_AR_NAME)
@NoArgsConstructor(access = PRIVATE)
public class Department extends AggregateRoot {
    public static final String DEPARTMENT_AR_NAME = "department";

    private String name;
    private List<String> userIds;

    public Department(String name) {
        super(newDepartmentId());
        this.name = name;
        this.userIds = new ArrayList<>();
        this.raiseEvent(new DepartmentCreatedEvent(name));
    }

    public static String newDepartmentId() {
        return "DPT" + newSnowflakeId();
    }

    public void addUser(String userId) {
        requireNonBlank(userId, "User ID must not be blank.");
        this.userIds.add(userId);
        this.raiseEvent(new DepartmentUserAddedEvent(userId));
    }

}
