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

    @Column(name = "event_id", nullable = false, unique = true, updatable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String topic;           // e.g. "order-events"

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;   // e.g. "Order"

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;     // e.g. "1345"

    @Column(name = "event_type", nullable = false)
    private String eventType;       // e.g. "OrderCreated"

    @Column(nullable = false, columnDefinition = "text")
    private String payload;         // JSON

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;         // NULL = not yet sent to Kafka

    protected OutboxEvent() {}      // required by JPA

    public OutboxEvent(String topic, String aggregateType, String aggregateId, String eventType, String payload) {
        this.eventId = UUID.randomUUID();
        this.topic = topic;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public void markSent() { this.sentAt = Instant.now(); }

    public Long getId() { return id; }
    public UUID getEventId() { return eventId; }
    public String getTopic() { return topic; }
    public String getAggregateType() { return aggregateType; }
    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
}