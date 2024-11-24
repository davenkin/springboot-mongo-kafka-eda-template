package davenkin.springboot.web.common.domainevent.consume;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static davenkin.springboot.web.common.domainevent.consume.ConsumingDomainEvent.Fields.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

// Upon consuming, record the event in DB to avoid duplicated event handling
@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumingDomainEventDao<T> {
    private final MongoTemplate mongoTemplate;

    // return true means this event has never been consumed before
    public boolean recordAsConsumed(ConsumingDomainEvent<T> consumingDomainEvent, String handlerName) {
        Query query = query(where(eventId).is(consumingDomainEvent.getEventId()).and(ConsumingDomainEvent.Fields.handlerName).is(handlerName));

        Update update = new Update()
                .setOnInsert(type, consumingDomainEvent.getType())
                .setOnInsert(consumedAt, Instant.now());

        UpdateResult result = this.mongoTemplate.update(ConsumingDomainEvent.class)
                .matching(query)
                .apply(update)
                .upsert();

        return result.getMatchedCount() == 0;
    }
}
