package davenkin.springboot.web.user;

import davenkin.springboot.web.BaseApiTest;
import davenkin.springboot.web.common.ResponseId;
import davenkin.springboot.web.user.command.CreateUserCommand;
import davenkin.springboot.web.user.command.UpdateUserNameCommand;
import davenkin.springboot.web.user.domain.User;
import davenkin.springboot.web.user.domain.event.UserCreatedEvent;
import davenkin.springboot.web.user.domain.event.UserNameUpdatedEvent;
import org.junit.jupiter.api.Test;

import static davenkin.springboot.web.common.DomainEventType.USER_CREATED;
import static davenkin.springboot.web.common.DomainEventType.USER_NAME_UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

public class UserControllerApiTest extends BaseApiTest {

    @Test
    public void shouldCreateUser() {
        CreateUserCommand createUserCommand = CreateUserCommand.builder().name("davenkin").build();
        ResponseId responseId = this.webTestClient.post()
                .uri("/users")
                .body(fromValue(createUserCommand))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(ResponseId.class)
                .returnResult()
                .getResponseBody();
        User user = userRepository.byId(responseId.id());
        assertEquals(createUserCommand.name(), user.getName());

        UserCreatedEvent userCreatedEvent = latestEventFor(user.getId(), USER_CREATED, UserCreatedEvent.class);
        assertEquals(user.getId(), userCreatedEvent.getArId());
    }

    @Test
    public void shouldUpdateUserName() {
        String userId = this.userCommandService.createUser("davenkin1");
        UpdateUserNameCommand updateUserNameCommand = UpdateUserNameCommand.builder().name("davenkin2").build();
        this.webTestClient.put()
                .uri("/users/{userId}/name", userId)
                .body(fromValue(updateUserNameCommand))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
        User user = userRepository.byId(userId);
        assertEquals(updateUserNameCommand.name(), user.getName());
        UserNameUpdatedEvent userNameUpdatedEvent = latestEventFor(user.getId(), USER_NAME_UPDATED, UserNameUpdatedEvent.class);
        assertEquals(updateUserNameCommand.name(), userNameUpdatedEvent.getNewName());
        assertEquals("davenkin1", userNameUpdatedEvent.getOldName());
    }
}
