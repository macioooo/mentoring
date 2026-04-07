package sii.ecommerce.project.order.internal.client;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductCatalogClient {
    ProductSnapshot fetchProductDetails(UUID productId);

    record ProductSnapshot(String name, BigDecimal price) {}
}