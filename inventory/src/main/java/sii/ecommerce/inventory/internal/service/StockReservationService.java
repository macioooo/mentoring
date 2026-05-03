package sii.ecommerce.inventory.internal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sii.ecommerce.inventory.internal.model.Stock;
import sii.ecommerce.inventory.internal.repository.StockRepository;
import sii.ecommerce.order.api.events.OrderCreatedEvent;
import sii.ecommerce.order.api.events.OrderItemPayload;

@Service
@RequiredArgsConstructor
public class StockReservationService {

    private static final int AUTOMATIC_STOCK_ROW_INITIAL_AVAILABLE_UNITS = 100_000;

    private final StockRepository stockRepository;

    @Transactional
    public void reserveForOrderCreated(OrderCreatedEvent event) {
        for (OrderItemPayload orderLine : event.items()) {
            Stock stockRow = stockRepository.findByProductId(orderLine.productId()).orElseGet(() -> stockRepository.save(
                    new Stock(
                            orderLine.productId(),
                            AUTOMATIC_STOCK_ROW_INITIAL_AVAILABLE_UNITS,
                            0)));
            stockRow.reserve(orderLine.quantity());
        }
    }
}
