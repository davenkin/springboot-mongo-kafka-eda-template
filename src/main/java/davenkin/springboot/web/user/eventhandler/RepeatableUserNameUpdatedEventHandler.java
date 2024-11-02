package davenkin.springboot.web.user.eventhandler;

import davenkin.springboot.web.common.domainevent.consume.AbstractTransactionalDomainEventHandler;
import davenkin.springboot.web.department.domain.DepartmentRepository;
import davenkin.springboot.web.user.domain.User;
import davenkin.springboot.web.user.domain.UserRepository;
import davenkin.springboot.web.user.domain.event.UserNameUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * We are updating DB so need to use AbstractTransactionalDomainEventHandler.
 * Here we are always fetching the latest username from DB to update the department creator name,
 * hence it can be run repeatedly
 */

@Slf4j
//@Component
@RequiredArgsConstructor
public class RepeatableUserNameUpdatedEventHandler extends AbstractTransactionalDomainEventHandler<UserNameUpdatedEvent> {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    // Update creator name for all departments created by this user
    @Override
    protected void doHandle(UserNameUpdatedEvent domainEvent) {
        //Fetch the latest username from DB
        User user = this.userRepository.byId(domainEvent.getArId());

        this.departmentRepository.updateAllDepartmentsCreatorName(domainEvent.getArId(), user.getName());
    }

    @Override
    public boolean isRepeatable() {
        return true; // This handler can be run repeatedly as we are using the latest username every time the event happens even with out-of-order events
    }
}
