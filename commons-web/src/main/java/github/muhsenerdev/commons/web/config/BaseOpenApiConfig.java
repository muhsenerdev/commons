package github.muhsenerdev.commons.web.config;

import java.util.Map;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import github.muhsenerdev.commons.web.response.BadRequestResponse;
import github.muhsenerdev.commons.web.response.ConflictResponse;
import github.muhsenerdev.commons.web.response.ErrorResponse;
import github.muhsenerdev.commons.web.response.ForbiddenResponse;
import github.muhsenerdev.commons.web.response.InternalErrorResponse;
import github.muhsenerdev.commons.web.response.NotFoundResponse;
import github.muhsenerdev.commons.web.response.UnauthorizedResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

@Configuration
public class BaseOpenApiConfig {

    public static final String BAD_REQUEST_RESPONSE = "BadRequestResponse";
    public static final String UNAUTHORIZED_RESPONSE = "UnauthorizedResponse";
    public static final String FORBIDDEN_RESPONSE = "ForbiddenResponse";
    public static final String NOT_FOUND_RESPONSE = "NotFoundResponse";
    public static final String CONFLICT_RESPONSE = "ConflictResponse";
    public static final String INTERNAL_SERVER_ERROR_RESPONSE = "InternalErrorResponse";

    public static final String VALIDATION_ERROR_EXAMPLE = "ValidationErrorExample";
    public static final String AUTH_REQUIRED_EXAMPLE = "AuthRequiredExample";
    public static final String RESOURCE_NOT_FOUND_EXAMPLE = "ResourceNotFoundExample";
    public static final String CONFLICT_EXAMPLE = "ConflictExample";

    @Bean
    public OpenApiCustomizer baseOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            Components components = openApi.getComponents();

            // 1. Register Schemas
            registerSchemas(components);

            // 2. Register Examples
            registerExamples(components);

            // 3. Register Common Responses
            registerCommonResponses(components);

        };
    };

    private void registerSchemas(Components components) {
        // Automatically convert classes to OpenAPI schemas
        addSchema(components, ErrorResponse.class);
        addSchema(components, BadRequestResponse.class);
        addSchema(components, UnauthorizedResponse.class);
        addSchema(components, ForbiddenResponse.class);
        addSchema(components, NotFoundResponse.class);
        addSchema(components, ConflictResponse.class);
        addSchema(components, InternalErrorResponse.class);
    }

    @SuppressWarnings("rawtypes")
    private void addSchema(Components components, Class<?> clazz) {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(clazz);
        schemas.forEach(components::addSchemas);
    }

    private void registerCommonResponses(Components components) {
        components.addResponses(BAD_REQUEST_RESPONSE,
                createApiResponse("Bad Request - Validation error or malformed request",
                        "#/components/schemas/BadRequestResponse", VALIDATION_ERROR_EXAMPLE));
        components.addResponses(UNAUTHORIZED_RESPONSE, createApiResponse("Unauthorized - Authentication required",
                "#/components/schemas/UnauthorizedResponse", AUTH_REQUIRED_EXAMPLE));
        components.addResponses(FORBIDDEN_RESPONSE, createApiResponse("Forbidden - Insufficient permissions",
                "#/components/schemas/ForbiddenResponse", null));
        components.addResponses(NOT_FOUND_RESPONSE, createApiResponse("Not Found - Resource does not exist",
                "#/components/schemas/NotFoundResponse", RESOURCE_NOT_FOUND_EXAMPLE));
        components.addResponses(CONFLICT_RESPONSE,
                createApiResponse("Conflict - Resource already exists or state conflict",
                        "#/components/schemas/ConflictResponse", CONFLICT_EXAMPLE));
        components.addResponses(INTERNAL_SERVER_ERROR_RESPONSE,
                createApiResponse("Internal Server Error - Unexpected error occurred",
                        "#/components/schemas/InternalErrorResponse", null));
    }

    private void registerExamples(Components components) {
        components.addExamples(VALIDATION_ERROR_EXAMPLE, new Example()
                .summary("Validation Error Example")
                .description("Typical validation error when fields are missing or invalid")
                .value(Map.of(
                        "status", 400,
                        "message", "Validation failed",
                        "path", "/api/v1/resource",
                        "errors", Map.of(
                                "email.required", "Email is required.",
                                "password.too-short", "Password must be longer than 8."))));

        components.addExamples(AUTH_REQUIRED_EXAMPLE, new Example()
                .summary("Authentication Required")
                .value(Map.of(
                        "status", 401,
                        "message", "Authentication required",
                        "path", "/api/v1/resource",
                        "errors", Map.of(
                                "authentication.required", "Authentication required"))));

        components.addExamples(RESOURCE_NOT_FOUND_EXAMPLE, new Example()
                .summary("Resource Not Found")
                .value(Map.of(
                        "status", 404,
                        "message", "Resource not found",
                        "path", "/api/v1/resource/123",
                        "errors", Map.of())));

        components.addExamples(CONFLICT_EXAMPLE, new Example()
                .summary("Resource Conflict")
                .value(Map.of(
                        "status", 409,
                        "message", "Resource already exists",
                        "path", "/api/v1/resource",
                        "errors", Map.of("user.duplicate.username-email", "User with this email already exists."))));
    }

    private ApiResponse createApiResponse(String description, String schemaRef, String exampleKey) {
        MediaType mediaType = new MediaType().schema(new Schema<>().$ref(schemaRef));
        if (exampleKey != null) {
            mediaType.addExamples("default", new Example().$ref("#/components/examples/" + exampleKey));
        }

        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                        mediaType));
    }
}
