package com.emre.ftgo.common.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tüketicinin duplicate tespiti için kullanacağı kimlik. Bir kez üretilir, asla değişmez.
    @Column(name = "event_id", nullable = false, unique = true, updatable = false)
    private UUID eventId;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;   // örn. "Order"

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;     // örn. "1345"

    @Column(name = "event_type", nullable = false)
    private String eventType;       // örn. "OrderCreated"

    @Column(nullable = false, columnDefinition = "text")
    private String payload;         // JSON

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;         // NULL = henüz Kafka'ya gönderilmedi

    protected OutboxEvent() {}      // JPA için

    public OutboxEvent(String aggregateType, String aggregateId, String eventType, String payload) {
        this.eventId = UUID.randomUUID();
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public void markSent() { this.sentAt = Instant.now(); }

    public Long getId() { return id; }
    public UUID getEventId() { return eventId; }
    public String getAggregateType() { return aggregateType; }
    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
}