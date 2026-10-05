package com.emre.ftgo.order;

import java.util.UUID;

public record CardAuthorizedReply(UUID sagaId, Long orderId) {
}