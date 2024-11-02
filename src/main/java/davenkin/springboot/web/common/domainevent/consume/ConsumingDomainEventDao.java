package davenkin.springboot.web.common.domainevent.consume;

import com.mongodb.client.result.UpdateResult;
import davenkin.springboot.web.common.domainevent.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static davenkin.springboot.web.common.Constants.MONGO_ID;
import static davenkin.springboot.web.common.domainevent.consume.ConsumingDomainEvent.Fields.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumingDomainEventDao<T extends DomainEvent> {
    private final MongoTemplate mongoTemplate;

    public boolean recordAsConsumed(ConsumingDomainEvent<T> consumingDomainEvent) {
        Query query = query(where(MONGO_ID).is(consumingDomainEvent.getId()));

        Update update = new Update()
                .setOnInsert(arId, consumingDomainEvent.getArId())
                .setOnInsert(type, consumingDomainEvent.getType())
                .setOnInsert(consumedAt, Instant.now());

        UpdateResult result = this.mongoTemplate.update(ConsumingDomainEvent.class)
                .matching(query)
                .apply(update)
                .upsert();

        return result.getMatchedCount() == 0;
    }
}