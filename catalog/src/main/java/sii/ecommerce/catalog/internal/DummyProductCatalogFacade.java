package sii.ecommerce.catalog.internal;

import org.springframework.stereotype.Component;
import sii.ecommerce.catalog.api.ProductCatalogFacade;

import java.math.BigDecimal;
import java.util.UUID;

@Component
class DummyProductCatalogFacade implements ProductCatalogFacade {

    @Override
    public ProductSnapshot fetchProductDetails(UUID productId) {
        return new ProductSnapshot("Random mocked laptop", BigDecimal.valueOf(4999.00));
    }
}
