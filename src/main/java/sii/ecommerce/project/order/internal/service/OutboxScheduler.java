package sii.ecommerce.project.order.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sii.ecommerce.project.order.internal.model.OutboxMessage;
import sii.ecommerce.project.order.internal.repository.OutboxRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;


    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void processOutboxMessages() {
        List<OutboxMessage> pendingMessages = outboxRepository.findByProcessedFalse();

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.debug("Processing {} outbox messages...", pendingMessages.size());

        for (OutboxMessage message : pendingMessages) {

            kafkaTemplate.send("order-events", message.getAggregateId(), message.getPayload());

            message.setProcessed(true);
            outboxRepository.save(message);

            log.info("Event {} from the order {} sent to kafka", message.getEventType(), message.getAggregateId());
        }
    }
}