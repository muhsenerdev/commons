package github.muhsenerdev.users.core.infra.security;

import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import github.muhsenerdev.commons.core.auth.Principal;
import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.users.core.application.auth.DefaultPrincipal;
import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import github.muhsenerdev.users.core.domain.users.UserStatus;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userRepository.findWithRolesByEmail(Email.of(email))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("User is not active");
        }

        if (!passwordEncoder.matches(password, user.getPassword().getValue())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Principal principal = toPrincipal(user);

        var authorities = principal.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private Principal toPrincipal(User user) {
        return DefaultPrincipal.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .name(user.getName())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .missingDetails(user.getMissingDetails())
                .status(user.getStatus())
                .build();
    }
}
