package sii.ecommerce.order.internal.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void getTotalAmount_whenMultipleItemsAdded_shouldReturnCorrectSum() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(UUID.randomUUID(), "Laptop", BigDecimal.valueOf(5000), 1);
        order.addItem(UUID.randomUUID(), "Mouse", BigDecimal.valueOf(100), 2);

        BigDecimal totalAmount = order.getTotalAmount();

        assertThat(totalAmount).isEqualByComparingTo(BigDecimal.valueOf(5200));
    }
}
