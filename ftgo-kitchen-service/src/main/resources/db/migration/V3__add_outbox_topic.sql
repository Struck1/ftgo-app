ALTER TABLE outbox_events ADD COLUMN topic VARCHAR(255) NOT NULL DEFAULT 'kitchen-events';
ALTER TABLE outbox_events ALTER COLUMN topic DROP DEFAULT;
