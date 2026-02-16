package github.muhsenerdev.users.core.infra.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import github.muhsenerdev.commons.core.auth.Principal;
import github.muhsenerdev.users.api.application.auth.JwtTokenCustomizer;
import github.muhsenerdev.users.core.infra.config.SecurityProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityManager {

    private final TokenManager tokenManager;
    private final List<JwtTokenCustomizer> jwtTokenCustomizers;
    private final SecurityProperties securityProperties;

    public void authenticate(Principal principal, HttpServletRequest req, HttpServletResponse resp) {
        // 1. Prepare claims
        Map<String, Object> claims = new HashMap<>();
        for (JwtTokenCustomizer customizer : jwtTokenCustomizers) {
            customizer.customize(claims, principal);
        }

        claims.put("email", principal.getEmail());
        claims.put("roles", principal.getRoles());

        // 2. Generate token
        String token = tokenManager.generateToken(claims, principal.getUsername());

        // 3. Set token to response
        String deviceType = req.getHeader("X-Device-Type");
        if ("WEB".equalsIgnoreCase(deviceType)) {
            var cookieSettings = securityProperties.getAccessToken().getCookie();
            Cookie cookie = new Cookie(cookieSettings.getName(), token);
            cookie.setHttpOnly(cookieSettings.isHttpOnly());
            cookie.setSecure(cookieSettings.isSecure());
            cookie.setPath(cookieSettings.getPath());
            if (cookieSettings.getMaxAge() != null) {
                cookie.setMaxAge(cookieSettings.getMaxAge());
            }
            if (cookieSettings.getDomain() != null) {
                cookie.setDomain(cookieSettings.getDomain());
            }
            resp.addCookie(cookie);
        } else {
            resp.setHeader("Authorization", "Bearer " + token);
        }

        log.info("User {} authenticated successfully", principal.getUsername());
    }
}
