# Informe Técnico - Sistema de Login

## Información del Proyecto

**Tecnología Principal**: Spring Boot 3.0.13  
**Base de Datos**: PostgreSQL  
**Despliegue**: Salón 104M, PC 01  
**Algoritmo de Seguridad**: PBKDF2 con 100,000 iteraciones

---

## Implementación

### Tecnologías Utilizadas
- **Backend**: Spring Boot con Spring Security
- **Base de Datos**: PostgreSQL
- **Frontend**: Thymeleaf + Bootstrap 5
- **Seguridad**: PBKDF2WithHmacSHA256

### Funcionalidades Desarrolladas
- ✅ Sistema de login y registro de usuarios
- ✅ Roles de administrador y usuario común
- ✅ Hash seguro de contraseñas con PBKDF2 (100,000 iteraciones)
- ✅ Gestión de usuarios para administradores
- ✅ Cambio de contraseñas para usuarios

---

## Dificultades Principales

### Problema de Reset de Contraseñas
La mayor dificultad fue implementar el sistema de reset de contraseñas. El requisito era que cuando un administrador resetee la contraseña de un usuario, este pueda ingresar con cualquier contraseña para luego cambiarla.

**Implementación actual**:
- Se marca al usuario con un flag `needsPasswordReset = true`
- El usuario puede ingresar con cualquier contraseña hasta que establezca una nueva

**Vulnerabilidad identificada**:
- ⚠️ **Ventana de seguridad**: Entre el reset del admin y el cambio del usuario, cualquier persona podría acceder a la cuenta
- ⚠️ **Falta de notificación**: El usuario no es informado automáticamente del reset
- ⚠️ **Sin verificación adicional**: No hay autenticación por email o SMS

### Otras Dificultades
- Configuración de PostgreSQL en red local
- Despliegue como archivo WAR en Tomcat
- Sincronización de validaciones frontend-backend

---

## Conclusiones

### Logros
✅ Sistema funcional desplegado exitosamente  
✅ Implementación correcta de PBKDF2 para seguridad de contraseñas  
✅ Interfaz responsive y amigable  
✅ Arquitectura MVC bien estructurada  

### Lecciones Aprendidas
1. **Seguridad vs Usabilidad**: El balance entre facilidad de uso y seguridad es complejo
2. **Vulnerabilidades imprevistas**: Funcionalidades aparentemente simples pueden introducir riesgos de seguridad
3. **Importancia de la documentación**: Documentar decisiones técnicas es crucial para futuras mejoras

### Recomendaciones Futuras
- Implementar tokens temporales para reset de contraseñas
- Añadir notificaciones automáticas por email
- Considerar autenticación multifactor
- Implementar logging de seguridad detallado


El proyecto cumplió exitosamente sus objetivos principales. La vulnerabilidad identificada en el reset de contraseñas representa una valiosa lección sobre los desafíos reales en el desarrollo de sistemas seguros. 