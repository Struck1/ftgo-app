package com.emre.ftgo.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull Long consumerId,
        @NotNull Long restaurantId,
        @NotNull @DecimalMin("0.01") BigDecimal totalAmount) {
}