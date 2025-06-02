-- Script para crear las tablas necesarias
-- Ejecutar después de crear la base de datos y conectarse a loginapp_db
-- Este script elimina las tablas existentes y las recrea desde cero

-- ============================
-- ELIMINAR TABLAS EXISTENTES
-- ============================

-- Eliminar triggers si existen
DROP TRIGGER IF EXISTS update_users_updated_at ON users;

-- Eliminar función si existe
DROP FUNCTION IF EXISTS update_updated_at_column();

-- Eliminar tablas si existen (CASCADE para eliminar dependencias)
DROP TABLE IF EXISTS users CASCADE;

-- ============================
-- CREAR TABLAS
-- ============================

-- Tabla de usuarios
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    needs_password_reset BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- CREAR ÍNDICES
-- ============================

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_is_admin ON users(is_admin);
CREATE INDEX idx_users_needs_password_reset ON users(needs_password_reset);

-- ============================
-- INSERTAR DATOS INICIALES
-- ============================

-- Insertar usuario administrador por defecto
-- NOTA: La contraseña será configurada automáticamente por la aplicación
INSERT INTO users (username, password_hash, salt, is_admin, needs_password_reset) 
VALUES ('admin', 'P3a+OQFNg1tqAayb2530nE8GlVT6PdAJXyGbXy4ET7o=', 'mxws6jYrWLZE8kKdYRADuh3z2fVJGDCiQqov1CRz9xA=', TRUE, FALSE);

-- ============================
-- CREAR FUNCIONES Y TRIGGERS
-- ============================

-- Función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para actualizar updated_at
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- ============================
-- VERIFICACIÓN
-- ============================

-- Mostrar estructura de la tabla creada
\echo '=== ESTRUCTURA DE LA TABLA USERS ==='
\d users

-- Mostrar datos insertados
\echo '=== DATOS INICIALES ==='
SELECT id, username, is_admin, needs_password_reset, created_at FROM users;

-- Mostrar índices creados
\echo '=== ÍNDICES CREADOS ==='
SELECT indexname, indexdef FROM pg_indexes WHERE tablename = 'users';

\echo '=== TABLAS CREADAS EXITOSAMENTE ===' 