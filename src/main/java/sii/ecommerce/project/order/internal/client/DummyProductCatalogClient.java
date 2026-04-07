package sii.ecommerce.project.order.internal.client;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;


@Component
class DummyProductCatalogClient implements ProductCatalogClient {

    @Override
    public ProductSnapshot fetchProductDetails(UUID productId) {

        return new ProductSnapshot("Random mocked laptop", BigDecimal.valueOf(4999.00));
    }
}