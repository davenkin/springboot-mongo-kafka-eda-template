package davenkin.springboot.web;

import davenkin.springboot.web.common.domainevent.DomainEvent;
import davenkin.springboot.web.common.domainevent.DomainEventType;
import davenkin.springboot.web.common.domainevent.publish.PublishingDomainEvent;
import davenkin.springboot.web.common.domainevent.publish.PublishingDomainEventDao;
import davenkin.springboot.web.department.DepartmentApplicationService;
import davenkin.springboot.web.user.command.UserCommandService;
import davenkin.springboot.web.user.infrastructure.UserRepository;
import org.junit.jupiter.api.parallel.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static davenkin.springboot.web.common.utils.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@ActiveProfiles("build")
@Execution(CONCURRENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class BaseApiTest {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PublishingDomainEventDao publishingDomainEventDao;

    @Autowired
    protected UserCommandService userCommandService;

    @Autowired
    protected DepartmentApplicationService departmentApplicationService;

    protected <T extends DomainEvent> T latestEventFor(String arId, DomainEventType type, Class<T> eventClass) {
        requireNonBlank(arId, "AR ID must not be blank.");
        requireNonNull(type, "Domain event type must not be null.");
        requireNonNull(eventClass, "Domain event class must not be null.");

        Query query = query(where(PublishingDomainEvent.Fields.event + "." + DomainEvent.Fields.arId).is(arId)
                .and(PublishingDomainEvent.Fields.event + "." + DomainEvent.Fields.type).is(type))
                .with(by(DESC, PublishingDomainEvent.Fields.raisedAt));
        return (T) mongoTemplate.findOne(query, PublishingDomainEvent.class).getEvent();
    }
}
