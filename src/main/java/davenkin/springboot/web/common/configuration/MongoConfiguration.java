package davenkin.springboot.web.common.configuration;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import davenkin.springboot.web.common.domainevent.publish.DomainEventPublisher;
import davenkin.springboot.web.common.domainevent.publish.PublishingDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.transaction.TransactionManager;

import static davenkin.springboot.web.common.Constants.PUBLISHING_DOMAIN_EVENT_COLLECTION;

@Slf4j
@Configuration
public class MongoConfiguration {

    // Make all @Persistent(including @Document and @TypeAlias) annotated class to be managed by Mongo,
    // otherwise only @Document annotated classes will be considered as the default behavior
    @Bean
    MongoManagedTypes mongoManagedTypes(ApplicationContext applicationContext) throws ClassNotFoundException {
        return MongoManagedTypes.fromIterable(new EntityScanner(applicationContext).scan(Persistent.class));
    }

    @Bean
    public TransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    // Normally we use @NonBuildProfile to disable it for build pipeline
    @NonBuildProfile
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

}
