package davenkin.springboot.web.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Profile("!build")
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@EnableSchedulerLock(defaultLockAtMostFor = "60m", defaultLockAtLeastFor = "10s")
public class SchedulingConfiguration {
    private final DomainEventPublisher domainEventPublisher;

    @Scheduled(cron = "0 */2 * * * ?")
    public void houseKeepPublishDomainEvent() {
        log.debug("House keep publishing domain events.");
        domainEventPublisher.publishStagedDomainEvents();
    }
}
