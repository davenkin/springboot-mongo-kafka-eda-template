package davenkin.springboot.web.common;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static davenkin.springboot.web.common.Constants.MONGO_ID;
import static org.springframework.data.mongodb.core.ReplaceOptions.replaceOptions;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumingDomainEventDao {
    private final MongoTemplate mongoTemplate;

    public boolean checkNotConsumed(DomainEvent domainEvent) {
        ConsumingDomainEvent consumingDomainEvent = new ConsumingDomainEvent(domainEvent);
        Query query = query(where(MONGO_ID).is(consumingDomainEvent.getId()));
        UpdateResult result = this.mongoTemplate.replace(query, consumingDomainEvent, replaceOptions().upsert());
        return result.getMatchedCount() == 0;
    }
}
