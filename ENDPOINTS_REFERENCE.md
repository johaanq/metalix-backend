# 📚 REFERENCIA COMPLETA DE ENDPOINTS - METALIX BACKEND

## 🎯 Total: 74 Endpoints

Base URL: `http://localhost:8080/api/v1`

---

## 🏠 HOME (1 endpoint)

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| GET | `/` | ❌ | Verificar estado del servidor |

---

## 🔐 AUTHENTICATION (2 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| POST | `/auth/register` | ❌ | - | Registrar nuevo usuario |
| POST | `/auth/login` | ❌ | - | Iniciar sesión y obtener token |

---

## 👥 USERS (11 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/users` | ✅ | ADMIN* | Listar todos los usuarios |
| GET | `/users/{id}` | ✅ | ALL | Obtener usuario por ID |
| GET | `/users/{id}/points` | ✅ | ALL | Obtener puntos del usuario |
| GET | `/users/role/{role}` | ✅ | ADMIN* | Obtener usuarios por rol |
| GET | `/users/municipality/{municipalityId}` | ✅ | ADMIN* | Usuarios por municipalidad |
| GET | `/users/{id}/profile` | ✅ | ALL | Perfil completo del usuario |
| GET | `/users/{id}/stats` | ✅ | ALL | Estadísticas del usuario |
| GET | `/users/{id}/activity` | ✅ | ALL | Historial de actividad |
| PUT | `/users/{id}` | ✅ | ALL | Actualizar usuario |
| PATCH | `/users/{id}/deactivate` | ✅ | ADMIN* | Desactivar usuario |
| DELETE | `/users/{id}` | ✅ | SYS_ADMIN | Eliminar usuario |

\* ADMIN = SYSTEM_ADMIN o MUNICIPALITY_ADMIN

---

## 🏛️ MUNICIPALITIES (8 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/municipalities` | ✅ | ALL | Listar todas las municipalidades |
| GET | `/municipalities/{id}` | ✅ | ALL | Obtener municipalidad por ID |
| GET | `/municipalities/code/{code}` | ✅ | ALL | Obtener municipalidad por código |
| GET | `/municipalities/{id}/stats` | ✅ | ALL | Estadísticas de municipalidad |
| GET | `/municipalities/{id}/dashboard` | ✅ | ALL | Dashboard de municipalidad |
| POST | `/municipalities` | ✅ | SYS_ADMIN | Crear nueva municipalidad |
| PUT | `/municipalities/{id}` | ✅ | ADMIN* | Actualizar municipalidad |
| DELETE | `/municipalities/{id}` | ✅ | SYS_ADMIN | Eliminar municipalidad |

---

## 📍 ZONES (6 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/zones` | ✅ | ALL | Listar todas las zonas |
| GET | `/zones/{id}` | ✅ | ALL | Obtener zona por ID |
| GET | `/zones/municipality/{municipalityId}` | ✅ | ALL | Listar zonas por municipalidad |
| POST | `/zones` | ✅ | ADMIN* | Crear nueva zona |
| PUT | `/zones/{id}` | ✅ | ADMIN* | Actualizar zona |
| DELETE | `/zones/{id}` | ✅ | ADMIN* | Eliminar zona |

---

## 🗑️ WASTE COLLECTORS (9 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/waste-collectors` | ✅ | ALL | Listar todos los contenedores |
| GET | `/waste-collectors/{id}` | ✅ | ALL | Obtener contenedor por ID |
| GET | `/waste-collectors/municipality/{municipalityId}` | ✅ | ALL | Contenedores por municipalidad |
| GET | `/waste-collectors/zone/{zoneId}` | ✅ | ALL | Contenedores por zona |
| GET | `/waste-collectors/full` | ✅ | ALL | Contenedores llenos (>80%) |
| POST | `/waste-collectors` | ✅ | ADMIN* | Crear nuevo contenedor |
| PUT | `/waste-collectors/{id}` | ✅ | ADMIN* | Actualizar contenedor |
| PATCH | `/waste-collectors/{id}/empty` | ✅ | ADMIN* | Vaciar contenedor |
| DELETE | `/waste-collectors/{id}` | ✅ | ADMIN* | Eliminar contenedor |

---

## ♻️ WASTE COLLECTIONS (8 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/waste-collections` | ✅ | ALL | Listar todas las recolecciones |
| GET | `/waste-collections/{id}` | ✅ | ALL | Obtener recolección por ID |
| GET | `/waste-collections/user/{userId}?page=X&size=Y` | ✅ | ALL | Recolecciones por usuario (paginado) |
| GET | `/waste-collections/collector/{collectorId}` | ✅ | ALL | Recolecciones por contenedor |
| GET | `/waste-collections/municipality/{municipalityId}` | ✅ | ALL | Recolecciones por municipalidad |
| POST | `/waste-collections` | ✅ | ALL | Registrar nueva recolección |
| PUT | `/waste-collections/{id}` | ✅ | ALL | Actualizar recolección |
| DELETE | `/waste-collections/{id}` | ✅ | ALL | Eliminar recolección |

