package sii.ecommerce.inventory.internal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "consumed_kafka_messages", schema = "ecommerce_inventory")
@IdClass(ConsumedKafkaMessageId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumedKafkaMessage {

    @Id
    private String topic;

    @Id
    @Column(name = "partition_id")
    private int partitionId;

    @Id
    @Column(name = "message_offset")
    private long messageOffset;
}
