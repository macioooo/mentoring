package sii.ecommerce.order.internal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "ecommerce_order")
@Getter
@NoArgsConstructor
public class Order {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID clientId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    public Order(UUID clientId) {
        this.clientId = clientId;
    }

    public void addItem(UUID productId, String snapshotName, BigDecimal snapshotPrice, int quantity) {
        this.items.add(new OrderItem(productId, snapshotName, snapshotPrice, quantity));
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(item -> item.getSnapshotPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
