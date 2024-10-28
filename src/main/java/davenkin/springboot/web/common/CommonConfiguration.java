package davenkin.springboot.web.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;
import org.apache.kafka.common.serialization.Deserializer;
import org.bson.Document;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoManagedTypes;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListener;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.TransactionManager;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS;
import static davenkin.springboot.web.common.Constants.PUBLISHING_DOMAIN_EVENT_COLLECTION;
import static davenkin.springboot.web.common.Constants.SHEDLOCK_COLLECTION;

@Configuration
public class CommonConfiguration {

    // Make all @Persistent(including @Document and @TypeAlias) annotated class to be managed by Mongo,
    // otherwise only @Document annotated classes will be considered as the default behavior
    @Bean
    MongoManagedTypes mongoManagedTypes(ApplicationContext applicationContext) throws ClassNotFoundException {
        return MongoManagedTypes.fromIterable(new EntityScanner(applicationContext).scan(Persistent.class));
    }

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

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(500);
        executor.initialize();
        executor.setThreadNamePrefix("default-");
        return executor;
    }

    @Bean
    public LockProvider lockProvider(MongoTemplate mongoTemplate) {
        return new MongoLockProvider(mongoTemplate.getCollection(SHEDLOCK_COLLECTION));
    }

    @Bean
    public LockingTaskExecutor lockingTaskExecutor(LockProvider lockProvider) {
        return new DefaultLockingTaskExecutor(lockProvider);
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
    public DefaultKafkaConsumerFactoryCustomizer defaultKafkaConsumerFactoryCustomizer(DomainEventJsonDeserializer deserializer) {
        return producerFactory -> {
            producerFactory.setValueDeserializer((Deserializer) deserializer);
        };
    }

    // It's recommend to use @NonBuildProfile to disable this bean when doing a build
    // @NonBuildProfile
    @Bean(destroyMethod = "stop")
    MessageListenerContainer mongoDomainEventChangeStreamListenerContainer(MongoTemplate mongoTemplate,
                                                                           TaskExecutor taskExecutor,
                                                                           DomainEventPublisher domainEventPublisher) {
        MessageListenerContainer container = new DefaultMessageListenerContainer(mongoTemplate, taskExecutor);

        var listener = (MessageListener<ChangeStreamDocument<Document>, PublishingDomainEvent>) message -> {
            domainEventPublisher.publishStagedDomainEvents();
        };

        ChangeStreamRequest<? super PublishingDomainEvent> request = ChangeStreamRequest.builder(listener)
                .collection(PUBLISHING_DOMAIN_EVENT_COLLECTION)
                .filter(new Document("$match", new Document("operationType", OperationType.INSERT.getValue())))
                .build();

        container.register(request, PublishingDomainEvent.class);
        container.start();
        return container;
    }
}
