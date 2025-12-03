# Prueba de Endpoints - Metalix Backend

Este documento contiene pruebas para todos los endpoints del backend de Metalix.

## Configuración

```bash
# URL del backend
BASE_URL="http://localhost:8081/api/v1"

# Variables globales (se llenarán después del login)
TOKEN=""
USER_ID=""
MUNICIPALITY_ID=""
```

---

## 1. AUTENTICACIÓN

### 1.1. Registrar Usuario Ciudadano
```bash
curl -X POST "${BASE_URL}/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ciudadano@test.com",
    "password": "Test123!",
    "firstName": "Juan",
    "lastName": "Pérez",
    "phone": "987654321",
    "address": "Av. Test 123",
    "city": "Lima",
    "zipCode": "15001"
  }'
```

**Respuesta esperada**: `200 OK` con token y userId

---

### 1.2. Login
```bash
curl -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ciudadano@test.com",
    "password": "Test123!"
  }'
```

**Respuesta esperada**: `200 OK` con token y userId

💡 **Guarda el token**: Copia el token de la respuesta para usarlo en las siguientes peticiones.

---

## 2. USUARIOS

### 2.1. Obtener Todos los Usuarios ✅ SIMPLIFICADO
```bash
curl -X GET "${BASE_URL}/users" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista paginada de usuarios (página 0, tamaño 100 por defecto)

**Opcional**: Agregar parámetros de paginación
```bash
curl -X GET "${BASE_URL}/users?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

### 2.2. Obtener Usuario por ID
```bash
curl -X GET "${BASE_URL}/users/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con datos del usuario

---

### 2.3. Obtener Puntos de Usuario
```bash
curl -X GET "${BASE_URL}/users/1/points" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con número de puntos

---

### 2.4. Obtener Perfil de Usuario
```bash
curl -X GET "${BASE_URL}/users/1/profile" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con perfil completo del usuario

---

### 2.5. Obtener Estadísticas de Usuario
```bash
curl -X GET "${BASE_URL}/users/1/stats" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con estadísticas del usuario

---

### 2.6. Obtener Actividad de Usuario
```bash
curl -X GET "${BASE_URL}/users/1/activity?limit=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de actividades

---

### 2.7. Actualizar Usuario
```bash
curl -X PUT "${BASE_URL}/users/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan Carlos",
    "phone": "999888777"
  }'
```

**Respuesta esperada**: `200 OK` con usuario actualizado

---

### 2.8. Obtener Usuarios por Municipalidad
```bash
curl -X GET "${BASE_URL}/users/municipality/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de usuarios

---

## 3. MUNICIPALIDADES

### 3.1. Obtener Todas las Municipalidades
```bash
curl -X GET "${BASE_URL}/municipalities" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de municipalidades

---

### 3.2. Obtener Municipalidad por ID
```bash
curl -X GET "${BASE_URL}/municipalities/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con datos de la municipalidad

---

### 3.3. Obtener Estadísticas de Municipalidad
```bash
curl -X GET "${BASE_URL}/municipalities/1/stats" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con estadísticas

---

### 3.4. Obtener Dashboard de Municipalidad
```bash
curl -X GET "${BASE_URL}/municipalities/1/dashboard" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con datos del dashboard

---

### 3.5. Crear Municipalidad (Solo SYSTEM_ADMIN)
```bash
curl -X POST "${BASE_URL}/municipalities" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Municipalidad de Test",
    "code": "MUN-TEST-001",
    "region": "Lima",
    "population": 50000,
    "area": 100.5,
    "contactEmail": "contacto@muntest.gob.pe",
    "contactPhone": "014567890"
  }'
```

**Respuesta esperada**: `201 CREATED` con municipalidad creada

---

## 4. ZONAS

### 4.1. Obtener Todas las Zonas
```bash
curl -X GET "${BASE_URL}/zones" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de zonas

---

### 4.2. Obtener Zona por ID
```bash
curl -X GET "${BASE_URL}/zones/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con datos de la zona

---

### 4.3. Obtener Zonas por Municipalidad
```bash
curl -X GET "${BASE_URL}/zones/municipality/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de zonas

---

### 4.4. Crear Zona
```bash
curl -X POST "${BASE_URL}/zones" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Zona Centro",
    "municipalityId": 1,
    "boundaries": {
      "north": -12.0,
      "south": -12.1,
      "east": -77.0,
      "west": -77.1
    },
    "population": 10000,
    "zoneType": "COMMERCIAL"
  }'
```

**Respuesta esperada**: `201 CREATED` con zona creada

---

## 5. COLECTORES DE RESIDUOS

### 5.1. Obtener Todos los Colectores
```bash
curl -X GET "${BASE_URL}/waste-collectors" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de colectores

---

