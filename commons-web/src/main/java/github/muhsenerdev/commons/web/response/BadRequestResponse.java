package github.muhsenerdev.commons.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Schema(description = "Response returned for 400 Bad Request")
public class BadRequestResponse extends ErrorResponse {
}
