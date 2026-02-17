package github.muhsenerdev.users.core.infra.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import github.muhsenerdev.commons.core.auth.Principal;
import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.users.api.application.auth.LoginResponseCustomizer;
import github.muhsenerdev.users.api.application.auth.OidcUserDetails;
import github.muhsenerdev.users.core.application.user.shared.SocialLoginHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SecurityManager securityManager;
    private final List<LoginResponseCustomizer> loginResponseCustomizers;
    private final ObjectMapper objectMapper;
    private final SocialLoginHandler socialLoginHandler;
    private final SecurityMapper securityMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        log.debug("Handling successfull authentication.");
        Object principalObject = authentication.getPrincipal();

        Principal principal;
        if (principalObject instanceof DefaultOidcUser oidc) {
            principal = handleSocialLogin((OAuth2AuthenticationToken) authentication, request, response, oidc);
        } else {
            principal = (Principal) principalObject;
        }

        // 1. Inject token via SecurityManager
        securityManager.authenticate(principal, request, response);

        // 2. Prepare response body
        writeAndFlushLoginResponse(response, principal);

    }

    private Principal handleSocialLogin(OAuth2AuthenticationToken authentication, HttpServletRequest request,
            HttpServletResponse response, DefaultOidcUser oidc) throws IOException {

        String provider = authentication.getAuthorizedClientRegistrationId();

        Email email = Email.of(oidc.getEmail());
        OidcUserDetails userDetails = OidcUserDetails.builder()
                .name(oidc.getFullName())
                .email(email.getValue())
                .username(oidc.getPreferredUsername())
                .providerId(oidc.getSubject())
                .provider(provider)
                .build();
        var userInfo = socialLoginHandler.handle(userDetails);
        return securityMapper.toPrincipal(userInfo);

    }

    private void writeAndFlushLoginResponse(HttpServletResponse response, Principal principal) throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        for (LoginResponseCustomizer customizer : loginResponseCustomizers) {
            customizer.customize(responseBody, principal);
        }

        responseBody.put("user_id", principal.getUserId());
        responseBody.put("email", principal.getEmail());
        responseBody.put("username", principal.getUsername());
        responseBody.put("name", principal.getName());
        responseBody.put("roles", principal.getRoles());
        responseBody.put("missing_details", principal.getMissingDetails());
        responseBody.put("status", principal.getStatus());

        // 3. Write response
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), responseBody);
        response.flushBuffer();
    }

}
