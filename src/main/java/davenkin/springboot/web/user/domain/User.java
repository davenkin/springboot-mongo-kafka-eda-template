package davenkin.springboot.web.user.domain;

import davenkin.springboot.web.common.AggregateRoot;
import davenkin.springboot.web.user.domain.event.UserCreatedEvent;
import davenkin.springboot.web.user.domain.event.UserNameUpdatedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import static davenkin.springboot.web.common.SnowflakeIdGenerator.newSnowflakeId;
import static davenkin.springboot.web.user.domain.User.USER_COLLECTION_NAME;
import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldNameConstants
@Document(USER_COLLECTION_NAME)
@TypeAlias(USER_COLLECTION_NAME)
@NoArgsConstructor(access = PRIVATE)
public class User extends AggregateRoot {
    public static final String USER_COLLECTION_NAME = "user";

    private String name;

    public User(String name) {
        super(newUserId());
        this.name = name;
        raiseEvent(new UserCreatedEvent(name));
    }

    public static String newUserId() {
        return "USR" + newSnowflakeId();
    }

    public void updateName(String newName) {
        UserNameUpdatedEvent userNameUpdatedEvent = new UserNameUpdatedEvent(name, newName);
        this.name = newName;
        raiseEvent(userNameUpdatedEvent);
    }
}
