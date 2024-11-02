package davenkin.springboot.web.common.domainevent.publish;

import davenkin.springboot.web.common.domainevent.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import static davenkin.springboot.web.common.Constants.PUBLISHING_DOMAIN_EVENT_COLLECTION;
import static davenkin.springboot.web.common.domainevent.publish.DomainEventPublishStatus.CREATED;
import static lombok.AccessLevel.PRIVATE;


@Getter
@FieldNameConstants
@NoArgsConstructor(access = PRIVATE)
@Document(PUBLISHING_DOMAIN_EVENT_COLLECTION)
@TypeAlias("PUBLISHING_DOMAIN_EVENT")
public class PublishingDomainEvent {

    private String id;
    private DomainEvent event;

    private DomainEventPublishStatus status;

    private int publishedCount;

    private Instant raisedAt;

    public PublishingDomainEvent(DomainEvent event) {
        this.id = event.getId();
        this.event = event;
        this.status = CREATED;
        this.publishedCount = 0;
        this.raisedAt = event.getRaisedAt();
    }
}
