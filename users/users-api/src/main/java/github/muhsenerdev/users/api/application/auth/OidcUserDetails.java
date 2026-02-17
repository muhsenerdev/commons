package github.muhsenerdev.users.api.application.auth;

import lombok.Builder;

@Builder(toBuilder = true)
public record OidcUserDetails(String email, String username, String name, String provider, String providerId) {

}
