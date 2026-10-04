package com.emre.ftgo.order;

import java.util.UUID;

public record TicketCreatedReply(UUID sagaId, Long orderId) {
}