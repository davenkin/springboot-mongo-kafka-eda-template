package davenkin.springboot.web.about;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AboutController {
    private final ZonedDateTime deployTime = now();

    @GetMapping(value = "/about")
    public QAboutInfo about() {
        String deployTime = this.deployTime.toString();

        return QAboutInfo.builder()
                .deployTime(deployTime)
                .build();
    }
}
