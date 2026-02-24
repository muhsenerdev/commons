package github.muhsenerdev.commons.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public final class Money implements Serializable {

    private BigDecimal amount;
    private String currency;

    private Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new InvalidDomainException("money.amount.required", "Amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new InvalidDomainException("money.currency.required", "Currency cannot be null or blank");
        }
        this.amount = amount;
        this.currency = currency;
    }

    @Builder
    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public String toString() {
        return amount + " " + currency;
    }

}
