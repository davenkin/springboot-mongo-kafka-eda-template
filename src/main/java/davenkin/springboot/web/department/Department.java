package davenkin.springboot.web.department;

import davenkin.springboot.web.common.model.AggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

import static davenkin.springboot.web.common.Constants.DEPARTMENT_COLLECTION;
import static davenkin.springboot.web.common.model.AggregateRootType.DEPARTMENT;
import static davenkin.springboot.web.common.utils.CommonUtils.requireNonBlank;
import static davenkin.springboot.web.common.utils.SnowflakeIdGenerator.newSnowflakeId;
import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldNameConstants
@Document(DEPARTMENT_COLLECTION)
@TypeAlias("DEPARTMENT")
@NoArgsConstructor(access = PRIVATE)
public class Department extends AggregateRoot {

    private String name;
    private List<String> userIds;

    public Department(String name) {
        super(newDepartmentId(), DEPARTMENT);
        this.name = name;
        this.userIds = new ArrayList<>();
        this.raiseEvent(new DepartmentCreatedEvent(name, this.getId()));
    }

    public static String newDepartmentId() {
        return "DPT" + newSnowflakeId();
    }

    public void addUser(String userId) {
        requireNonBlank(userId, "User ID must not be blank.");
        this.userIds.add(userId);
        this.raiseEvent(new DepartmentUserAddedEvent(userId, this.getId()));
    }
}
