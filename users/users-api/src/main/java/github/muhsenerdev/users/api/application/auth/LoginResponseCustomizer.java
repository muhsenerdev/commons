package github.muhsenerdev.users.api.application.auth;

import java.util.Map;

import github.muhsenerdev.commons.core.auth.Principal;

public interface LoginResponseCustomizer {

    void customize(Map<String, Object> response, Principal principal);

}
