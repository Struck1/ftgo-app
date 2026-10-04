package com.emre.ftgo.kitchen;

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

@Component
public class KitchenSagaCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(KitchenSagaCommandHandler.class);

    private final TicketRepository ticketRepository;
    private final ProcessedMessageRepository processedMessageRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public KitchenSagaCommandHandler(TicketRepository ticketRepository,
                                     ProcessedMessageRepository processedMessageRepository,
                                     OutboxService outboxService,
                                     ObjectMapper objectMapper) {
        this.ticketRepository = ticketRepository;
        this.processedMessageRepository = processedMessageRepository;
        this.outboxService = outboxService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "kitchen-commands")
    @Transactional
    public void onCommand(ConsumerRecord<String, String> record) throws Exception {
        UUID messageId = readHeader(record, "eventId").map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Missing eventId header"));
        String commandType = readHeader(record, "eventType").orElse("");

        if (processedMessageRepository.existsByMessageId(messageId)) {
            log.info("Command {} already processed, skipping", messageId);
            return;
        }

        if ("CreateTicket".equals(commandType)) {
            CreateTicketCommand command = objectMapper.readValue(record.value(), CreateTicketCommand.class);
            Ticket ticket = ticketRepository.save(new Ticket(command.orderId(), command.restaurantId()));

            // The reply goes through the outbox in the same transaction as the ticket.
            outboxService.publish(
                    "order-saga-replies",
                    "CreateOrderSaga",
                    command.sagaId().toString(),
                    "TicketCreated",
                    new TicketCreatedReply(command.sagaId(), command.orderId()));

            log.info("Ticket {} created for order {}", ticket.getId(), command.orderId());
        } else {
            // Unknown command: log and mark as processed so it does not block the channel.
            log.warn("Ignoring unknown command type '{}'", commandType);
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