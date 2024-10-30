package davenkin.springboot.web.common;

import static davenkin.springboot.web.common.Constants.MONGO_ID;
import static davenkin.springboot.web.common.ConsumingDomainEvent.Fields.arId;
import static davenkin.springboot.web.common.ConsumingDomainEvent.Fields.consumedAt;
import static davenkin.springboot.web.common.ConsumingDomainEvent.Fields.type;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.Instant;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumingDomainEventDao {
  private final MongoTemplate mongoTemplate;

  public boolean checkNotConsumed(DomainEvent domainEvent) {
    Query query = query(where(MONGO_ID).is(domainEvent.getId()));

    Update update = new Update()
        .setOnInsert(arId, domainEvent.getArId())
        .setOnInsert(type, domainEvent.getType())
        .setOnInsert(consumedAt, Instant.now());

    UpdateResult result = this.mongoTemplate.update(ConsumingDomainEvent.class)
        .matching(query)
        .apply(update)
        .upsert();

    return result.getMatchedCount() == 0;
  }
}
