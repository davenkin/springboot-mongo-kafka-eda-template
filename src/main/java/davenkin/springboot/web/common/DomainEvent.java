package davenkin.springboot.web.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import davenkin.springboot.web.department.DepartmentCreatedEvent;
import davenkin.springboot.web.department.DepartmentUserAddedEvent;
import davenkin.springboot.web.user.domain.event.UserCreatedEvent;
import davenkin.springboot.web.user.domain.event.UserNameUpdatedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import static davenkin.springboot.web.common.DomainEvent.DOMAIN_EVENT_COLLECTION_NAME;
import static davenkin.springboot.web.common.SnowflakeIdGenerator.newSnowflakeId;
import static lombok.AccessLevel.PROTECTED;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = UserCreatedEvent.class, name = "USER_CREATED"),
        @JsonSubTypes.Type(value = UserNameUpdatedEvent.class, name = "USER_NAME_UPDATED"),
        @JsonSubTypes.Type(value = DepartmentCreatedEvent.class, name = "DEPARTMENT_CREATED"),
        @JsonSubTypes.Type(value = DepartmentUserAddedEvent.class, name = "DEPARTMENT_USER_ADDED"),
})

@Getter
@Document(DOMAIN_EVENT_COLLECTION_NAME)
@NoArgsConstructor(access = PROTECTED)
@FieldNameConstants
public abstract class DomainEvent {
    public static final String DOMAIN_EVENT_COLLECTION_NAME = "domain_event";

    private String id;
    private String arId;
    private DomainEventType type;
    private Instant raisedAt;

    protected DomainEvent(DomainEventType type) {
        this.id = newEventId();
        this.type = type;
        this.raisedAt = Instant.now();
    }

    void setArId(String arId) {
        this.arId = arId;
    }

    public static String newEventId() {
        return "EVT" + newSnowflakeId();
    }
}
