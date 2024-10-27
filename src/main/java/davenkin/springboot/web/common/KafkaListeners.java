package davenkin.springboot.web.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaListeners {
    private final ObjectMapper objectMapper;

    @KafkaListener(id = "myId", topics = "user_domain_event")
    public void listen(String event) throws JsonProcessingException {
        DomainEvent domainEvent = objectMapper.readValue(event, DomainEvent.class);
        log.info("Recir: {} {}", domainEvent.getId(), domainEvent.getType());
    }
}
