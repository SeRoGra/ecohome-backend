-- =====================================================
-- EcoHome Store — Script de inicialización de BD
-- Se ejecuta al arrancar si spring.sql.init.mode=always
-- =====================================================

-- Extensión para gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ── Tabla users ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id            UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(100)  NOT NULL,
    email         VARCHAR(255)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    role          VARCHAR(20)   NOT NULL DEFAULT 'CLIENT'
                  CHECK (role IN ('ADMIN','CLIENT')),
    created_at    TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- ── Tabla products ────────────────────────────────────
CREATE TABLE IF NOT EXISTS products (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255)   NOT NULL,
    price       NUMERIC(10,2)  NOT NULL CHECK (price > 0),
    stock       INTEGER        NOT NULL DEFAULT 0 CHECK (stock >= 0),
    available   BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Nota: updated_at se actualiza desde PostgresProductAdapter.java
-- El trigger PL/pgSQL con $$ no es compatible con el parser de R2DBC.

-- ── Datos semilla ─────────────────────────────────────
INSERT INTO products (name, price, stock) VALUES
    ('Vaso de vidrio reciclado 350ml',  12500.00, 50),
    ('Plato biodegradable 24cm',         8900.00, 120),
    ('Set cubiertos ecológicos x4',     25000.00, 30),
    ('Bolsa reutilizable tela L',        6500.00, 200),
    ('Termo bambú 500ml',               35000.00, 45)
ON CONFLICT DO NOTHING;