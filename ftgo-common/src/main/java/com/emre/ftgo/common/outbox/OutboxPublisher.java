package com.emre.ftgo.common.outbox;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private static final int BATCH_SIZE = 50;

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxEventRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void publishPending() {
        List<OutboxEvent> batch = repository.lockNextBatch(BATCH_SIZE);

        for (OutboxEvent event : batch) {
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(event.getTopic(), event.getAggregateId(), event.getPayload());
            record.headers().add("eventId", event.getEventId().toString().getBytes(StandardCharsets.UTF_8));
            record.headers().add("eventType", event.getEventType().getBytes(StandardCharsets.UTF_8));

            try {
                kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("Failed to send outbox event {}, will retry in the next poll: {}",
                        event.getEventId(), e.getMessage());
                return;   // events marked so far are committed, the rest are retried
            }

            event.markSent();
        }
    }
}