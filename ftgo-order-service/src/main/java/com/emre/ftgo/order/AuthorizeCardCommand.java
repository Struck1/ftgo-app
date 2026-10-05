package com.emre.ftgo.order;

import java.math.BigDecimal;
import java.util.UUID;

public record AuthorizeCardCommand(UUID sagaId, Long orderId, Long consumerId, BigDecimal totalAmount) {
}