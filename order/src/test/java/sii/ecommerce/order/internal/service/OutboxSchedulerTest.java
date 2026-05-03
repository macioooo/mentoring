package sii.ecommerce.order.internal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import sii.ecommerce.order.internal.model.OutboxMessage;
import sii.ecommerce.order.internal.repository.OutboxRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxSchedulerTest {

    private static final String ORDER_EVENTS_TOPIC = "order-events";

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private OutboxScheduler outboxScheduler;

    @BeforeEach
    void setUp() {
        outboxScheduler = new OutboxScheduler(outboxRepository, kafkaTemplate, ORDER_EVENTS_TOPIC);
    }

    @Test
    void processOutboxMessages_whenPendingMessagesExist_shouldSendToKafkaAndSave() {
        OutboxMessage pendingOutboxMessage = OutboxMessage.builder()
                .id(UUID.randomUUID())
                .aggregateId("order-123")
                .eventType("OrderCreated")
                .payload("{\"test\":\"data\"}")
                .processed(false)
                .build();

        when(outboxRepository.findByProcessedFalse()).thenReturn(List.of(pendingOutboxMessage));

        outboxScheduler.processOutboxMessages();

        verify(kafkaTemplate, times(1)).send(ORDER_EVENTS_TOPIC, "order-123", "{\"test\":\"data\"}");
        verify(outboxRepository, times(1)).save(pendingOutboxMessage);
        assertThat(pendingOutboxMessage.isProcessed()).isTrue();
    }

    @Test
    void processOutboxMessages_whenNoPendingMessages_shouldDoNothing() {
        when(outboxRepository.findByProcessedFalse()).thenReturn(Collections.emptyList());

        outboxScheduler.processOutboxMessages();

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
        verify(outboxRepository, never()).save(any());
    }
}
