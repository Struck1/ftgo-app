package com.emre.ftgo.kitchen;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Ticket() {}

    public Ticket(Long orderId, Long restaurantId) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.status = "CREATED";
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
}