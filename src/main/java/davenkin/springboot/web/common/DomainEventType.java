package davenkin.springboot.web.common;

import static davenkin.springboot.web.common.AggregateRootType.DEPARTMENT;
import static davenkin.springboot.web.common.AggregateRootType.USER;

public enum DomainEventType {
    USER_CREATED(USER),
    USER_NAME_UPDATED(USER),
    DEPARTMENT_CREATED(DEPARTMENT),
    DEPARTMENT_USER_ADDED(DEPARTMENT);

    private final AggregateRootType arType;

    DomainEventType(AggregateRootType arType) {
        this.arType = arType;
    }

    public AggregateRootType getArType() {
        return arType;
    }
}
