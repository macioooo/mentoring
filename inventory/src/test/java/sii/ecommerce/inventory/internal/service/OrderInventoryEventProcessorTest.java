package sii.ecommerce.inventory.internal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sii.ecommerce.inventory.internal.model.ConsumedKafkaMessage;
import sii.ecommerce.inventory.internal.model.ConsumedKafkaMessageId;
import sii.ecommerce.inventory.internal.repository.ConsumedKafkaMessageRepository;
import sii.ecommerce.order.api.events.OrderCreatedEvent;
import sii.ecommerce.order.api.events.OrderItemPayload;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderInventoryEventProcessorTest {

    @Mock
    private ConsumedKafkaMessageRepository consumedKafkaMessageRepository;

    @Mock
    private StockReservationService stockReservationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private OrderInventoryEventProcessor orderInventoryEventProcessor;

    @BeforeEach
    void setUp() {
        orderInventoryEventProcessor =
                new OrderInventoryEventProcessor(consumedKafkaMessageRepository, objectMapper, stockReservationService);
    }

    @Test
    void process_whenAlreadyProcessed_skipsReservation() throws Exception {
        ConsumedKafkaMessageId kafkaMessageIdentity = new ConsumedKafkaMessageId("order-events", 0, 1L);
        when(consumedKafkaMessageRepository.existsById(kafkaMessageIdentity)).thenReturn(true);

        orderInventoryEventProcessor.process("order-events", 0, 1L, "{}");

        verify(stockReservationService, never()).reserveForOrderCreated(any());
        verify(consumedKafkaMessageRepository, never()).save(any());
    }

    @Test
    void process_whenNewMessage_reservesAndMarksProcessed() throws Exception {
        when(consumedKafkaMessageRepository.existsById(any())).thenReturn(false);

        UUID orderId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                orderId,
                clientId,
                BigDecimal.TEN,
                List.of(new OrderItemPayload(productId, 2, BigDecimal.ONE)));
        String serializedEvent = objectMapper.writeValueAsString(orderCreatedEvent);

        orderInventoryEventProcessor.process("order-events", 0, 5L, serializedEvent);

        verify(stockReservationService, times(1)).reserveForOrderCreated(any(OrderCreatedEvent.class));
        verify(consumedKafkaMessageRepository, times(1)).save(any(ConsumedKafkaMessage.class));
    }
}
