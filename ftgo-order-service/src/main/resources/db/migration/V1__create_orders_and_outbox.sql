CREATE TABLE orders (
                        id             BIGSERIAL    PRIMARY KEY,
                        consumer_id    BIGINT       NOT NULL,
                        restaurant_id  BIGINT       NOT NULL,
                        total_amount   NUMERIC(10,2) NOT NULL,
                        status         VARCHAR(30)  NOT NULL,
                        created_at     TIMESTAMPTZ  NOT NULL
);

CREATE TABLE outbox_events (
                               id              BIGSERIAL    PRIMARY KEY,
                               event_id        UUID         NOT NULL UNIQUE,
                               aggregate_type  VARCHAR(255) NOT NULL,
                               aggregate_id    VARCHAR(255) NOT NULL,
                               event_type      VARCHAR(255) NOT NULL,
                               payload         TEXT         NOT NULL,
                               created_at      TIMESTAMPTZ  NOT NULL,
                               sent_at         TIMESTAMPTZ
);

CREATE INDEX idx_outbox_events_unsent ON outbox_events (id) WHERE sent_at IS NULL;