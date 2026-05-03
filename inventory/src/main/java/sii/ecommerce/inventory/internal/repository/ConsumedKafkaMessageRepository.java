package sii.ecommerce.inventory.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sii.ecommerce.inventory.internal.model.ConsumedKafkaMessage;
import sii.ecommerce.inventory.internal.model.ConsumedKafkaMessageId;

public interface ConsumedKafkaMessageRepository
        extends JpaRepository<ConsumedKafkaMessage, ConsumedKafkaMessageId> {}
