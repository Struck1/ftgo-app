package com.emre.ftgo.order;

import java.math.BigDecimal;

public record OrderCreatedEvent(Long orderId, Long consumerId, Long restaurantId, BigDecimal totalAmount) {
}