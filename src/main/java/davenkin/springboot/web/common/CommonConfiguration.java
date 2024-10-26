package davenkin.springboot.web.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import org.bson.Document;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListener;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.transaction.TransactionManager;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS;
import static davenkin.springboot.web.common.PublishingDomainEvent.PUBLISHING_DOMAIN_EVENT_COLLECTION_NAME;

@Configuration
public class CommonConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomizer() {
        return builder -> {
            builder.visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                    .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                    .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS, WRITE_DURATIONS_AS_TIMESTAMPS, FAIL_ON_UNKNOWN_PROPERTIES);
        };
    }

    @Bean
    public TransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Bean(destroyMethod = "stop")
    MessageListenerContainer mongoDomainEventChangeStreamListenerContainer(MongoTemplate mongoTemplate) {
        MessageListenerContainer container = new DefaultMessageListenerContainer(mongoTemplate);

        var listener = (MessageListener<ChangeStreamDocument<Document>, PublishingDomainEvent>) message -> {
            System.out.println(message.getBody());
        };

        ChangeStreamRequest<? super PublishingDomainEvent> request = ChangeStreamRequest.builder(listener)
                .collection(PUBLISHING_DOMAIN_EVENT_COLLECTION_NAME)
                .filter(new Document("$match", new Document("operationType", OperationType.INSERT.getValue())))
                .build();

        container.register(request, PublishingDomainEvent.class);
        container.start();
        return container;
    }
}
