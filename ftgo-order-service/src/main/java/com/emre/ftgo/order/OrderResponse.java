package com.emre.ftgo.order;

import java.math.BigDecimal;

public record OrderResponse(Long id, Long consumerId, Long restaurantId, BigDecimal totalAmount, String status) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getConsumerId(), order.getRestaurantId(),
                order.getTotalAmount(), order.getStatus());
    }
}