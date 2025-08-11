INSERT INTO users (username, email, password_hash, full_name, role, is_active)
VALUES ('admin', 'admin@lubricentro.local', '$2a$10$aMa9LkA5AgqWjkOvfL6cFeZMJ.mZ/x8DbzOYQfJBBrrLxzeTFkcMe', 'Administrator', 'ADMIN', TRUE)
ON CONFLICT (username) DO NOTHING;