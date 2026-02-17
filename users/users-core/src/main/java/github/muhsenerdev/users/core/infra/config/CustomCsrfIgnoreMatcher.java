package github.muhsenerdev.users.core.infra.config;

import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCsrfIgnoreMatcher implements RequestMatcher {

    private final SecurityProperties securityProperties;
    private final PathPatternRequestMatcher publicMatcher = PathPatternRequestMatcher.withDefaults()
            .matcher("/public/**");

    @Override
    public boolean matches(HttpServletRequest request) {
        // 1. Ignore if path is /public/**
        if (publicMatcher.matches(request)) {
            return true;
        }

        // 2. Ignore if Authorization: Bearer ... is present AND access_token cookie is
        // missing/empty
        String authHeader = request.getHeader("Authorization");
        boolean hasBearerToken = authHeader != null && authHeader.startsWith("Bearer ");

        boolean hasAccessTokenCookie = false;
        if (request.getCookies() != null) {
            String cookieName = securityProperties.getAccessToken().getCookie().getName();
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    if (cookie.getValue() != null && !cookie.getValue().isBlank()) {
                        hasAccessTokenCookie = true;
                    }
                    break;
                }
            }
        }

        // If it's a token-based request (Bearer) and not a cookie-based request (no
        // access_token cookie), ignore CSRF
        return hasBearerToken && !hasAccessTokenCookie;
    }
}
