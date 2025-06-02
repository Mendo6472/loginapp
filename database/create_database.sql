-- Script para crear la base de datos y usuario en PostgreSQL
-- Ejecutar como superusuario (postgres)
-- Este script es idempotente (se puede ejecutar múltiples veces)

-- Crear usuario para la aplicación (solo si no existe)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_roles WHERE rolname = 'loginapp_user'
    ) THEN
        CREATE USER loginapp_user WITH PASSWORD 'loginapp_password123';
    END IF;
END $$;

-- Crear base de datos (solo si no existe)
SELECT 'CREATE DATABASE loginapp_db OWNER loginapp_user'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'loginapp_db')\gexec

-- Otorgar privilegios
GRANT ALL PRIVILEGES ON DATABASE loginapp_db TO loginapp_user;

-- Conectarse a la base de datos loginapp_db y ejecutar lo siguiente:
\c loginapp_db;

-- Otorgar privilegios en el esquema public
GRANT ALL ON SCHEMA public TO loginapp_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO loginapp_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO loginapp_user;

-- Configurar privilegios por defecto para futuras tablas
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO loginapp_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO loginapp_user; 