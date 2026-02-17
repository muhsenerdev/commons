package github.muhsenerdev.users.core.infra.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.muhsenerdev.commons.core.auth.Principal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncompletedUserFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/v1/registration/complete")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Principal principal) {
            if (!principal.isActive()) {
                log.warn("User {} is inactive, blocking request to {}", principal.getEmail(),
                        request.getRequestURI());
                sendIncompleteRegistrationResponse(request, response, principal);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendIncompleteRegistrationResponse(HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws IOException {

        IncompleteRegistrationResponse error = IncompleteRegistrationResponse.builder()
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .message("User registration is incomplete. Please provide missing details.")
                .status(HttpStatus.FORBIDDEN.value())
                .missingDetails(principal.getMissingDetails())
                .build();

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), error);
    }

    @Getter
    @Builder
    public static class IncompleteRegistrationResponse {
        private final Instant timestamp;
        private final String path;
        private final String message;
        private final int status;
        private final Set<String> missingDetails;
    }
}
