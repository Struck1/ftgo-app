package com.emre.ftgo.kitchen;

import java.util.UUID;

public record TicketCreatedReply(UUID sagaId, Long orderId) {
}