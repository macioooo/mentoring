package sii.ecommerce.inventory.internal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sii.ecommerce.inventory.internal.model.ConsumedKafkaMessage;
import sii.ecommerce.inventory.internal.model.ConsumedKafkaMessageId;
import sii.ecommerce.inventory.internal.repository.ConsumedKafkaMessageRepository;
import sii.ecommerce.order.api.events.OrderCreatedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderInventoryEventProcessor {

    private final ConsumedKafkaMessageRepository consumedKafkaMessageRepository;
    private final ObjectMapper objectMapper;
    private final StockReservationService stockReservationService;

    @Transactional
    public void process(String topic, int partition, long offset, String json) throws JsonProcessingException {
        ConsumedKafkaMessageId kafkaMessageIdentity = new ConsumedKafkaMessageId(topic, partition, offset);
        if (consumedKafkaMessageRepository.existsById(kafkaMessageIdentity)) {
            log.debug("Skipping Kafka message already applied: topic={} partition={} offset={}", topic, partition, offset);
            return;
        }

        OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(json, OrderCreatedEvent.class);
        stockReservationService.reserveForOrderCreated(orderCreatedEvent);
        consumedKafkaMessageRepository.save(new ConsumedKafkaMessage(topic, partition, offset));
        log.info("Reserved stock for order {}", orderCreatedEvent.orderId());
    }
}
