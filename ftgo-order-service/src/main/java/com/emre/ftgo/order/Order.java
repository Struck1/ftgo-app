package com.emre.ftgo.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumer_id", nullable = false)
    private Long consumerId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Order() {}

    public Order(Long consumerId, Long restaurantId, BigDecimal totalAmount) {
        this.consumerId = consumerId;
        this.restaurantId = restaurantId;
        this.totalAmount = totalAmount;
        this.status = "CREATED";
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getConsumerId() { return consumerId; }
    public Long getRestaurantId() { return restaurantId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
}