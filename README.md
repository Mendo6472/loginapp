# Login App - Sistema de Gestión de Usuarios

## Descripción

Sistema de gestión de usuarios desarrollado en Spring Boot con autenticación segura usando PBKDF2 para hash de contraseñas.

## Características

- **Autenticación PBKDF2**: Hash seguro de contraseñas con salt único
- **Roles**: Administrador y usuarios comunes
- **Gestión de usuarios**: CRUD completo para administradores
- **Cambio de contraseñas**: Los usuarios pueden cambiar sus propias contraseñas

## Tecnologías

- Java 17
- PostgreSQL
- Thymeleaf
- Bootstrap 5

## Instalación

### 1. Base de Datos

```bash
# Crear base de datos y usuario
sudo -u postgres psql
\i database/create_database.sql
\q

# Crear tablas
psql -U loginapp_user -d loginapp_db -h localhost
\i database/create_tables.sql
\q
```

### 2. Ejecutar Aplicación

```bash
./gradlew bootRun
```

### 3. Acceso

- **URL**: http://localhost:8080
- **Admin por defecto**: admin / admin123

## Configuración

La configuración está en `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/loginapp_db
    username: loginapp_user
    password: loginapp_password123
```

## Funcionalidades

### Administrador
- Ver todos los usuarios
- Eliminar usuarios
- Resetear contraseñas

### Usuario
- Ver último login
- Cambiar contraseña
- Dashboard personal

## Estructura

```
src/main/java/com/example/loginapp/
├── controller/          # Controladores web
├── service/            # Lógica de negocio
├── entity/             # Entidades JPA
├── repository/         # Repositorios de datos
└── dto/               # Objetos de transferencia

database/              # Scripts SQL
templates/             # Plantillas HTML
```

## Uso del Sistema

### Registro de Nuevos Usuarios
1. Accede a http://localhost:8080
2. Haz clic en "Registrarse"
3. Completa el formulario con:
   - Nombre de usuario (3-50 caracteres)
   - Contraseña (mínimo 6 caracteres)
   - Confirmación de contraseña
4. Haz clic en "Crear Cuenta"

### Inicio de Sesión
1. Accede a http://localhost:8080
2. Ingresa tu nombre de usuario y contraseña
3. Haz clic en "Iniciar Sesión"

### Funciones de Administrador
1. Inicia sesión con las credenciales de administrador
2. Accede al panel de administración
3. Gestiona usuarios:
   - Ver lista completa de usuarios
   - Eliminar usuarios
   - Resetear contraseñas

### Funciones de Usuario Común
1. Inicia sesión con tu cuenta de usuario
2. Accede a tu dashboard personal
3. Opciones disponibles:
   - Ver información de último login
   - Cambiar contraseña
   - Ver perfil completo

## Seguridad Implementada

### Algoritmo PBKDF2
- **Algoritmo**: PBKDF2WithHmacSHA256
- **Iteraciones**: 100,000
- **Longitud de clave**: 256 bits
- **Longitud de salt**: 32 bytes
- **Codificación**: Base64

### Validaciones
- Validación de entrada en todos los formularios
- Verificación de contraseñas actuales antes de cambios
- Protección contra ataques CSRF (deshabilitado para desarrollo)
- Sesiones seguras con invalidación automática

## Configuración de Desarrollo

### Variables de Entorno
Puedes configurar las siguientes variables de entorno:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/loginapp_db
export DB_USERNAME=loginapp_user
export DB_PASSWORD=loginapp_password123
export SERVER_PORT=8080
```

## Troubleshooting

### Error de Conexión a PostgreSQL
```
Caused by: org.postgresql.util.PSQLException: Connection refused
```
**Solución**: Verifica que PostgreSQL esté ejecutándose y que las credenciales sean correctas.

### Error de Compilación Java
```
Error: Could not find or load main class
```
**Solución**: Asegúrate de usar Java 17 y que JAVA_HOME esté configurado correctamente.

### Error de Gradle
```
Gradle version X is required. Current version is Y
```
**Solución**: Usa el wrapper incluido (`./gradlew`) en lugar de tu instalación local de Gradle.

---

**Nota**: Este es un proyecto educativo que demuestra la implementación de seguridad con PBKDF2 en Spring Boot. Para uso en producción, considera implementaciones adicionales de seguridad según tus necesidades específicas. 