---

## 📊 SENSOR DATA (6 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/sensor-data` | ✅ | ADMIN* | Listar todos los datos de sensores |
| GET | `/sensor-data/{id}` | ✅ | ALL | Obtener dato de sensor por ID |
| GET | `/sensor-data/collector/{collectorId}` | ✅ | ALL | Datos por contenedor |
| GET | `/sensor-data/collector/{collectorId}/latest` | ✅ | ALL | Último dato del contenedor |
| POST | `/sensor-data` | ✅ | ALL | Registrar dato de sensor |
| DELETE | `/sensor-data/{id}` | ✅ | SYS_ADMIN | Eliminar dato de sensor |

---

## 🎁 REWARDS (8 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/rewards` | ✅ | ALL | Listar todas las recompensas |
| GET | `/rewards/{id}` | ✅ | ALL | Obtener recompensa por ID |
| GET | `/rewards/active` | ✅ | ALL | Listar recompensas activas |
| GET | `/rewards/municipality/{municipalityId}` | ✅ | ALL | Recompensas por municipalidad |
| GET | `/rewards/affordable/{maxPoints}` | ✅ | ALL | Recompensas asequibles (≤ puntos) |
| POST | `/rewards` | ✅ | ADMIN* | Crear nueva recompensa |
| PUT | `/rewards/{id}` | ✅ | ADMIN* | Actualizar recompensa |
| DELETE | `/rewards/{id}` | ✅ | ADMIN* | Eliminar recompensa |

---

## 💰 REWARD TRANSACTIONS (5 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/reward-transactions` | ✅ | ADMIN* | Listar todas las transacciones |
| GET | `/reward-transactions/{id}` | ✅ | ALL | Obtener transacción por ID |
| GET | `/reward-transactions/user/{userId}?page=X&size=Y` | ✅ | ALL | Transacciones por usuario (paginado) |
| POST | `/reward-transactions/redeem` | ✅ | ALL | Canjear recompensa |
| DELETE | `/reward-transactions/{id}` | ✅ | SYS_ADMIN | Eliminar transacción |

**Body para redeem:**
```json
{
  "userId": 3,
  "rewardId": 1
}
```

---

## 💳 RFID CARDS (10 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/rfid-cards` | ✅ | ADMIN* | Listar todas las tarjetas RFID |
| GET | `/rfid-cards/{id}` | ✅ | ALL | Obtener tarjeta por ID |
| GET | `/rfid-cards/number/{cardNumber}` | ✅ | ALL | Obtener tarjeta por número |
| GET | `/rfid-cards/user/{userId}` | ✅ | ALL | Obtener tarjeta de usuario |
| POST | `/rfid-cards` | ✅ | ADMIN* | Crear nueva tarjeta RFID |
| POST | `/rfid-cards/assign` | ✅ | ADMIN* | Asignar tarjeta a usuario |
| POST | `/rfid-cards/use/{cardNumber}` | ✅ | ALL | Usar tarjeta RFID |
| PUT | `/rfid-cards/{id}` | ✅ | ADMIN* | Actualizar tarjeta |
| PATCH | `/rfid-cards/{id}/block` | ✅ | ADMIN* | Bloquear tarjeta |
| DELETE | `/rfid-cards/{id}` | ✅ | SYS_ADMIN | Eliminar tarjeta |

**Body para assign:**
```json
{
  "cardNumber": "RFID001",
  "userId": 3
}
```

---

## 📈 MONITORING (10 endpoints)

### Reports (3 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/monitoring/reports` | ✅ | ADMIN* | Listar todos los reportes |
| GET | `/monitoring/reports/{id}` | ✅ | ADMIN* | Obtener reporte por ID |
| GET | `/monitoring/reports/municipality/{municipalityId}` | ✅ | ADMIN* | Reportes por municipalidad |
| POST | `/monitoring/reports` | ✅ | ADMIN* | Crear nuevo reporte |

### Metrics (4 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/monitoring/metrics` | ✅ | ALL | Listar todas las métricas |
| GET | `/monitoring/metrics/{id}` | ✅ | ALL | Obtener métrica por ID |
| GET | `/monitoring/metrics/municipality/{municipalityId}` | ✅ | ALL | Métricas por municipalidad |
| POST | `/monitoring/metrics` | ✅ | ADMIN* | Crear nueva métrica |

