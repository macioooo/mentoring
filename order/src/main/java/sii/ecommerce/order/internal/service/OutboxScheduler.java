package sii.ecommerce.order.internal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sii.ecommerce.order.internal.model.OutboxMessage;
import sii.ecommerce.order.internal.repository.OutboxRepository;

import java.util.List;

@Slf4j
@Service
class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String orderEventsTopicName;

    OutboxScheduler(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.order-events-topic:order-events}") String orderEventsTopicName) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.orderEventsTopicName = orderEventsTopicName;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void processOutboxMessages() {
        List<OutboxMessage> pendingOutboxMessages = outboxRepository.findByProcessedFalse();

        if (pendingOutboxMessages.isEmpty()) {
            return;
        }

        log.debug("Processing {} outbox messages...", pendingOutboxMessages.size());

        for (OutboxMessage pendingOutboxMessage : pendingOutboxMessages) {

            kafkaTemplate.send(
                    orderEventsTopicName, pendingOutboxMessage.getAggregateId(), pendingOutboxMessage.getPayload());

            pendingOutboxMessage.setProcessed(true);
            outboxRepository.save(pendingOutboxMessage);

            log.info(
                    "Event {} from the order {} sent to kafka",
                    pendingOutboxMessage.getEventType(),
                    pendingOutboxMessage.getAggregateId());
        }
    }
}
