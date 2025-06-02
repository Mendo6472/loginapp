# Scripts de Base de Datos - Login App

## Instalación Nueva

Para una instalación completamente nueva:

1. **Crear base de datos y usuario:**
   ```bash
   # Conectarse como superusuario postgres
   psql -U postgres
   
   # Ejecutar el script de creación
   \i create_database.sql
   ```

2. **Crear tablas:**
   ```bash
   # Conectarse a la base de datos de la aplicación
   psql -U loginapp_user -d loginapp_db
   
   # Ejecutar el script de tablas
   \i create_tables.sql
   ```

## ⚠️ IMPORTANTE: Reset Completo de Base de Datos

**El script `create_tables.sql` ahora ELIMINA todas las tablas existentes y las recrea desde cero.**

### Para un Reset Completo (⚠️ BORRA TODOS LOS DATOS):
```bash
# Conectarse a la base de datos
psql -U loginapp_user -d loginapp_db

# Este comando ELIMINARÁ todos los datos existentes
\i create_tables.sql
```

### ⚠️ Advertencia
- **El script `create_tables.sql` BORRARÁ TODOS LOS DATOS existentes**
- Solo úsalo si quieres empezar desde cero

## Estructura de la Tabla `users`

Después de ejecutar los scripts, la tabla `users` tendrá la siguiente estructura:

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Verificación

Para verificar que todo está configurado correctamente:

```sql
-- Verificar estructura de la tabla
\d users

-- Verificar índices
SELECT indexname, indexdef 
FROM pg_indexes 
WHERE tablename = 'users';
```

## Resumen de Scripts

| Script | Descripción | ⚠️ Peligro |
|--------|-------------|-----------|
| `create_database.sql` | Crea la base de datos y usuario (idempotente) | ✅ Seguro |
| `create_tables.sql` | **ELIMINA y recrea todas las tablas** | ⚠️ BORRA DATOS | 