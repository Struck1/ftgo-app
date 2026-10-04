package com.emre.ftgo.kitchen;

import com.emre.ftgo.common.outbox.OutboxService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class KitchenSagaCommandHandlerOutboxFailureTest {

    @Autowired
    private KitchenSagaCommandHandler handler;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ProcessedMessageRepository processedMessageRepository;

    @MockitoBean
    private OutboxService outboxService;

    @Test
    void ticketIsRolledBackWhenOutboxPublishFails() {
        UUID commandId = UUID.randomUUID();
        UUID sagaId = UUID.randomUUID();
        Long testOrderId = 888_888L;

        doThrow(new IllegalStateException("simulated outbox failure"))
                .when(outboxService).publish(any(), any(), any(), any(), any());

        String json = """
                {"sagaId":"%s","orderId":%d,"restaurantId":5}
                """.formatted(sagaId, testOrderId);

        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("kitchen-commands", 0, 0L, sagaId.toString(), json);
        record.headers().add(new RecordHeader("eventId", commandId.toString().getBytes(StandardCharsets.UTF_8)));
        record.headers().add(new RecordHeader("eventType", "CreateTicket".getBytes(StandardCharsets.UTF_8)));

        assertThatThrownBy(() -> handler.onCommand(record))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("simulated outbox failure");

        assertThat(ticketRepository.findAll().stream()
                .anyMatch(t -> t.getOrderId().equals(testOrderId)))
                .as("ticket must be rolled back")
                .isFalse();

        assertThat(processedMessageRepository.existsByMessageId(commandId))
                .as("message must not be marked as processed")
                .isFalse();
    }
}