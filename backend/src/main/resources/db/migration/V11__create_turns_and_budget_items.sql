-- Create turns table
CREATE TABLE IF NOT EXISTS turns (
    id BIGSERIAL PRIMARY KEY,
    automobile_id BIGINT NOT NULL REFERENCES automobiles(id) ON DELETE CASCADE,
    attended_by_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL,
    turn_number INTEGER NOT NULL,
    scheduled_date DATE,
    scheduled_time TIME,
    arrival_time TIMESTAMP NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    service_record_id BIGINT REFERENCES service_records(id) ON DELETE SET NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create budget_items table
CREATE TABLE IF NOT EXISTS budget_items (
    id BIGSERIAL PRIMARY KEY,
    turn_id BIGINT NOT NULL REFERENCES turns(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    final_price DECIMAL(10, 2),
    included BOOLEAN NOT NULL DEFAULT true,
    notes TEXT
);

-- Create indexes
CREATE INDEX idx_turns_status ON turns(status);
CREATE INDEX idx_turns_scheduled_date ON turns(scheduled_date);
CREATE INDEX idx_turns_arrival_time ON turns(arrival_time);
CREATE INDEX idx_budget_items_turn_id ON budget_items(turn_id);
