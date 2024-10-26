package davenkin.springboot.web;

import davenkin.springboot.web.common.PublishingDomainEventDao;
import davenkin.springboot.web.user.command.UserCommandService;
import davenkin.springboot.web.user.infrastructure.UserRepository;
import org.junit.jupiter.api.parallel.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("build")
@Execution(CONCURRENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class BaseApiTest {
    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PublishingDomainEventDao publishingDomainEventDao;

    @Autowired
    protected UserCommandService userCommandService;
}
