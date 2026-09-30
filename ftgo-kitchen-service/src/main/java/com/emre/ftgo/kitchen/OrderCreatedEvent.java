package com.emre.ftgo.kitchen;

import java.math.BigDecimal;

public record OrderCreatedEvent(Long orderId, Long consumerId, Long restaurantId, BigDecimal totalAmount) {
}