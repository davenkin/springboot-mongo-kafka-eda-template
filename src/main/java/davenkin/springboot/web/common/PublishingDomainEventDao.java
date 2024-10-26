package davenkin.springboot.web.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static davenkin.springboot.web.common.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishingDomainEventDao {
    private final MongoTemplate mongoTemplate;

    public void insert(List<DomainEvent> events) {
        requireNonNull(events, "Domain events must not be null.");

        mongoTemplate.insertAll(events);
    }


    public <T extends DomainEvent> T latestEventFor(String arId, DomainEventType type, Class<T> eventClass) {
        requireNonBlank(arId, "AR ID must not be blank.");
        requireNonNull(type, "Domain event type must not be null.");
        requireNonNull(eventClass, "Domain event class must not be null.");

        Query query = query(where(DomainEvent.Fields.arId).is(arId)
                .and(DomainEvent.Fields.type).is(type))
                .with(by(DESC, DomainEvent.Fields.raisedAt));
        return mongoTemplate.findOne(query, eventClass);
    }

}
