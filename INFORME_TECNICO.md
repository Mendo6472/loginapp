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

### Análisis Detallado de Seguridad PBKDF2

#### Configuración Técnica Implementada
```java
private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
private static final int ITERATIONS = 100000;      // 100,000 iteraciones
private static final int KEY_LENGTH = 256;         // 256 bits
private static final int SALT_LENGTH = 32;         // 32 bytes
```

#### Justificación de Parámetros

**1. Algoritmo PBKDF2WithHmacSHA256**:
- Estándar recomendado por NIST (National Institute of Standards and Technology)
- Utiliza SHA-256 como función hash subyacente
- Resistente a ataques de colisión conocidos

**2. 100,000 Iteraciones**:
- Número recomendado por OWASP para 2023
- Tiempo de procesamiento: ~100ms en hardware moderno
- Balance óptimo entre seguridad y rendimiento
- Resistencia contra ataques de fuerza bruta con hardware especializado

**3. Longitud de Clave 256 bits**:
- Proporciona un nivel de seguridad equivalente a AES-256
- Suficiente para resistir ataques futuros con computación cuántica
- Estándar de la industria para aplicaciones críticas

**4. Salt de 32 bytes**:
- Previene ataques de rainbow table
- Garantiza unicidad incluso con contraseñas idénticas
- Generado criptográficamente seguro con `SecureRandom`

#### Proceso de Hash Implementado

1. **Generación de Salt**: Se genera un salt único de 32 bytes para cada contraseña
2. **Aplicación PBKDF2**: La contraseña se procesa con el salt usando 100,000 iteraciones
3. **Codificación**: El resultado se codifica en Base64 para almacenamiento seguro
4. **Almacenamiento**: Hash y salt se guardan por separado en la base de datos

#### Ventajas de Seguridad Logradas

- ✅ **Resistencia a Rainbow Tables**: Salt único previene ataques precomputados
- ✅ **Resistencia a Fuerza Bruta**: 100,000 iteraciones ralentizan significativamente los ataques
- ✅ **Resistencia a Diccionario**: El tiempo computacional hace inviables los ataques masivos
- ✅ **Escalabilidad Futura**: El número de iteraciones puede incrementarse según evolucione el hardware

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