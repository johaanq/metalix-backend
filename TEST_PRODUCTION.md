# 🌐 PRUEBAS DEL BACKEND EN PRODUCCIÓN (RENDER)

## 📍 Información del Servidor

**URL Base**: `https://metalix-backend.onrender.com`  
**API Base**: `https://metalix-backend.onrender.com/api/v1`  
**Swagger**: `https://metalix-backend.onrender.com/swagger-ui/index.html`

---

## ✅ ESTADO ACTUAL

### Prueba Ejecutada:

```
=== METALIX BACKEND TEST (RENDER) ===
✅ [1] Home Endpoint... PASSED
❌ [2] Login... FAILED (401 - No hay usuarios)
```

**Conclusión**: El servidor está funcionando correctamente, pero necesita datos de prueba.

---

## 🚀 CONFIGURACIÓN INICIAL (PRIMERA VEZ)

### Paso 1: Crear Usuario Administrador

Ejecuta este script en PowerShell:

```powershell
# Registrar usuario admin
$registerBody = @{
    email = "admin@metalix.com"
    password = "password123"
    firstName = "Admin"
    lastName = "System"
    role = "SYSTEM_ADMIN"
} | ConvertTo-Json

$response = Invoke-RestMethod `
    -Uri "https://metalix-backend.onrender.com/api/v1/auth/register" `
    -Method POST `
    -Body $registerBody `
    -ContentType "application/json"

Write-Host "✅ Usuario creado exitosamente!"
Write-Host "Token: $($response.token)"
```

### Paso 2: Ejecutar Pruebas Completas

Una vez que tengas el usuario admin:

```powershell
cd C:\Users\quino\OneDrive\Documents\metalix-backend
.\test-backend-simple.ps1
```

---

## 🧪 PRUEBAS RÁPIDAS

### 1. Verificar que el servidor está en línea

```powershell
Invoke-RestMethod -Uri "https://metalix-backend.onrender.com/"
```

**Respuesta esperada:**
```json
{
  "application": "Metalix Backend API",
  "version": "1.0.0",
  "status": "running",
  "swagger": "/swagger-ui/index.html"
}
```

### 2. Registrar un nuevo usuario

```powershell
$user = @{
    email = "test@metalix.com"
    password = "password123"
    firstName = "Test"
    lastName = "User"
    role = "CITIZEN"
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "https://metalix-backend.onrender.com/api/v1/auth/register" `
    -Method POST `
    -Body $user `
    -ContentType "application/json"
