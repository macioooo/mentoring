package sii.ecommerce.project.order.internal.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import sii.ecommerce.project.order.internal.model.OutboxMessage;
import sii.ecommerce.project.order.internal.repository.OutboxRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxSchedulerTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private OutboxScheduler outboxScheduler;

    @Test
    void processOutboxMessages_whenPendingMessagesExist_shouldSendToKafkaAndSave() {
        // GIVEN
        OutboxMessage message = OutboxMessage.builder()
                .id(UUID.randomUUID())
                .aggregateId("order-123")
                .eventType("OrderCreated")
                .payload("{\"test\":\"data\"}")
                .processed(false)
                .build();

        when(outboxRepository.findByProcessedFalse()).thenReturn(List.of(message));

        // WHEN
        outboxScheduler.processOutboxMessages();

        // THEN
        verify(kafkaTemplate, times(1)).send("order-events", "order-123", "{\"test\":\"data\"}");
        verify(outboxRepository, times(1)).save(message);
        assertThat(message.isProcessed()).isTrue();
    }

    @Test
    void processOutboxMessages_whenNoPendingMessages_shouldDoNothing() {
        // GIVEN
        when(outboxRepository.findByProcessedFalse()).thenReturn(Collections.emptyList());

        // WHEN
        outboxScheduler.processOutboxMessages();

        // THEN
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
        verify(outboxRepository, never()).save(any());
    }
}