package github.muhsenerdev.commons.web.response;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Schema(description = "Response returned for 401 Unauthorized")
public class UnauthorizedResponse extends ErrorResponse {

    public static UnauthorizedResponse of(String path, String message, Map<String, String> errors) {
        return UnauthorizedResponse.builder()
                .path(path)
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .errors(errors)
                .build();
    }
}