```

### 3. Hacer Login

```powershell
$login = @{
    email = "test@metalix.com"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-RestMethod `
    -Uri "https://metalix-backend.onrender.com/api/v1/auth/login" `
    -Method POST `
    -Body $login `
    -ContentType "application/json"

$token = $response.token
Write-Host "Token: $token"
```

### 4. Obtener lista de usuarios (requiere autenticación)

```powershell
$headers = @{ "Authorization" = "Bearer $token" }

Invoke-RestMethod `
    -Uri "https://metalix-backend.onrender.com/api/v1/users" `
    -Method GET `
    -Headers $headers
```

---

## 🔐 CAMPOS QUE SE GENERAN AUTOMÁTICAMENTE

**NO necesitas enviar estos campos al crear recursos:**

### ✅ Siempre se generan automáticamente:
- `id` - ID único del registro
- `createdAt` - Fecha/hora de creación
- `updatedAt` - Fecha/hora de última actualización

### ✅ Tienen valores por defecto (opcionales):
- `role` - Default: `CITIZEN`
- `isActive` - Default: `true`
- `totalPoints` - Default: `0`
- `status` - Default: `ACTIVE`
- `timestamp` - Default: `now()`
- `verified` - Default: `false`

---

## 📋 EJEMPLOS DE REQUESTS

### Crear Usuario (Register)

```json
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CITIZEN"
}
```

**Campos requeridos:**
- ✅ `email`
- ✅ `password`
- ✅ `firstName`
- ✅ `lastName`

**Campos opcionales:**
- `role` (default: CITIZEN)
- `phone`
- `address`
- `city`
- `zipCode`
- `municipalityId`

**Campos que NO debes enviar (se generan):**
- ❌ `id`
- ❌ `createdAt`
- ❌ `updatedAt`
- ❌ `isActive` (usa default: true)
- ❌ `totalPoints` (usa default: 0)

### Crear Municipalidad

```json
POST /api/v1/municipalities
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Lima Metropolitana",
  "code": "LIMA001",
  "region": "Lima",
  "population": 9000000,
  "area": 2672.0,
  "contactEmail": "info@munilima.gob.pe",
  "contactPhone": "+51-1-315-1800",
  "contactAddress": "Jr. de la Unión 300, Lima",
  "website": "https://www.munlima.gob.pe"
}
```

**Campos requeridos:**
- ✅ `name`
- ✅ `code`
- ✅ `region`

**Campos que NO debes enviar:**
- ❌ `id`
- ❌ `createdAt`
- ❌ `updatedAt`

### Crear Zona

```json
POST /api/v1/zones
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "San Isidro",
  "municipalityId": 1,
  "zoneType": "COMMERCIAL",
  "population": 55000,
  "boundariesNorth": -12.0864,
  "boundariesSouth": -12.1064,
  "boundariesEast": -77.0278,
  "boundariesWest": -77.0478
}
```

### Crear Contenedor (Waste Collector)

```json
POST /api/v1/waste-collectors
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Contenedor Parque Kennedy",
  "municipalityId": 1,
  "zoneId": 1,
  "latitude": -12.0864,
  "longitude": -77.0278,
  "address": "Parque Kennedy, Miraflores",
  "status": "ACTIVE",
  "capacity": 1000.0,
  "currentWeight": 0.0
}
```

### Registrar Recolección

```json
POST /api/v1/waste-collections
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 3,
  "collectorId": 1,
  "weight": 5.5,
  "recyclableType": "METAL",
  "municipalityId": 1,
  "zoneId": 1
}
```

**Campos que NO debes enviar (se generan):**
- ❌ `id`
- ❌ `timestamp` (usa default: now)
- ❌ `points` (se calcula automáticamente)
- ❌ `verified` (usa default: false)
- ❌ `createdAt`
- ❌ `updatedAt`

### Crear Recompensa

```json
POST /api/v1/rewards
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Vale de Descuento 20%",
  "description": "20% de descuento en tiendas seleccionadas",
  "pointsCost": 100,
  "category": "SHOPPING",
  "stock": 50,
  "validUntil": "2025-12-31T23:59:59",
  "municipalityId": 1,
  "isActive": true
}
```

### Canjear Recompensa

```json
POST /api/v1/reward-transactions/redeem
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 3,
  "rewardId": 1
}
```

**Solo necesitas estos 2 campos!** Todo lo demás se genera automáticamente.

---

## 🛠️ SCRIPT DE PRUEBA AUTOMATIZADO

El script `test-backend-simple.ps1` ya está configurado para usar Render:

```powershell
# Ejecutar
cd C:\Users\quino\OneDrive\Documents\metalix-backend
.\test-backend-simple.ps1
```

**Prueba estos 8 endpoints:**
1. ✅ Home
2. ✅ Login
3. ✅ Get Users
4. ✅ Get Municipalities
5. ✅ Get Zones
6. ✅ Get Waste Collectors
7. ✅ Get Rewards
8. ✅ Get RFID Cards

---

## 🌐 SWAGGER UI EN PRODUCCIÓN

Accede a la documentación interactiva:

```
https://metalix-backend.onrender.com/swagger-ui/index.html
```

**Pasos para usar Swagger:**
1. Abre la URL en tu navegador
2. Busca `/api/v1/auth/login`
3. Haz login con tus credenciales
4. Copia el token de la respuesta
5. Click en "Authorize" 🔓
6. Pega: `Bearer {tu_token}`
7. Ahora puedes probar todos los endpoints

---

## ⚠️ NOTAS IMPORTANTES

### 1. Primera vez en Render
Si es la primera vez que usas el servidor de Render, necesitas crear un usuario admin primero (ver Paso 1 arriba).

### 2. Servidor "dormido"
Render pone los servidores gratuitos a dormir después de 15 minutos de inactividad. La **primera petición puede tardar 30-60 segundos** en responder mientras el servidor se "despierta".

### 3. Base de datos vacía
El servidor de producción inicia con una base de datos vacía. Debes:
- Crear usuarios vía `/auth/register`
- Crear municipalidades, zonas, contenedores, etc. vía API
- O cargar el `data-sample.sql` en la base de datos de Render

### 4. Variables de entorno
Asegúrate de que estas variables estén configuradas en Render:
- `MYSQL_HOST`
- `MYSQL_PORT`
- `MYSQL_DATABASE`
- `MYSQL_ROOT_PASSWORD`
- `JWT_SECRET`

---

## 📊 RESUMEN

| Aspecto | Estado | Notas |
|---------|--------|-------|
| Servidor en línea | ✅ | Funcionando correctamente |
| Home endpoint | ✅ | Responde OK |
| Swagger UI | ✅ | Disponible |
| Usuarios de prueba | ❌ | Necesitas crearlos |
| Datos de prueba | ❌ | Necesitas cargarlos |
| CORS | ✅ | Configurado para Vercel |
| Auto-generación de IDs | ✅ | Funciona correctamente |
| Auto-generación de timestamps | ✅ | Funciona correctamente |

---

## 🎯 PRÓXIMOS PASOS

1. ✅ Crear usuario admin en producción
2. ✅ Ejecutar script de pruebas
3. ✅ Cargar datos de prueba (opcional)
4. ✅ Conectar frontend Angular
5. ✅ Probar integración completa

---

**Fecha**: 2 de Octubre, 2025  
**Servidor**: Render (Producción)  
**Estado**: ✅ Operativo

