package davenkin.springboot.web.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DomainEventJsonDeserializer implements Deserializer<DomainEvent> {
    private final ObjectMapper objectMapper;

    @Override
    public DomainEvent deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, DomainEvent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
