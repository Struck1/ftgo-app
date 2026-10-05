package com.emre.ftgo.order;

import com.emre.ftgo.common.outbox.OutboxService;
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

import static com.emre.ftgo.order.CreateOrderSagaState.State;

@Component
public class OrderSagaReplyListener {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaReplyListener.class);

    private final CreateOrderSagaStateRepository sagaStateRepository;
    private final OrderRepository orderRepository;
    private final ProcessedMessageRepository processedMessageRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public OrderSagaReplyListener(CreateOrderSagaStateRepository sagaStateRepository,
                                  OrderRepository orderRepository,
                                  ProcessedMessageRepository processedMessageRepository,
                                  OutboxService outboxService,
                                  ObjectMapper objectMapper) {
        this.sagaStateRepository = sagaStateRepository;
        this.orderRepository = orderRepository;
        this.processedMessageRepository = processedMessageRepository;
        this.outboxService = outboxService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-saga-replies")
    @Transactional(rollbackFor = Exception.class)
    public void onReply(ConsumerRecord<String, String> record) throws Exception {
        UUID messageId = readHeader(record, "eventId").map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Missing eventId header"));
        String replyType = readHeader(record, "eventType").orElse("");

        if (processedMessageRepository.existsByMessageId(messageId)) {
            log.info("Saga reply {} already processed, skipping", messageId);
            return;
        }

        switch (replyType) {
            case "TicketCreated" ->
                    onTicketCreated(objectMapper.readValue(record.value(), TicketCreatedReply.class));
            case "CardAuthorized" ->
                    onCardAuthorized(objectMapper.readValue(record.value(), CardAuthorizedReply.class));
            case "CardAuthorizationFailed" ->
                    onCardAuthorizationFailed(objectMapper.readValue(record.value(), CardAuthorizationFailedReply.class));
            default -> log.warn("Ignoring unknown reply type '{}'", replyType);
        }

        processedMessageRepository.save(new ProcessedMessage(messageId));
    }

    private void onTicketCreated(TicketCreatedReply reply) {
        CreateOrderSagaState saga = loadSaga(reply.sagaId());
        if (!isIn(saga, State.CREATING_TICKET, "TicketCreated")) {
            return;
        }
        saga.transitionTo(State.AUTHORIZING_CARD);

        Order order = orderRepository.findById(saga.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Unknown order: " + saga.getOrderId()));

        // Next saga step: ask Accounting Service to authorize the card (the pivot transaction).
        outboxService.publish(
                "accounting-commands",
                "CreateOrderSaga",
                saga.getSagaId().toString(),
                "AuthorizeCard",
                new AuthorizeCardCommand(saga.getSagaId(), order.getId(), order.getConsumerId(), order.getTotalAmount()));

        log.info("Saga {} moved to AUTHORIZING_CARD", saga.getSagaId());
    }

    private void onCardAuthorized(CardAuthorizedReply reply) {
        CreateOrderSagaState saga = loadSaga(reply.sagaId());
        if (!isIn(saga, State.AUTHORIZING_CARD, "CardAuthorized")) {
            return;
        }
        saga.transitionTo(State.APPROVING_TICKET);
        log.info("Saga {} moved to APPROVING_TICKET", saga.getSagaId());
        // Step 6: send the ApproveTicket command to Kitchen Service here.
    }

    private void onCardAuthorizationFailed(CardAuthorizationFailedReply reply) {
        CreateOrderSagaState saga = loadSaga(reply.sagaId());
        if (!isIn(saga, State.AUTHORIZING_CARD, "CardAuthorizationFailed")) {
            return;
        }
        saga.transitionTo(State.REJECTING_TICKET);
        log.info("Saga {} moved to REJECTING_TICKET: {}", saga.getSagaId(), reply.reason());
        // Step 6: send the RejectTicket command to Kitchen Service here (compensation).
    }

    private CreateOrderSagaState loadSaga(UUID sagaId) {
        return sagaStateRepository.findById(sagaId)
                .orElseThrow(() -> new IllegalStateException("Unknown saga: " + sagaId));
    }

    // A reply is only valid in the state that waits for it.
    private boolean isIn(CreateOrderSagaState saga, State expected, String replyType) {
        if (saga.getState() == expected) {
            return true;
        }
        log.warn("Saga {} got {} in unexpected state {}, ignoring", saga.getSagaId(), replyType, saga.getState());
        return false;
    }

    private Optional<String> readHeader(ConsumerRecord<String, String> record, String key) {
        var header = record.headers().lastHeader(key);
        return header == null
                ? Optional.empty()
                : Optional.of(new String(header.value(), StandardCharsets.UTF_8));
    }
}