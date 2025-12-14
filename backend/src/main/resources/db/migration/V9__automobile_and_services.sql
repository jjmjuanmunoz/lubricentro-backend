CREATE TABLE IF NOT EXISTS automobiles (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    plate VARCHAR(20) UNIQUE NOT NULL,
    owner_id BIGINT REFERENCES users(id)
    );

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS phone VARCHAR(50),
    ADD COLUMN IF NOT EXISTS address VARCHAR(255);


CREATE TABLE service_records (
                                 id BIGSERIAL PRIMARY KEY,
                                 automobile_id BIGINT NOT NULL REFERENCES automobiles(id) ON DELETE CASCADE,
                                 service_date DATE NOT NULL,
                                 current_km INT NOT NULL,
                                 oil_brand VARCHAR(100),
                                 oil_name VARCHAR(100),
                                 oil_filter BOOLEAN,
                                 air_filter VARCHAR(20),
                                 fuel_filter BOOLEAN,
                                 gearbox_oil BOOLEAN,
                                 differential_oil BOOLEAN,
                                 next_service_km INT
);