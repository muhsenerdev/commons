package github.muhsenerdev.commons.web.response;

import java.time.Instant;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Schema(description = "Generic error response structure")
public class ErrorResponse {

    @Schema(description = "Error timestamp", example = "2024-03-20T10:00:00Z")
    private final Instant timestamp;

    @Schema(description = "Requested API path", example = "/api/v1/resource")
    private final String path;

    @Schema(description = "Error message", example = "An error occurred")
    private final String message;

    @Schema(description = "HTTP status code", example = "400")
    private final int status;

    @Schema(description = "Validation errors or field-specific messages", example = "{\"field\": \"error message\"}")
    private final Map<String, String> errors;
}
