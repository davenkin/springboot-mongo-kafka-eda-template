package davenkin.springboot.web.common.configuration;

import davenkin.springboot.web.common.configuration.profile.NonBuildProfile;
import davenkin.springboot.web.common.domainevent.publish.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@NonBuildProfile
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@EnableSchedulerLock(defaultLockAtMostFor = "60m", defaultLockAtLeastFor = "10s")
public class SchedulingConfiguration {
    private final DomainEventPublisher domainEventPublisher;

    @Scheduled(cron = "0 */1 * * * ?")
    public void houseKeepPublishDomainEvent() {
        log.debug("House keep publishing domain events.");
        domainEventPublisher.publishStagedDomainEvents();
    }
}
