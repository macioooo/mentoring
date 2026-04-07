package sii.ecommerce.project.order.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sii.ecommerce.project.order.internal.model.Order;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}