package github.muhsenerdev.users.api.application.auth;

import java.util.Map;

import github.muhsenerdev.commons.core.auth.Principal;

public interface JwtTokenCustomizer {

    void customize(Map<String, Object> claims, Principal principal);

}
