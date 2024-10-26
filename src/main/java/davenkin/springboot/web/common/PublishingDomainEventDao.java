package davenkin.springboot.web.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

import static davenkin.springboot.web.common.CommonUtils.requireNonBlank;
import static davenkin.springboot.web.common.Constants.MONGO_ID;
import static davenkin.springboot.web.common.DomainEventPublishStatus.*;
import static davenkin.springboot.web.common.PublishingDomainEvent.Fields.*;
import static java.util.Objects.requireNonNull;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishingDomainEventDao {
    private final MongoTemplate mongoTemplate;

    public void stage(List<DomainEvent> events) {
        requireNonNull(events, "Domain events must not be null.");
        List<PublishingDomainEvent> publishingDomainEvents = events.stream().map(PublishingDomainEvent::new).toList();
        mongoTemplate.insertAll(publishingDomainEvents);
    }

    public List<DomainEvent> stagedEvents(String startId, int limit) {
        requireNonBlank(startId, "Start ID must not be blank.");

        Query query = query(where(status).in(CREATED, PUBLISH_FAILED)
                .and(MONGO_ID).gt(startId)
                .and(publishedCount).lt(3))
                .with(by(ASC, raisedAt))
                .limit(limit);
        return mongoTemplate.find(query, PublishingDomainEvent.class).stream().map(PublishingDomainEvent::getEvent).toList();
    }


    public void successPublish(String eventId) {
        requireNonBlank(eventId, "Domain event ID must not be blank.");
        Query query = Query.query(where(MONGO_ID).is(eventId));
        Update update = new Update();
        update.set(status, PUBLISH_SUCCEED.name()).inc(publishedCount);
        mongoTemplate.updateFirst(query, update, PublishingDomainEvent.class);
    }

    public void failPublish(String eventId) {
        requireNonBlank(eventId, "Domain event ID must not be blank.");
        Query query = Query.query(where(MONGO_ID).is(eventId));
        Update update = new Update();
        update.set(status, PUBLISH_FAILED.name()).inc(publishedCount);
        mongoTemplate.updateFirst(query, update, PublishingDomainEvent.class);
    }

}
