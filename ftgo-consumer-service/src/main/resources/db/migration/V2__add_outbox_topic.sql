ALTER TABLE outbox_events ADD COLUMN topic VARCHAR(255) NOT NULL DEFAULT 'consumer-events';
ALTER TABLE outbox_events ALTER COLUMN topic DROP DEFAULT;
