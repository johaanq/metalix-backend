# Metalix Backend - Sistema de Gestión de Residuos

Backend desarrollado con **Spring Boot 3.5.6** y **Java 21** siguiendo arquitectura **Domain-Driven Design (DDD)** para el sistema de gestión de residuos Metalix.

## 🚂 Despliegue en Railway

Ver [README_RAILWAY.md](README_RAILWAY.md) para instrucciones de despliegue.

## 🏗️ Arquitectura

El proyecto implementa **DDD** con los siguientes **Bounded Contexts**:

- **IAM (Identity Access Management)**: Autenticación, autorización con JWT
- **Municipality**: Gestión de municipalidades y zonas
- **Waste Collection**: Recolectores, colecciones de residuos, sensores IoT
- **Reward**: Sistema de recompensas y transacciones de puntos
- **Monitoring**: Reportes, métricas y alertas del sistema
- **User Identification**: Gestión de tarjetas RFID

### Estructura de Carpetas DDD

```
src/main/java/com/metalix/metalixbackend/
├── shared/                    # Shared Kernel
│   ├── domain/model/         # Clases base (BaseEntity, AuditableModel)
│   ├── exception/            # Excepciones personalizadas
│   └── config/               # Configuraciones (CORS, Swagger, JPA)
│
├── iam/                      # Identity Access Management Context
│   ├── domain/
│   │   ├── model/
│   │   │   ├── aggregates/   # User
│   │   │   └── valueobjects/ # Role
│   │   └── repository/       # UserRepository
│   ├── application/
│   │   └── services/         # AuthenticationService, UserService, JwtService
│   ├── infrastructure/
│   │   └── security/         # SecurityConfiguration, JwtAuthenticationFilter
│   └── interfaces/
│       └── rest/             # Controllers, DTOs
│
├── municipality/             # Municipality Context
├── wastecollection/         # Waste Collection Context
├── reward/                  # Reward Context
├── monitoring/              # Monitoring Context
└── useridentification/      # User Identification Context
```

## 🚀 Tecnologías

- **Spring Boot 3.5.6**
- **Java 21**
- **Spring Data JPA**
- **Spring Security + JWT**
- **MySQL**
- **Lombok**
- **Swagger/OpenAPI 3**
- **Bean Validation**

## 📋 Requisitos Previos

- Java 21+
- Maven 3.8+
- MySQL 8.0+

## ⚙️ Configuración

### 1. Base de Datos MySQL

```sql
CREATE DATABASE metalix_db;
```

### 2. Configurar `application.properties`

Edita `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/metalix_db
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA

# JWT Configuration
jwt.secret=TU_SECRET_KEY_BASE64
jwt.expiration=86400000

# CORS - Agrega tu dominio frontend
cors.allowed-origins=http://localhost:4200,https://tu-dominio.vercel.app
```

### 3. Compilar y Ejecutar

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

## 📚 Documentación API

**Swagger UI**: `http://localhost:8080/swagger-ui.html`

**OpenAPI JSON**: `http://localhost:8080/api-docs`

## 🔐 Autenticación

### Registro de Usuario

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "role": "CITIZEN"
}
```

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Respuesta**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "role": "CITIZEN"
}
```

### Usar el Token

Incluye el token en el header de cada petición:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 🎯 Endpoints Principales

### IAM
- `POST /api/v1/auth/register` - Registrar usuario
- `POST /api/v1/auth/login` - Login
- `GET /api/v1/users` - Listar usuarios (admin)
- `GET /api/v1/users/{id}` - Obtener usuario
- `GET /api/v1/users/{id}/points` - Obtener puntos del usuario

### Municipalities
- `GET /api/v1/municipalities` - Listar municipalidades
- `POST /api/v1/municipalities` - Crear municipalidad (admin)
- `GET /api/v1/zones` - Listar zonas
- `GET /api/v1/zones/municipality/{id}` - Zonas por municipalidad

