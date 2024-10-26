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
import static davenkin.springboot.web.common.DomainEventPublishStatus.PUBLISH_FAILED;
import static davenkin.springboot.web.common.DomainEventPublishStatus.PUBLISH_SUCCEED;
import static davenkin.springboot.web.common.PublishingDomainEvent.Fields.publishedCount;
import static davenkin.springboot.web.common.PublishingDomainEvent.Fields.status;
import static java.util.Objects.requireNonNull;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishingDomainEventDao {
    private final MongoTemplate mongoTemplate;

    public void insert(List<DomainEvent> events) {
        requireNonNull(events, "Domain events must not be null.");
        List<PublishingDomainEvent> publishingDomainEvents = events.stream().map(PublishingDomainEvent::new).toList();
        mongoTemplate.insertAll(publishingDomainEvents);
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
