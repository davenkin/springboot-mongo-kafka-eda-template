package davenkin.springboot.web.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS;

@Configuration
public class CommonConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomizer() {
        return builder -> {
            builder.visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                    .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                    .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS, WRITE_DURATIONS_AS_TIMESTAMPS, FAIL_ON_UNKNOWN_PROPERTIES);
        };
    }
}
