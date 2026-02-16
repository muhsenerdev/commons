package github.muhsenerdev.commons.web.response;

import java.time.Instant;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ErrorResponse {
    private final Instant timestamp;
    private final String path;
    private final String message;
    private final int status;
    private final Map<String, String> errors;
}
