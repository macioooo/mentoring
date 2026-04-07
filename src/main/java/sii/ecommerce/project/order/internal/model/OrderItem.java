package sii.ecommerce.project.order.internal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor
class OrderItem {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID productId;
    private String snapshotName;
    private BigDecimal snapshotPrice;
    private int quantity;

    public OrderItem(UUID productId, String snapshotName, BigDecimal snapshotPrice, int quantity) {
        this.productId = productId;
        this.snapshotName = snapshotName;
        this.snapshotPrice = snapshotPrice;
        this.quantity = quantity;
    }
}