CREATE TABLE tickets (
                         id             BIGSERIAL    PRIMARY KEY,
                         order_id       BIGINT       NOT NULL,
                         restaurant_id  BIGINT       NOT NULL,
                         status         VARCHAR(30)  NOT NULL,
                         created_at     TIMESTAMPTZ  NOT NULL
);

CREATE TABLE processed_messages (
                                    id            BIGSERIAL    PRIMARY KEY,
                                    message_id    UUID         NOT NULL UNIQUE,
                                    processed_at  TIMESTAMPTZ  NOT NULL
);