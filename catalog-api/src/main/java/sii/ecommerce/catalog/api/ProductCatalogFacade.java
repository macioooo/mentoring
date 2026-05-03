package sii.ecommerce.catalog.api;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductCatalogFacade {

    ProductSnapshot fetchProductDetails(UUID productId);

    record ProductSnapshot(String name, BigDecimal price) {}
}
