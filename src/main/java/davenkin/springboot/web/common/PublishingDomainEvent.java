package davenkin.springboot.web.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import static davenkin.springboot.web.common.DomainEventPublishStatus.CREATED;
import static davenkin.springboot.web.common.PublishingDomainEvent.PUBLISHING_DOMAIN_EVENT_COLLECTION_NAME;
import static lombok.AccessLevel.PRIVATE;


@Getter
@FieldNameConstants
@NoArgsConstructor(access = PRIVATE)
@TypeAlias(PUBLISHING_DOMAIN_EVENT_COLLECTION_NAME)
@Document(PUBLISHING_DOMAIN_EVENT_COLLECTION_NAME)
public class PublishingDomainEvent {
    public static final String PUBLISHING_DOMAIN_EVENT_COLLECTION_NAME = "publishing_domain_event";

    private String id;
    private DomainEvent event;

    private DomainEventPublishStatus status;

    private int publishedCount;

    public PublishingDomainEvent(DomainEvent event) {
        this.id = event.getId();
        this.event = event;
        this.status = CREATED;
        this.publishedCount = 0;
    }
}
