package sii.ecommerce.inventory.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sii.ecommerce.inventory.internal.model.Stock;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {

    Optional<Stock> findByProductId(UUID productId);
}
