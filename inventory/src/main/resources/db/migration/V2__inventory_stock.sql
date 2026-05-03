CREATE SCHEMA IF NOT EXISTS ecommerce_inventory;

CREATE TABLE ecommerce_inventory.stock (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL UNIQUE,
    available_quantity INT NOT NULL,
    reserved_quantity INT NOT NULL DEFAULT 0
);

CREATE TABLE ecommerce_inventory.consumed_kafka_messages (
    topic VARCHAR(255) NOT NULL,
    partition_id INT NOT NULL,
    message_offset BIGINT NOT NULL,
    PRIMARY KEY (topic, partition_id, message_offset)
);
