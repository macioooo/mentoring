package sii.ecommerce.order.internal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sii.ecommerce.catalog.api.ProductCatalogFacade;
import sii.ecommerce.order.api.dto.CreateOrderCommand;
import sii.ecommerce.order.api.dto.OrderItemDto;
import sii.ecommerce.order.internal.model.Order;
import sii.ecommerce.order.internal.model.OutboxMessage;
import sii.ecommerce.order.internal.repository.OrderRepository;
import sii.ecommerce.order.internal.repository.OutboxRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OutboxRepository outboxRepository;
    @Mock
    private ProductCatalogFacade productCatalogFacade;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_whenValidCommand_shouldSaveOrderAndOutboxMessage() throws JsonProcessingException {
        UUID clientId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(clientId, List.of(new OrderItemDto(productId, 2)));

        when(productCatalogFacade.fetchProductDetails(productId))
                .thenReturn(new ProductCatalogFacade.ProductSnapshot("Monitor", BigDecimal.valueOf(1000)));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"fake\":\"json\"}");

        UUID orderId = orderService.createOrder(createOrderCommand);

        assertThat(orderId).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(outboxRepository, times(1)).save(any(OutboxMessage.class));
    }

    @Test
    void createOrder_whenProductCatalogThrowsException_shouldThrowExceptionAndNotSave() {
        UUID clientId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(clientId, List.of(new OrderItemDto(productId, 1)));

        when(productCatalogFacade.fetchProductDetails(productId))
                .thenThrow(new RuntimeException("Product Catalog is DOWN!"));

        assertThatThrownBy(() -> orderService.createOrder(createOrderCommand))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product Catalog is DOWN!");

        verify(orderRepository, never()).save(any());
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void createOrder_whenJsonSerializationFails_shouldThrowExceptionAndNotSaveOutbox() throws JsonProcessingException {
        UUID productId = UUID.randomUUID();
        CreateOrderCommand createOrderCommand =
                new CreateOrderCommand(UUID.randomUUID(), List.of(new OrderItemDto(productId, 1)));

        when(productCatalogFacade.fetchProductDetails(productId))
                .thenReturn(new ProductCatalogFacade.ProductSnapshot("Monitor", BigDecimal.valueOf(1000)));
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});

        assertThatThrownBy(() -> orderService.createOrder(createOrderCommand))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("System error");

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(outboxRepository, never()).save(any());
    }
}
