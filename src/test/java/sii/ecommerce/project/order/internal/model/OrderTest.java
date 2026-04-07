package sii.ecommerce.project.order.internal.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void getTotalAmount_whenMultipleItemsAdded_shouldReturnCorrectSum() {
        // GIVEN
        Order order = new Order(UUID.randomUUID());
        order.addItem(UUID.randomUUID(), "Laptop", BigDecimal.valueOf(5000), 1);
        order.addItem(UUID.randomUUID(), "Mouse", BigDecimal.valueOf(100), 2);

        // WHEN
        BigDecimal totalAmount = order.getTotalAmount();

        // THEN
        assertThat(totalAmount).isEqualByComparingTo(BigDecimal.valueOf(5200));
    }
}