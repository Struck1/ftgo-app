package com.emre.ftgo.accounting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AuthorizationPolicy {
    private final BigDecimal cardLimit;

    public AuthorizationPolicy(@Value("${accounting.card-limit}") BigDecimal cardLimit) {
        this.cardLimit = cardLimit;
    }

    public boolean allows(BigDecimal amount) {
        return amount.compareTo(cardLimit) <= 0;
    }

    public BigDecimal getCardLimit() {
        return cardLimit;
    }

}
