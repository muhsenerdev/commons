package github.muhsenerdev.users.api.application.registration;

import java.util.List;
import java.util.Map;

import github.muhsenerdev.users.api.application.auth.OidcUserDetails;

public interface RegistrationHook {

    void validateRegistrationDetails(Map<String, Object> otherDetails);

    void validateCompletionDetails(Map<String, Object> command);

    List<String> getExtractDetails(OidcUserDetails userDetails);
}
