CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        client_id UUID NOT NULL,
                        status VARCHAR(50) NOT NULL
);

CREATE TABLE order_items (
                             id UUID PRIMARY KEY,
                             order_id UUID NOT NULL,
                             product_id UUID NOT NULL,
                             snapshot_name VARCHAR(255) NOT NULL,
                             snapshot_price DECIMAL(10, 2) NOT NULL,
                             quantity INT NOT NULL,
                             CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE outbox_messages (
                                 id UUID PRIMARY KEY,
                                 aggregate_type VARCHAR(255) NOT NULL,
                                 aggregate_id VARCHAR(255) NOT NULL,
                                 event_type VARCHAR(255) NOT NULL,
                                 payload JSONB NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                 processed BOOLEAN NOT NULL DEFAULT FALSE
);