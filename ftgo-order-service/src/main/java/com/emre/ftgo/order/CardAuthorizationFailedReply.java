package com.emre.ftgo.order;

import java.util.UUID;

public record CardAuthorizationFailedReply(UUID sagaId, Long orderId, String reason) {
}