### Alerts (5 endpoints)

| Método | Endpoint | Auth | Role | Descripción |
|--------|----------|------|------|-------------|
| GET | `/monitoring/alerts` | ✅ | ALL | Listar todas las alertas |
| GET | `/monitoring/alerts/{id}` | ✅ | ALL | Obtener alerta por ID |
| GET | `/monitoring/alerts/municipality/{municipalityId}` | ✅ | ALL | Alertas por municipalidad |
| GET | `/monitoring/alerts/unread` | ✅ | ALL | Listar alertas no leídas |
| POST | `/monitoring/alerts` | ✅ | ADMIN* | Crear nueva alerta |

---

## 🔑 ROLES Y PERMISOS

| Role | Descripción | Permisos |
|------|-------------|----------|
| **SYSTEM_ADMIN** | Administrador del sistema | ✅ Acceso completo a todo |
| **MUNICIPALITY_ADMIN** | Administrador municipal | ✅ Gestiona su municipalidad<br>✅ Gestiona zonas<br>✅ Gestiona contenedores<br>✅ Ve reportes |
| **CITIZEN** | Ciudadano | ✅ Ve información pública<br>✅ Registra recolecciones<br>✅ Canjea recompensas<br>✅ Ve su perfil y puntos |

---

## 📝 TIPOS DE DATOS

### Roles
- `SYSTEM_ADMIN`
- `MUNICIPALITY_ADMIN`
- `CITIZEN`

### Material Types
- `METAL`
- `PLASTIC`
- `PAPER`
- `GLASS`

### Reward Categories
- `SHOPPING`
- `ENTERTAINMENT`
- `DINING`
- `SERVICES`
- `DISCOUNTS`

### Zone Types
- `RESIDENTIAL`
- `COMMERCIAL`
- `INDUSTRIAL`
- `MIXED`

### Collector Status
- `ACTIVE`
- `INACTIVE`
- `MAINTENANCE`

### Collection Status
- `PENDING`
- `COMPLETED`
- `CANCELLED`

### Transaction Types
- `EARNED`
- `REDEEMED`
- `EXPIRED`
- `REFUNDED`

### Alert Severity
- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`

---

## 📊 ESTADÍSTICAS

### Por Módulo

| Módulo | GET | POST | PUT | PATCH | DELETE | Total |
|--------|-----|------|-----|-------|--------|-------|
| Home | 1 | 0 | 0 | 0 | 0 | 1 |
| Authentication | 0 | 2 | 0 | 0 | 0 | 2 |
| Users | 8 | 0 | 1 | 1 | 1 | 11 |
| Municipalities | 5 | 1 | 1 | 0 | 1 | 8 |
| Zones | 3 | 1 | 1 | 0 | 1 | 6 |
| Waste Collectors | 5 | 1 | 1 | 1 | 1 | 9 |
| Waste Collections | 5 | 1 | 1 | 0 | 1 | 8 |
| Sensor Data | 4 | 1 | 0 | 0 | 1 | 6 |
| Rewards | 5 | 1 | 1 | 0 | 1 | 8 |
| Reward Transactions | 3 | 1 | 0 | 0 | 1 | 5 |
| RFID Cards | 4 | 3 | 1 | 1 | 1 | 10 |
| Monitoring | 10 | 0 | 0 | 0 | 0 | 10 |
| **TOTAL** | **53** | **12** | **7** | **3** | **9** | **74** |

### Por Método HTTP

- **GET**: 53 endpoints (71.6%)
- **POST**: 12 endpoints (16.2%)
- **PUT**: 7 endpoints (9.5%)
- **PATCH**: 3 endpoints (4.1%)
- **DELETE**: 9 endpoints (12.2%)

### Por Autenticación

- **Públicos** (sin auth): 3 endpoints (4.1%)
- **Autenticados**: 71 endpoints (95.9%)

### Por Rol Requerido

- **ALL**: 49 endpoints (66.2%)
- **ADMIN** (SYS o MUNIC): 16 endpoints (21.6%)
- **SYSTEM_ADMIN**: 6 endpoints (8.1%)

---

## 🔗 ENLACES ÚTILES

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API Docs JSON**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

---

## 📌 NOTAS

1. Todos los endpoints excepto `/`, `/auth/register` y `/auth/login` requieren autenticación con Bearer token
2. Los endpoints paginados aceptan parámetros `?page=0&size=10`
3. El token JWT expira en 7 días por defecto
4. Las fechas se manejan en formato ISO 8601: `2025-10-02T14:30:00`
5. Los IDs son de tipo `Long` en el backend

---

**Versión Backend**: 1.0.0  
**Total Endpoints**: 74  
**Última Actualización**: 2 de Octubre, 2025

