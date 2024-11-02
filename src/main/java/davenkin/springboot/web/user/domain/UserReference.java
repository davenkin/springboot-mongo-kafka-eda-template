package davenkin.springboot.web.user.domain;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@Builder
@FieldNameConstants
public record UserReference(String id, String name) {
}
