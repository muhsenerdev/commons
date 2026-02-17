package github.muhsenerdev.users.core.infra.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import github.muhsenerdev.commons.core.auth.Principal;
import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.commons.core.vo.Name;
import github.muhsenerdev.commons.core.vo.RoleName;
import github.muhsenerdev.commons.core.vo.Username;
import github.muhsenerdev.users.api.application.auth.JwtTokenCustomizer;
import github.muhsenerdev.users.api.application.auth.PrincipalProvider;
import github.muhsenerdev.users.core.application.auth.DefaultPrincipal;
import github.muhsenerdev.users.core.domain.users.UserStatus;
import github.muhsenerdev.users.core.infra.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityManager {

    private final TokenManager tokenManager;
    private final List<JwtTokenCustomizer> jwtTokenCustomizers;
    private final SecurityProperties securityProperties;

    @Autowired(required = false)
    private PrincipalProvider principalProvider;

    public void authenticate(Principal principal, HttpServletRequest req, HttpServletResponse resp) {
        // 1. Prepare claims
        Map<String, Object> claims = new HashMap<>();
        for (JwtTokenCustomizer customizer : jwtTokenCustomizers) {
            customizer.customize(claims, principal);
        }

        claims.put("user_id", principal.getUserId().toString());
        claims.put("email", principal.getEmail());
        claims.put("roles", principal.getRoles());
        claims.put("missing_details", principal.getMissingDetails());
        claims.put("status", principal.getStatus());
        claims.put("active", principal.isActive());
        claims.put("username", principal.getUsername());
        claims.put("full_name", principal.getName());

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

    @SuppressWarnings("unchecked")
    public Authentication verifyAndAuthenticate(String token) {
        try {
            Claims claims = tokenManager.verifyToken(token);

            UUID userId = UUID.fromString(claims.get("user_id", String.class));
            String emailStr = claims.get("email", String.class);
            List<String> rolesList = claims.get("roles", List.class);
            Set<String> missingDetails = new HashSet<>(claims.get("missing_details", List.class));
            String statusStr = claims.get("status", String.class);
            String fullNameSt = claims.get("full_name", String.class);
            String usernameSt = claims.get("username", String.class);

            Set<RoleName> roles = rolesList.stream()
                    .map(RoleName::of)
                    .collect(Collectors.toSet());

            DefaultPrincipal defaultPrincipal = DefaultPrincipal.builder()
                    .userId(userId)
                    .email(Email.of(emailStr))
                    .roles(roles)
                    .missingDetails(missingDetails)
                    .status(UserStatus.valueOf(statusStr))
                    .name(Name.fromStringOrNull(fullNameSt))
                    .username(Username.fromStringOrNull(usernameSt))
                    .build();

            Principal finalPrincipal = defaultPrincipal;
            if (principalProvider != null) {
                finalPrincipal = principalProvider.getPrincipal(defaultPrincipal, claims);
            }

            var authorities = finalPrincipal.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(finalPrincipal, null, authorities);

        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
            return null;
        }
    }
}
