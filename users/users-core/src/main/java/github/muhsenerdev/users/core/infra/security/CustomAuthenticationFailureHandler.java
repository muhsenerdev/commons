package github.muhsenerdev.users.core.infra.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.muhsenerdev.commons.core.exception.AuthenticationRequiredException;
import github.muhsenerdev.commons.web.response.UnauthorizedResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        log.error("Authentication failed: {}", exception.getMessage());

        var authRequiredException = new AuthenticationRequiredException(exception.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> errorDetails = Map.of(
                authRequiredException.getCode(),
                authRequiredException.getMessage());

        UnauthorizedResponse unauthorizedResponse = UnauthorizedResponse.of(request.getRequestURI(),
                exception.getMessage(), errorDetails);

        objectMapper.writeValue(response.getWriter(), unauthorizedResponse);
        response.flushBuffer();
    }
}
