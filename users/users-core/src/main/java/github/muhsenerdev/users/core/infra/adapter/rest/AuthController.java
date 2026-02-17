package github.muhsenerdev.users.core.infra.adapter.rest;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/public/auth")
@Tag(name = "Authentication", description = "Authentication and CSRF endpoints")
public class AuthController {

    @Operation(summary = "Login with username and password", description = "Standard form-login endpoint. Handled by Spring Security filter chain.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(type = "object", requiredProperties = {
            "username",
            "password" }), examples = @ExampleObject(name = "Login Request", value = "username=user@example.com&password=yourpassword"))))
    @PostMapping("/login")
    public void dummyLogin(@RequestParam String username, @RequestParam String password) {
        // This endpoint is dummy and only for Swagger documentation.
        // The actual request is intercepted by Spring Security at the same URL.
    }

    @Operation(summary = "Get CSRF Token", description = "Triggers the generation of a lazy CSRF token and returns it in the response cookie (XSRF-TOKEN).")
    @GetMapping("/csrf")
    public void getCsrf(CsrfToken token) {
        // Accessing CsrfToken parameter triggers the lazy generation in Spring Security
        // 6.
        // The token will be automatically set in the response cookie by
        // CookieCsrfTokenRepository.
        token.getToken();
    }
}
