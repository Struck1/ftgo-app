package com.emre.ftgo.kitchen;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final TicketRepository ticketRepository;
    private final ProcessedMessageRepository processedMessageRepository;
    private final ObjectMapper objectMapper;

    public OrderEventListener(TicketRepository ticketRepository,
                              ProcessedMessageRepository processedMessageRepository,
                              ObjectMapper objectMapper) {
        this.ticketRepository = ticketRepository;
        this.processedMessageRepository = processedMessageRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-events")
    @Transactional
    public void onOrderEvent(ConsumerRecord<String, String> record) throws Exception {
        UUID eventId = readHeader(record, "eventId").map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Missing eventId header"));
        String eventType = readHeader(record, "eventType").orElse("");

        if (processedMessageRepository.existsByMessageId(eventId)) {
            log.info("Event {} already processed, skipping", eventId);
            return;
        }

        if ("OrderCreated".equals(eventType)) {
            OrderCreatedEvent event = objectMapper.readValue(record.value(), OrderCreatedEvent.class);
            ticketRepository.save(new Ticket(event.orderId(), event.restaurantId()));
        }

        processedMessageRepository.save(new ProcessedMessage(eventId));
    }

    private Optional<String> readHeader(ConsumerRecord<String, String> record, String key) {
        var header = record.headers().lastHeader(key);
        return header == null
                ? Optional.empty()
                : Optional.of(new String(header.value(), StandardCharsets.UTF_8));
    }

}

