CREATE TABLE IF NOT EXISTS products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          type VARCHAR(50),
                          brand VARCHAR(100),
                          viscosity VARCHAR(20),
                          unit_of_measure VARCHAR(50),
                          stock_quantity INTEGER,
                          minimum_stock INTEGER,
                          unit_price NUMERIC(10, 2),
                          active BOOLEAN DEFAULT TRUE
);