package davenkin.springboot.web.common;

import static davenkin.springboot.web.department.Department.DEPARTMENT_AR_NAME;
import static davenkin.springboot.web.user.domain.User.USER_AR_NAME;

public enum DomainEventType {
    USER_CREATED(USER_AR_NAME),
    USER_NAME_UPDATED(USER_AR_NAME),
    DEPARTMENT_CREATED(DEPARTMENT_AR_NAME),
    DEPARTMENT_USER_ADDED(DEPARTMENT_AR_NAME);

    private final String arName;

    DomainEventType(String arName) {
        this.arName = arName;
    }

    public String getArName() {
        return arName;
    }
}
