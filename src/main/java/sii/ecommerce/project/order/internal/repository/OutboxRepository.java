package sii.ecommerce.project.order.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sii.ecommerce.project.order.internal.model.OutboxMessage;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {


    List<OutboxMessage> findByProcessedFalse();
}