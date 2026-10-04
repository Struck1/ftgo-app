CREATE TABLE create_order_saga_state (
    saga_id     UUID         PRIMARY KEY,
    order_id    BIGINT       NOT NULL UNIQUE,
    state       VARCHAR(30)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);
