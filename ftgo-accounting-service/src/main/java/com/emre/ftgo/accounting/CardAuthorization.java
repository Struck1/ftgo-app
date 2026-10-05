package com.emre.ftgo.accounting;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "card_authorizations")
public class CardAuthorization {
    public enum Status { AUTHORIZED, DECLINED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "consumer_id", nullable = false)
    private Long consumerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String reason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CardAuthorization() {}   // required by JPA

    private CardAuthorization(Long orderId, Long consumerId, BigDecimal amount, Status status, String reason) {
        this.orderId = orderId;
        this.consumerId = consumerId;
        this.amount = amount;
        this.status = status;
        this.reason = reason;
        this.createdAt = Instant.now();
    }

    public static CardAuthorization authorized(Long orderId, Long consumerId, BigDecimal amount) {
        return new CardAuthorization(orderId, consumerId, amount, Status.AUTHORIZED, null);
    }

    public static CardAuthorization declined(Long orderId, Long consumerId, BigDecimal amount, String reason) {
        return new CardAuthorization(orderId, consumerId, amount, Status.DECLINED, reason);
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Status getStatus() { return status; }
}
