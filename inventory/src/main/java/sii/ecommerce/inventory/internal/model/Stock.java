package sii.ecommerce.inventory.internal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "stock", schema = "ecommerce_inventory")
@Getter
@NoArgsConstructor
public class Stock {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID productId;
    private int availableQuantity;
    private int reservedQuantity;

    public Stock(UUID productId, int availableQuantity, int reservedQuantity) {
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
    }

    public void reserve(int quantity) {
        if (availableQuantity < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock for product " + productId + ": need " + quantity + ", have " + availableQuantity);
        }
        availableQuantity -= quantity;
        reservedQuantity += quantity;
    }
}
