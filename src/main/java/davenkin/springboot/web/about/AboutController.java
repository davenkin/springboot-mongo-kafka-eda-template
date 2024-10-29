package davenkin.springboot.web.about;

import davenkin.springboot.web.user.command.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AboutController {
    private final Instant deployTime = Instant.now();
    private final UserCommandService userCommandService;

    @GetMapping(value = "/about")
    public QAboutInfo about() {
        String deployTime = this.deployTime.toString();
        this.userCommandService.createUser("OLD NAME");
        return QAboutInfo.builder()
                .deployTime(deployTime)
                .build();
    }
}