### Waste Collection
- `GET /api/v1/waste-collectors` - Listar contenedores
- `GET /api/v1/waste-collectors/full` - Contenedores llenos (>80%)
- `POST /api/v1/waste-collections` - Registrar recolección
- `GET /api/v1/waste-collections/user/{id}` - Colecciones por usuario
- `POST /api/v1/sensor-data` - Enviar datos de sensores

### Rewards
- `GET /api/v1/rewards` - Listar recompensas
- `GET /api/v1/rewards/active` - Recompensas activas
- `POST /api/v1/reward-transactions/redeem` - Canjear recompensa
- `GET /api/v1/reward-transactions/user/{id}` - Transacciones de usuario

### Monitoring
- `GET /api/v1/monitoring/reports` - Listar reportes (admin)
- `GET /api/v1/monitoring/metrics` - Obtener métricas
- `GET /api/v1/monitoring/alerts` - Listar alertas
- `GET /api/v1/monitoring/alerts/unread` - Alertas no leídas

### RFID Cards
- `GET /api/v1/rfid-cards/user/{id}` - Tarjeta por usuario
- `POST /api/v1/rfid-cards/use/{cardNumber}` - Usar tarjeta
- `POST /api/v1/rfid-cards/assign` - Asignar tarjeta a usuario

## 👥 Roles y Permisos

- **CITIZEN**: Usuario normal, puede registrar colecciones y canjear recompensas
- **MUNICIPALITY_ADMIN**: Admin municipal, gestiona su municipalidad y contenedores
- **SYSTEM_ADMIN**: Admin del sistema, acceso total

## 🌐 Despliegue

### Railway

1. Crea una cuenta en [Railway.app](https://railway.app)
2. Crea un nuevo proyecto MySQL
3. Conecta tu repositorio GitHub
4. Configura las variables de entorno:
   ```
   SPRING_DATASOURCE_URL=jdbc:mysql://...
   SPRING_DATASOURCE_USERNAME=...
   SPRING_DATASOURCE_PASSWORD=...
   JWT_SECRET=...
   CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app
   ```
5. Deploy automático

### Vercel (usando serverless)

Para Vercel, considera usar Railway para el backend ya que Vercel está optimizado para frontends. Alternativamente, usa Railway, Render o AWS.

## 🔧 Desarrollo

### Agregar nueva Entidad

1. Crear entidad en `domain/model/aggregates` o `entities`
2. Crear repository en `domain/repository`
3. Crear service en `application/services`
4. Crear controller en `interfaces/rest`
5. Crear DTOs si es necesario

### Testing

Consulta la [guía completa de testing](TESTING.md) para información detallada sobre:
- Usuarios de prueba con todos los roles
- Ejecutar tests por módulo
- Probar endpoints manualmente

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=AuthenticationControllerTest
mvn test -Dtest=MunicipalityControllerTest
mvn test -Dtest=WasteCollectorControllerTest
```

#### 🔑 Usuarios de Prueba (password: `password123`)

| Email | Rol | Puntos |
|-------|-----|--------|
| `admin@metalix.com` | SYSTEM_ADMIN | 0 |
| `admin.lima@metalix.com` | MUNICIPALITY_ADMIN | 0 |
| `maria.lopez@email.com` | CITIZEN | 150 |
| `jose.perez@email.com` | CITIZEN | 320 |
| `ana.torres@email.com` | CITIZEN | 200 |

## 📝 Notas Importantes

- El sistema usa **auditing automático** (createdAt, updatedAt)
- Las contraseñas se hashean con **BCrypt**
- Los tokens JWT expiran en **24 horas** por defecto
- **CORS** está configurado para permitir el frontend Angular
- La base de datos se crea automáticamente con **Hibernate DDL**

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/NuevaCaracteristica`)
3. Commit tus cambios (`git commit -m 'Agregar nueva característica'`)
4. Push a la rama (`git push origin feature/NuevaCaracteristica`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia Apache 2.0

## 📞 Contacto

Para preguntas o soporte: support@metalix.com

