CREATE TABLE processed_messages (
                                    id            BIGSERIAL    PRIMARY KEY,
                                    message_id    UUID         NOT NULL UNIQUE,
                                    processed_at  TIMESTAMPTZ  NOT NULL
);