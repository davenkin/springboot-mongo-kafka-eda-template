package davenkin.springboot.web.common.domainevent;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import davenkin.springboot.web.common.model.AggregateRootType;
import davenkin.springboot.web.department.domain.event.DepartmentCreatedEvent;
import davenkin.springboot.web.user.domain.event.UserCreatedEvent;
import davenkin.springboot.web.user.domain.event.UserNameUpdatedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;

import static davenkin.springboot.web.common.utils.CommonUtils.requireNonBlank;
import static davenkin.springboot.web.common.utils.SnowflakeIdGenerator.newSnowflakeId;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PROTECTED;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = UserCreatedEvent.class, name = "USER_CREATED"),
        @JsonSubTypes.Type(value = UserNameUpdatedEvent.class, name = "USER_NAME_UPDATED"),
        @JsonSubTypes.Type(value = DepartmentCreatedEvent.class, name = "DEPARTMENT_CREATED"),
})

@Getter
@FieldNameConstants
@NoArgsConstructor(access = PROTECTED)
public abstract class DomainEvent {

    private String id;
    private String arId;
    private DomainEventType type;

    private AggregateRootType arType;

    private Instant raisedAt;

    protected DomainEvent(DomainEventType type, String arId) {
        requireNonNull(type, "Domain event type must not be null.");
        requireNonBlank(arId, "Domain event aggregate root ID must not be null.");

        this.id = newEventId();
        this.arId = arId;
        this.type = type;
        this.arType = type.getArType();
        this.raisedAt = Instant.now();
    }

    public static String newEventId() {
        return "EVT" + newSnowflakeId();
    }
}
