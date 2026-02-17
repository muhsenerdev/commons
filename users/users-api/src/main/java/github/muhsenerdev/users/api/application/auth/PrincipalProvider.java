package github.muhsenerdev.users.api.application.auth;

import java.util.Map;

import github.muhsenerdev.commons.core.auth.Principal;

public interface PrincipalProvider {

    Principal getPrincipal(Principal defaultPrincipal, Map<String, Object> claims);

}
