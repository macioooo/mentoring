package sii.ecommerce.inventory.internal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumedKafkaMessageId implements Serializable {

    private String topic;
    private int partitionId;
    private long messageOffset;
}
