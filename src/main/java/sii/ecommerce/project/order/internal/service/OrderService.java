package sii.ecommerce.project.order.internal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sii.ecommerce.project.order.api.dto.CreateOrderCommand;
import sii.ecommerce.project.order.api.events.OrderCreatedEvent;
import sii.ecommerce.project.order.internal.client.ProductCatalogClient;
import sii.ecommerce.project.order.internal.model.Order;
import sii.ecommerce.project.order.internal.model.OutboxMessage;
import sii.ecommerce.project.order.internal.repository.OrderRepository;
import sii.ecommerce.project.order.internal.repository.OutboxRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ProductCatalogClient productCatalogClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public UUID createOrder(CreateOrderCommand command) {
        log.info("Starting processing order for client: {}", command.clientId());

        Order order = new Order(command.clientId());


        for (var item : command.items()) {
            var productInfo = productCatalogClient.fetchProductDetails(item.productId());
            order.addItem(item.productId(), productInfo.name(), productInfo.price(), item.quantity());
        }

        orderRepository.save(order);

        try {
            var event = new OrderCreatedEvent(order.getId(), order.getClientId(), order.getTotalAmount());
            OutboxMessage outboxMessage = OutboxMessage.builder()
                    .aggregateType("ORDER")
                    .aggregateId(order.getId().toString())
                    .eventType("OrderCreated")
                    .payload(objectMapper.writeValueAsString(event))
                    .build();

            outboxRepository.save(outboxMessage);
            log.info("Saved the order {} and Outbox event", order.getId());
        } catch (Exception e) {
            log.error("Error while event serialization for order {}", order.getId(), e);
            throw new RuntimeException("System error while processing the order", e);
        }

        return order.getId();
    }
}
