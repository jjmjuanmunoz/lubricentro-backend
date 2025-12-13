-- ============================================================
-- V10: Create Permissions, Roles, and Dynamic Menu System
-- ============================================================

-- Step 1: Create permissions table
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_permissions_code ON permissions(code);

-- Step 2: Create roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_roles_name ON roles(name);

-- Step 3: Create role_permissions join table
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permissions_role ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission ON role_permissions(permission_id);

-- Step 4: Create user_roles join table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);

-- Step 5: Create menus table
CREATE TABLE menus (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    url VARCHAR(255),
    type VARCHAR(20) NOT NULL CHECK (type IN ('section', 'collapsible', 'item')),
    icon VARCHAR(50),
    permission_code VARCHAR(100),
    parent_id BIGINT REFERENCES menus(id) ON DELETE CASCADE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (permission_code) REFERENCES permissions(code) ON DELETE SET NULL
);

CREATE INDEX idx_menus_parent ON menus(parent_id);
CREATE INDEX idx_menus_permission ON menus(permission_code);
CREATE INDEX idx_menus_order ON menus(display_order);

-- ============================================================
-- Step 6: Seed Permissions
-- ============================================================

INSERT INTO permissions (code, description) VALUES
-- Dashboard
('dashboard:read', 'Ver dashboard principal'),

-- Facturación
('invoice:read', 'Ver facturas'),
('invoice:create', 'Crear facturas'),
('invoice:update', 'Modificar facturas'),
('invoice:delete', 'Eliminar facturas'),

-- Clientes
('client:read', 'Ver clientes'),
('client:create', 'Crear clientes'),
('client:update', 'Modificar clientes'),
('client:delete', 'Eliminar clientes'),

-- Productos
('product:read', 'Ver productos'),
('product:create', 'Crear productos'),
('product:update', 'Modificar productos'),
('product:delete', 'Eliminar productos'),

-- Reportes
('report:read', 'Ver reportes'),
('report:iva', 'Ver libro IVA ventas'),
('report:monthly', 'Ver resumen mensual'),

-- AFIP
('afip:read', 'Ver información AFIP'),
('afip:status', 'Ver estado de servicios AFIP'),
('afip:puntosventa', 'Gestionar puntos de venta'),

-- Configuración
('config:read', 'Ver configuración'),
('config:empresa', 'Configurar datos de empresa'),
('config:users', 'Gestionar usuarios'),
('config:certificates', 'Gestionar certificados AFIP');

-- ============================================================
-- Step 7: Seed Roles
-- ============================================================

INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Administrador con acceso total al sistema'),
('ROLE_FACTURADOR', 'Usuario de facturación con acceso a clientes y facturas'),
('ROLE_CONTADOR', 'Contador con acceso a reportes y consultas');

-- ============================================================
-- Step 8: Assign Permissions to Roles
-- ============================================================

-- ROLE_ADMIN: All permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_ADMIN';

-- ROLE_FACTURADOR: invoice:*, client:*, product:read, dashboard:read
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_FACTURADOR'
  AND (p.code LIKE 'invoice:%'
       OR p.code LIKE 'client:%'
       OR p.code = 'product:read'
       OR p.code = 'dashboard:read');

-- ROLE_CONTADOR: report:*, dashboard:read, invoice:read, client:read
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_CONTADOR'
  AND (p.code LIKE 'report:%'
       OR p.code = 'dashboard:read'
       OR p.code = 'invoice:read'
       OR p.code = 'client:read');

-- ============================================================
-- Step 9: Migrate Existing Users
-- ============================================================

-- Assign ROLE_ADMIN to all users with role='ADMIN'
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.role = 'ADMIN' AND r.name = 'ROLE_ADMIN';

-- Handle any FACTURADOR users if they exist
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.role = 'FACTURADOR' AND r.name = 'ROLE_FACTURADOR';

-- Handle any CONTADOR users if they exist
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.role = 'CONTADOR' AND r.name = 'ROLE_CONTADOR';

-- Safety check: Assign ROLE_ADMIN to any orphaned users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE r.name = 'ROLE_ADMIN'
  AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id);

-- ============================================================
-- Step 10: Seed Menu Structure
-- ============================================================

-- Root level menus
INSERT INTO menus (id, title, url, type, icon, permission_code, parent_id, display_order) VALUES
(1, 'Dashboard', '/dashboard', 'item', 'dashboard', NULL, NULL, 1),
(2, 'Facturación', NULL, 'collapsible', 'receipt', 'invoice:read', NULL, 2),
(3, 'Clientes', NULL, 'collapsible', 'people', 'client:read', NULL, 3),
(4, 'Productos', '/productos', 'item', 'inventory', 'product:read', NULL, 4),
(5, 'Reportes', NULL, 'collapsible', 'assessment', 'report:read', NULL, 5),
(6, 'AFIP', NULL, 'collapsible', 'account_balance', 'afip:read', NULL, 6),
(7, 'Configuración', NULL, 'collapsible', 'settings', 'config:read', NULL, 7);

-- Facturación submenu
INSERT INTO menus (id, title, url, type, icon, permission_code, parent_id, display_order) VALUES
(8, 'Nueva Factura', '/facturacion/nueva', 'item', NULL, 'invoice:create', 2, 1),
(9, 'Listado', '/facturacion/listado', 'item', NULL, 'invoice:read', 2, 2),
(10, 'Notas Crédito/Débito', '/facturacion/notas', 'item', NULL, 'invoice:create', 2, 3);

-- Clientes submenu
INSERT INTO menus (id, title, url, type, icon, permission_code, parent_id, display_order) VALUES
(11, 'Listado', '/clientes/listado', 'item', NULL, 'client:read', 3, 1),
(12, 'Nuevo Cliente', '/clientes/nuevo', 'item', NULL, 'client:create', 3, 2);

-- Reportes submenu
INSERT INTO menus (id, title, url, type, icon, permission_code, parent_id, display_order) VALUES
(13, 'Libro IVA Ventas', '/reportes/iva-ventas', 'item', NULL, 'report:iva', 5, 1),
(14, 'Resumen Mensual', '/reportes/mensual', 'item', NULL, 'report:monthly', 5, 2);

-- AFIP submenu
INSERT INTO menus (id, title, url, type, icon, permission_code, parent_id, display_order) VALUES
(15, 'Estado Servicios', '/afip/status', 'item', NULL, 'afip:status', 6, 1),
(16, 'Puntos de Venta', '/afip/puntos-venta', 'item', NULL, 'afip:puntosventa', 6, 2);

-- Configuración submenu
INSERT INTO menus (id, title, url, type, icon, permission_code, parent_id, display_order) VALUES
(17, 'Datos Empresa', '/config/empresa', 'item', NULL, 'config:empresa', 7, 1),
(18, 'Usuarios', '/config/usuarios', 'item', NULL, 'config:users', 7, 2),
(19, 'Certificados AFIP', '/config/certificados', 'item', NULL, 'config:certificates', 7, 3);

-- Update sequence for menus
SELECT setval('menus_id_seq', 19, true);

-- ============================================================
-- Step 11: Drop old role column from users table
-- ============================================================

ALTER TABLE users DROP COLUMN role;

-- ============================================================
-- Migration Complete
-- ============================================================
