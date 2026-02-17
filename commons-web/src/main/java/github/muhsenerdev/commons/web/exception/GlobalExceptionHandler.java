package github.muhsenerdev.commons.web.exception;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import github.muhsenerdev.commons.core.exception.AuthenticationRequiredException;
import github.muhsenerdev.commons.core.exception.BusinessException;
import github.muhsenerdev.commons.core.exception.DuplicateException;
import github.muhsenerdev.commons.core.exception.InternalErrorException;
import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.core.exception.NoPermissionException;
import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.commons.web.response.BadRequestResponse;
import github.muhsenerdev.commons.web.response.ConflictResponse;
import github.muhsenerdev.commons.web.response.ErrorResponse;
import github.muhsenerdev.commons.web.response.ForbiddenResponse;
import github.muhsenerdev.commons.web.response.InternalErrorResponse;
import github.muhsenerdev.commons.web.response.NotFoundResponse;
import github.muhsenerdev.commons.web.response.UnauthorizedResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<NotFoundResponse> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(NotFoundResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .path(request.getRequestURI())
                        .message(ex.getMessage())
                        .status(HttpStatus.NOT_FOUND.value())
                        .build());
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ConflictResponse> handleDuplicateException(DuplicateException ex,
            HttpServletRequest request) {
        log.error("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ConflictResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .path(request.getRequestURI())
                        .message(ex.getMessage())
                        .status(HttpStatus.CONFLICT.value())
                        .build());
    }

    @ExceptionHandler(AuthenticationRequiredException.class)
    public ResponseEntity<UnauthorizedResponse> handleAuthenticationRequiredException(
            AuthenticationRequiredException ex,
            HttpServletRequest request) {
        log.error("Authentication required: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(UnauthorizedResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .path(request.getRequestURI())
                        .message(ex.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build());
    }

    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<ForbiddenResponse> handleNoPermissionException(NoPermissionException ex,
            HttpServletRequest request) {
        log.error("No permission: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ForbiddenResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .path(request.getRequestURI())
                        .message(ex.getMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .build());
    }

    @ExceptionHandler({ BusinessException.class, InvalidDomainException.class })
    public ResponseEntity<BadRequestResponse> handleBadRequestException(RuntimeException ex,
            HttpServletRequest request) {
        log.error("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BadRequestResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .path(request.getRequestURI())
                        .message(ex.getMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<InternalErrorResponse> handleInternalErrorException(InternalErrorException ex,
            HttpServletRequest request) {
        log.error("Internal error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(InternalErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .path(request.getRequestURI())
                        .message("An internal error occurred.")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequestResponse> handleValidationException(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BadRequestResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .path(request.getRequestURI())
                        .message("Validation failed")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .timestamp(OffsetDateTime.now())
                        .path(request.getRequestURI())
                        .message("An unexpected error occurred.")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build());
    }
}
