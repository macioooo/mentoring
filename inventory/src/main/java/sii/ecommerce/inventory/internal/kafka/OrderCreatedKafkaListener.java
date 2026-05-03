package sii.ecommerce.inventory.internal.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sii.ecommerce.inventory.internal.service.OrderInventoryEventProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
class OrderCreatedKafkaListener {

    private final OrderInventoryEventProcessor orderInventoryEventProcessor;

    @KafkaListener(
            topics = "${app.kafka.order-events-topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, String> consumerRecord) {
        try {
            orderInventoryEventProcessor.process(
                    consumerRecord.topic(),
                    consumerRecord.partition(),
                    consumerRecord.offset(),
                    consumerRecord.value());
        } catch (JsonProcessingException exception) {
            log.error(
                    "Failed to parse order event, key={} value={}",
                    consumerRecord.key(),
                    consumerRecord.value(),
                    exception);
            throw new IllegalStateException("Unprocessable Kafka message", exception);
        }
    }
}
