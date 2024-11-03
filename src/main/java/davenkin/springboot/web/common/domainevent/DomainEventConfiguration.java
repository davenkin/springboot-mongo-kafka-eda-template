package davenkin.springboot.web.common.domainevent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import davenkin.springboot.web.common.configuration.profile.NonBuildProfile;
import davenkin.springboot.web.common.domainevent.publish.DomainEventPublisher;
import davenkin.springboot.web.common.domainevent.publish.PublishingDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.bson.Document;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListener;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.ExponentialBackOff;

import static davenkin.springboot.web.common.Constants.PUBLISHING_DOMAIN_EVENT_COLLECTION;

@NonBuildProfile
@Slf4j
@Configuration
public class DomainEventConfiguration {

    @Bean(destroyMethod = "stop")
    MessageListenerContainer mongoDomainEventChangeStreamListenerContainer(MongoTemplate mongoTemplate,
                                                                           TaskExecutor taskExecutor,
                                                                           DomainEventPublisher domainEventPublisher) {
        MessageListenerContainer container = new DefaultMessageListenerContainer(mongoTemplate, taskExecutor);

        // Get notification on DomainEvent insert and publish staged domain events
        container.register(ChangeStreamRequest.builder(
                        (MessageListener<ChangeStreamDocument<Document>, PublishingDomainEvent>) message -> {
                            domainEventPublisher.publishStagedDomainEvents();
                        })
                .collection(PUBLISHING_DOMAIN_EVENT_COLLECTION)
                .filter(new Document("$match", new Document("operationType", OperationType.INSERT.getValue())))
                .build(), PublishingDomainEvent.class);
        container.start();
        return container;
    }

    @Bean
    public DefaultKafkaProducerFactoryCustomizer defaultKafkaProducerFactoryCustomizer(ObjectMapper objectMapper) {
        return producerFactory -> {
            JsonSerializer valueSerializer = new JsonSerializer(objectMapper);

            // Do not add __TypeId__ in the header as we don't want to rely on Kafka for later deserialization hence decrease coupling to Kafka
            valueSerializer.setAddTypeInfo(false);

            producerFactory.setValueSerializer(valueSerializer);
        };
    }


    @Bean
    public DefaultKafkaConsumerFactoryCustomizer defaultKafkaConsumerFactoryCustomizer(ObjectMapper objectMapper) {
        return producerFactory -> {
            // don't use type info in Kafka headers for deserialization, but the uses the Jackson annotations(@JsonTypeInfo) on DomainEvent itself to decrease coupling to Kafka
            // also as by default Kafka used full class name in the header, not using this make our code more refactor friendly as we can not freely change the event's package
            Deserializer valueDeserializer = new JsonDeserializer<>(DomainEvent.class, objectMapper, false);

            //we must wrap the JsonDeserializer into an ErrorHandlingDeserializer, otherwise deserialization error will result in endless message retry
            ErrorHandlingDeserializer deserializer = new ErrorHandlingDeserializer(valueDeserializer);
            producerFactory.setValueDeserializer(deserializer);
        };
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        ExponentialBackOff exponentialBackOff = new ExponentialBackOff(1000L, 2);
        exponentialBackOff.setMaxAttempts(2); // the message will be delivered 2 + 1 = 3 times
        return new DefaultErrorHandler(exponentialBackOff);
    }
}