### 5.2. Obtener Colector por ID
```bash
curl -X GET "${BASE_URL}/waste-collectors/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con datos del colector

---

### 5.3. Obtener Colectores por Municipalidad
```bash
curl -X GET "${BASE_URL}/waste-collectors/municipality/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de colectores

---

### 5.4. Obtener Colectores por Zona
```bash
curl -X GET "${BASE_URL}/waste-collectors/zone/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de colectores

---

### 5.5. Obtener Colectores Llenos
```bash
curl -X GET "${BASE_URL}/waste-collectors/full" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de colectores con >80% capacidad

---

### 5.6. Crear Colector
```bash
curl -X POST "${BASE_URL}/waste-collectors" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Colector Centro 1",
    "municipalityId": 1,
    "zoneId": 1,
    "latitude": -12.046373,
    "longitude": -77.042755,
    "address": "Av. Principal 123",
    "capacity": 1000,
    "status": "ACTIVE"
  }'
```

**Respuesta esperada**: `201 CREATED` con colector creado

---

### 5.7. Actualizar Estado de Colector
```bash
curl -X PUT "${BASE_URL}/waste-collectors/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "MAINTENANCE"
  }'
```

**Respuesta esperada**: `200 OK` con colector actualizado

---

### 5.8. Vaciar Colector
```bash
curl -X PATCH "${BASE_URL}/waste-collectors/1/empty" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con colector vaciado (currentWeight = 0)

---

## 6. RECOLECCIONES DE RESIDUOS

### 6.1. Obtener Todas las Recolecciones
```bash
curl -X GET "${BASE_URL}/waste-collections" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de recolecciones

---

### 6.2. Obtener Recolección por ID
```bash
curl -X GET "${BASE_URL}/waste-collections/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con datos de la recolección

---

### 6.3. Obtener Recolecciones por Usuario ✅ SIMPLIFICADO
```bash
curl -X GET "${BASE_URL}/waste-collections/user/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista paginada (página 0, tamaño 100 por defecto)

---

### 6.4. Obtener Recolecciones por Colector
```bash
curl -X GET "${BASE_URL}/waste-collections/collector/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de recolecciones

---

### 6.5. Crear Recolección
```bash
curl -X POST "${BASE_URL}/waste-collections" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "collectorId": 1,
    "municipalityId": 1,
    "weight": 5.5,
    "recyclableType": "METAL",
    "verificationMethod": "RFID"
  }'
```

**Respuesta esperada**: `201 CREATED` con recolección creada y puntos calculados

---

## 7. RECOMPENSAS

### 7.1. Obtener Todas las Recompensas
```bash
curl -X GET "${BASE_URL}/rewards" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de recompensas

---

### 7.2. Obtener Recompensa por ID
```bash
curl -X GET "${BASE_URL}/rewards/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con datos de la recompensa

---

### 7.3. Obtener Recompensas Activas
```bash
curl -X GET "${BASE_URL}/rewards/active" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de recompensas activas

---

### 7.4. Obtener Recompensas por Municipalidad
```bash
curl -X GET "${BASE_URL}/rewards/municipality/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de recompensas

---

### 7.5. Crear Recompensa
```bash
curl -X POST "${BASE_URL}/rewards" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Descuento 20% en Café",
    "description": "Descuento del 20% en cafeterías participantes",
    "pointsCost": 50,
    "category": "DISCOUNT",
    "availability": 100,
    "expirationDate": "2025-12-31",
    "municipalityId": 1
  }'
```

**Respuesta esperada**: `201 CREATED` con recompensa creada

---

### 7.6. Actualizar Recompensa
```bash
curl -X PUT "${BASE_URL}/rewards/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "availability": 50,
    "isActive": true
  }'
```

**Respuesta esperada**: `200 OK` con recompensa actualizada

---

### 7.7. Eliminar Recompensa
```bash
curl -X DELETE "${BASE_URL}/rewards/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `204 NO CONTENT`

---

## 8. TRANSACCIONES DE RECOMPENSAS

### 8.1. Obtener Todas las Transacciones (Admin)
```bash
curl -X GET "${BASE_URL}/reward-transactions" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de transacciones

---

### 8.2. Obtener Transacción por ID
```bash
curl -X GET "${BASE_URL}/reward-transactions/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con datos de la transacción

---

### 8.3. Obtener Transacciones por Usuario ✅ SIMPLIFICADO
```bash
curl -X GET "${BASE_URL}/reward-transactions/user/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista paginada (página 0, tamaño 100 por defecto)

---

### 8.4. Canjear Recompensa
```bash
curl -X POST "${BASE_URL}/reward-transactions/redeem" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "rewardId": 1
  }'
```

**Respuesta esperada**: `200 OK` con transacción creada

---

## 9. TARJETAS RFID

### 9.1. Obtener Todas las Tarjetas (Admin)
```bash
curl -X GET "${BASE_URL}/rfid-cards" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de tarjetas

