-- Script SQL para testear el sistema de autenticación
-- IMPORTANTE: La tabla 'users' ya fue creada automáticamente por JPA/Hibernate
-- Los nombres de columnas usan snake_case (account_non_expired, etc.)

-- Usuario de prueba 1
-- username: admin
-- password: admin
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('admin', '$2a$10$mfIHS4y0i/Tvqw3I/eIJ0O3d76vsVSoHlC8bB3wOyd7GTPe6G0ep.', 'admin@example.com', 1, 1, 1, 1);

-- Usuario de prueba 2
-- username: cuenta
-- password: cuenta1
INSERT INTO users (username, password, email, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('cuenta', '$2a$10$kEcOoofkVLnK7FAyvBSSn.xtSybfh6gkL4tJ/q1c5.6uujWNDtWVm', 'cuenta@example.com', 1, 1, 1, 1);
