package com.emre.ftgo.order;

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
public class OrderSagaReplyListener {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaReplyListener.class);

    private final CreateOrderSagaStateRepository sagaStateRepository;
    private final ProcessedMessageRepository processedMessageRepository;
    private final ObjectMapper objectMapper;

    public OrderSagaReplyListener(CreateOrderSagaStateRepository sagaStateRepository,
                                  ProcessedMessageRepository processedMessageRepository,
                                  ObjectMapper objectMapper) {
        this.sagaStateRepository = sagaStateRepository;
        this.processedMessageRepository = processedMessageRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-saga-replies")
    @Transactional
    public void onReply(ConsumerRecord<String, String> record) throws Exception {
        UUID messageId = readHeader(record, "eventId").map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Missing eventId header"));
        String replyType = readHeader(record, "eventType").orElse("");

        if (processedMessageRepository.existsByMessageId(messageId)) {
            log.info("Saga reply {} already processed, skipping", messageId);
            return;
        }

        if ("TicketCreated".equals(replyType)) {
            TicketCreatedReply reply = objectMapper.readValue(record.value(), TicketCreatedReply.class);
            CreateOrderSagaState saga = sagaStateRepository.findById(reply.sagaId())
                    .orElseThrow(() -> new IllegalStateException("Unknown saga: " + reply.sagaId()));

            // A reply is only valid in the state that waits for it.
            if (saga.getState() == CreateOrderSagaState.State.CREATING_TICKET) {
                saga.transitionTo(CreateOrderSagaState.State.AUTHORIZING_CARD);
                log.info("Saga {} moved to AUTHORIZING_CARD", saga.getSagaId());
                // Step 5: send the AuthorizeCard command to Accounting Service here.
            } else {
                log.warn("Saga {} got TicketCreated in unexpected state {}, ignoring",
                        saga.getSagaId(), saga.getState());
            }
        } else {
            log.warn("Ignoring unknown reply type '{}'", replyType);
        }

        processedMessageRepository.save(new ProcessedMessage(messageId));
    }

    private Optional<String> readHeader(ConsumerRecord<String, String> record, String key) {
        var header = record.headers().lastHeader(key);
        return header == null
                ? Optional.empty()
                : Optional.of(new String(header.value(), StandardCharsets.UTF_8));
    }
}