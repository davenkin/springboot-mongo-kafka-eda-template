package davenkin.springboot.web.user.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateUserCommand(@NotBlank @Size(max = 100) String name) {
}
