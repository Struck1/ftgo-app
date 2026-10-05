CREATE TABLE card_authorizations (
                                     id           BIGSERIAL      PRIMARY KEY,
                                     order_id     BIGINT         NOT NULL UNIQUE,
                                     consumer_id  BIGINT         NOT NULL,
                                     amount       NUMERIC(10,2)  NOT NULL,
                                     status       VARCHAR(20)    NOT NULL,
                                     reason       VARCHAR(255),
                                     created_at   TIMESTAMPTZ    NOT NULL
);

CREATE TABLE processed_messages (
                                    id            BIGSERIAL    PRIMARY KEY,
                                    message_id    UUID         NOT NULL UNIQUE,
                                    processed_at  TIMESTAMPTZ  NOT NULL
);

CREATE TABLE outbox_events (
                               id              BIGSERIAL    PRIMARY KEY,
                               event_id        UUID         NOT NULL UNIQUE,
                               topic           VARCHAR(255) NOT NULL,
                               aggregate_type  VARCHAR(255) NOT NULL,
                               aggregate_id    VARCHAR(255) NOT NULL,
                               event_type      VARCHAR(255) NOT NULL,
                               payload         TEXT         NOT NULL,
                               created_at      TIMESTAMPTZ  NOT NULL,
                               sent_at         TIMESTAMPTZ
);

CREATE INDEX idx_outbox_events_unsent ON outbox_events (id) WHERE sent_at IS NULL;