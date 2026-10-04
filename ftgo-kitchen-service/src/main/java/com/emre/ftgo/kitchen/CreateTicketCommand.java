package com.emre.ftgo.kitchen;

import java.util.UUID;

// Same shape as the record Order Service sends. Field names must match the JSON.
public record CreateTicketCommand(UUID sagaId, Long orderId, Long restaurantId) {
}