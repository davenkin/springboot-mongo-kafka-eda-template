package davenkin.springboot.web.user;

import davenkin.springboot.web.common.ResponseId;
import davenkin.springboot.web.user.command.CreateUserCommand;
import davenkin.springboot.web.user.command.UpdateUserNameCommand;
import davenkin.springboot.web.user.command.UserCommandService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserCommandService userCommandService;

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseId createUser(@RequestBody @Valid CreateUserCommand createUserCommand) {
        String userId = this.userCommandService.createUser(createUserCommand.name());
        return new ResponseId(userId);
    }

    @PutMapping(value = "/{userId}/name")
    public void updateUserName(@PathVariable("userId") @NotBlank String userId,
                               @RequestBody @Valid UpdateUserNameCommand updateUserNameCommand) {
        this.userCommandService.updateUserName(userId, updateUserNameCommand.name());
    }
}
