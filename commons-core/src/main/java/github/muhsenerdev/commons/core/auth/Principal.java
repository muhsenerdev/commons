package github.muhsenerdev.commons.core.auth;

import java.util.Set;
import java.util.UUID;

public interface Principal {

    UUID getUserId();

    String getEmail();

    String getUsername();

    String getName();

    Set<String> getRoles();

}
