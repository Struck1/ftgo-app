package com.emre.ftgo.order;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "create_order_saga_state")
public class CreateOrderSagaState {

    public enum State {
        CREATING_TICKET,
        AUTHORIZING_CARD,
        REJECTING_TICKET,
        ORDER_APPROVED,
        ORDER_REJECTED
    }

    @Id
    @Column(name = "saga_id", nullable = false, updatable = false)
    private UUID sagaId;

    @Column(name = "order_id", nullable = false, unique = true, updatable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private State state;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CreateOrderSagaState() {}   // required by JPA

    public CreateOrderSagaState(Long orderId) {
        this.sagaId = UUID.randomUUID();
        this.orderId = orderId;
        this.state = State.CREATING_TICKET;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void transitionTo(State newState) {
        this.state = newState;
        this.updatedAt = Instant.now();
    }

    public UUID getSagaId() { return sagaId; }
    public Long getOrderId() { return orderId; }
    public State getState() { return state; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
