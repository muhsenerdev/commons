package github.muhsenerdev.users.core.infra.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.muhsenerdev.commons.core.auth.Principal;
import github.muhsenerdev.users.api.application.auth.LoginResponseCustomizer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SecurityManager securityManager;
    private final List<LoginResponseCustomizer> loginResponseCustomizers;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        Principal principal = (Principal) authentication.getPrincipal();

        // 1. Inject token via SecurityManager
        securityManager.authenticate(principal, request, response);

        // 2. Prepare response body
        Map<String, Object> responseBody = new HashMap<>();
        for (LoginResponseCustomizer customizer : loginResponseCustomizers) {
            customizer.customize(responseBody, principal);
        }

        responseBody.put("user_id", principal.getUserId());
        responseBody.put("email", principal.getEmail());
        responseBody.put("username", principal.getUsername());
        responseBody.put("name", principal.getName());
        responseBody.put("roles", principal.getRoles());

        // 3. Write response
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), responseBody);
        response.flushBuffer();
    }
}
