package github.muhsenerdev.plans.infra.adapter.payment;

import github.muhsenerdev.commons.core.exception.ApplicationException;

public class PaymentGatewayException extends ApplicationException {

    protected PaymentGatewayException(String message) {
        super(message);
    }

}
