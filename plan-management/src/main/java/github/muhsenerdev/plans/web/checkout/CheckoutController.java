package github.muhsenerdev.plans.web.checkout;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.commons.core.auth.Principal;
import github.muhsenerdev.plans.application.subscription.checkout.CheckoutStartResponse;
import github.muhsenerdev.plans.application.subscription.checkout.StartCheckoutCommand;
import github.muhsenerdev.plans.application.subscription.checkout.StartCheckoutCommandHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subscriptions/checkout")
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Endpoints for starting plan purchases")
public class CheckoutController {

    private final StartCheckoutCommandHandler startCheckoutCommandHandler;

    @PostMapping
    @Operation(summary = "Start a checkout session", description = "Creates a pending subscription and returns a checkout URL")
    public CheckoutStartResponse startCheckout(@AuthenticationPrincipal Principal principal,
            @Valid @RequestBody StartCheckoutCommand command) {
        command.setUserId(principal.getUserId());
        return startCheckoutCommandHandler.handle(command);
    }
}