---

### 9.2. Obtener Tarjeta por ID
```bash
curl -X GET "${BASE_URL}/rfid-cards/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con datos de la tarjeta

---

### 9.3. Obtener Tarjeta por Usuario
```bash
curl -X GET "${BASE_URL}/rfid-cards/user/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con tarjeta del usuario

---

### 9.4. Asignar Tarjeta a Usuario
```bash
curl -X POST "${BASE_URL}/rfid-cards/assign" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "RFID123456",
    "userId": 1
  }'
```

**Respuesta esperada**: `200 OK` con tarjeta asignada

---

### 9.5. Usar Tarjeta RFID
```bash
curl -X POST "${BASE_URL}/rfid-cards/use/RFID123456" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con tarjeta actualizada (lastUsed, usageCount)

---

### 9.6. Bloquear Tarjeta
```bash
curl -X PATCH "${BASE_URL}/rfid-cards/1/block" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con tarjeta bloqueada (isActive = false)

---

## 10. MONITOREO

### 10.1. Obtener Todos los Reportes
```bash
curl -X GET "${BASE_URL}/monitoring/reports" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de reportes

---

### 10.2. Obtener Reporte por ID
```bash
curl -X GET "${BASE_URL}/monitoring/reports/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con datos del reporte

---

### 10.3. Obtener Reportes por Municipalidad
```bash
curl -X GET "${BASE_URL}/monitoring/reports/municipality/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de reportes

---

### 10.4. Crear Reporte
```bash
curl -X POST "${BASE_URL}/monitoring/reports" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "reportType": "COLLECTION_EFFICIENCY",
    "municipalityId": 1,
    "generatedBy": 1,
    "data": {},
    "status": "PENDING"
  }'
```

**Respuesta esperada**: `201 CREATED` con reporte creado

---

### 10.5. Obtener Todas las Métricas
```bash
curl -X GET "${BASE_URL}/monitoring/metrics" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de métricas

---

### 10.6. Obtener Métricas por Municipalidad
```bash
curl -X GET "${BASE_URL}/monitoring/metrics/municipality/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de métricas

---

### 10.7. Obtener Todas las Alertas
```bash
curl -X GET "${BASE_URL}/monitoring/alerts" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de alertas

---

### 10.8. Obtener Alertas por Municipalidad
```bash
curl -X GET "${BASE_URL}/monitoring/alerts/municipality/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de alertas

---

### 10.9. Obtener Alertas No Resueltas
```bash
curl -X GET "${BASE_URL}/monitoring/alerts/unread" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con lista de alertas no resueltas

---

### 10.10. Marcar Alerta como Leída
```bash
curl -X PATCH "${BASE_URL}/monitoring/alerts/1/read" \
  -H "Authorization: Bearer ${TOKEN}"
```

**Respuesta esperada**: `200 OK` con alerta actualizada

---

### 10.11. Resolver Alerta
```bash
curl -X PATCH "${BASE_URL}/monitoring/alerts/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "isResolved": true
  }'
```

**Respuesta esperada**: `200 OK` con alerta resuelta

---

## Resumen de Cambios Aplicados

### ✅ Endpoints Simplificados (Paginación Opcional)

Los siguientes endpoints ahora tienen parámetros de paginación **opcionales** con valores por defecto:

1. **GET `/api/v1/users`**
   - `page` (opcional, defecto: 0)
   - `size` (opcional, defecto: 100)
   - `sort` (opcional, defecto: "id")
   - `direction` (opcional, defecto: "asc")

2. **GET `/api/v1/users/role/{role}`**
   - `page` (opcional, defecto: 0)
   - `size` (opcional, defecto: 100)

3. **GET `/api/v1/waste-collections/user/{userId}`**
   - `page` (opcional, defecto: 0)
   - `size` (opcional, defecto: 100)

4. **GET `/api/v1/reward-transactions/user/{userId}`**
   - `page` (opcional, defecto: 0)
   - `size` (opcional, defecto: 100)

Ahora puedes llamar estos endpoints **sin parámetros** y funcionarán correctamente con valores por defecto razonables.

---

## Notas Importantes

1. **Token JWT**: Reemplaza `${TOKEN}` con tu token de autenticación obtenido del login
2. **IDs**: Ajusta los IDs (1, 2, etc.) según los datos de tu base de datos
3. **Roles**: Algunos endpoints requieren roles específicos (SYSTEM_ADMIN, MUNICIPALITY_ADMIN)
4. **Paginación**: Los endpoints con paginación ahora tienen valores por defecto de 100 items por página
5. **Fechas**: Usa formato ISO 8601 (YYYY-MM-DD) para fechas

---

**Última actualización**: Diciembre 3, 2025

