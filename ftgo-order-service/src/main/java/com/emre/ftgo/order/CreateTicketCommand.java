package com.emre.ftgo.order;

import java.util.UUID;

public record CreateTicketCommand(UUID sagaId, Long orderId, Long restaurantId) {
}
