# 🧪 GUÍA COMPLETA DE PRUEBAS - METALIX BACKEND

Esta guía te ayudará a probar **TODOS** los endpoints del backend de Metalix de forma manual y automatizada.

---

## 📋 TABLA DE CONTENIDOS

1. [Requisitos Previos](#requisitos-previos)
2. [Preparación del Entorno](#preparación-del-entorno)
3. [Método 1: Pruebas Automatizadas con PowerShell](#método-1-pruebas-automatizadas-con-powershell)
4. [Método 2: Pruebas Manuales con REST Client](#método-2-pruebas-manuales-con-rest-client)
5. [Método 3: Usar Swagger UI](#método-3-usar-swagger-ui)
6. [Resumen de Endpoints](#resumen-de-endpoints)
7. [Solución de Problemas](#solución-de-problemas)

---

## 📌 REQUISITOS PREVIOS

### 1. Backend ejecutándose
```bash
cd metalix-backend
mvn spring-boot:run
```

### 2. Base de datos MySQL configurada
```bash
# Asegúrate de que MySQL está ejecutándose
# y que tienes las variables de entorno configuradas:
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=metalix_db
MYSQL_ROOT_PASSWORD=tu_password
JWT_SECRET=tu_secret_key
```

### 3. Datos de prueba cargados
El backend debe tener el archivo `data-sample.sql` ejecutado para tener usuarios y datos de prueba.

**Usuario de prueba por defecto:**
- Email: `admin@metalix.com`
- Password: `password123`
- Role: `SYSTEM_ADMIN`

---

## 🚀 PREPARACIÓN DEL ENTORNO

### 1. Verificar que el servidor está ejecutándose

Abre tu navegador y visita:
```
http://localhost:8080
```

Deberías ver una respuesta JSON como:
```json
{
  "application": "Metalix Backend API",
  "version": "1.0.0",
  "status": "running",
  "swagger": "/swagger-ui/index.html",
  "apiDocs": "/v3/api-docs"
}
```

### 2. Verificar Swagger UI (Opcional pero recomendado)

Visita:
```
http://localhost:8080/swagger-ui/index.html
```

Deberías ver la documentación interactiva de la API.

---

## 🤖 MÉTODO 1: PRUEBAS AUTOMATIZADAS CON POWERSHELL

Este es el método **MÁS RÁPIDO** para probar todos los endpoints a la vez.

### Paso 1: Abrir PowerShell

Presiona `Win + X` y selecciona "Windows PowerShell" o "Terminal"

### Paso 2: Navegar al directorio del proyecto

```powershell
cd C:\Users\quino\OneDrive\Documents\metalix-backend
```

### Paso 3: Ejecutar el script de pruebas

```powershell
.\test-all-endpoints.ps1
```

### Opciones avanzadas:

```powershell
# Especificar URL diferente
.\test-all-endpoints.ps1 -BaseUrl "http://localhost:8080"

# Usar credenciales diferentes
.\test-all-endpoints.ps1 -Email "otro@email.com" -Password "password123"

# Ambos parámetros
.\test-all-endpoints.ps1 -BaseUrl "http://localhost:8080" -Email "admin@metalix.com" -Password "password123"
```

### Paso 4: Revisar resultados

El script mostrará:
- ✅ Tests que pasaron (código 200)
- ❌ Tests que fallaron (errores)
- ⚠️ Tests omitidos (requieren permisos especiales)

Al final verás un resumen:
```
TEST RESULTS SUMMARY
=====================
Total Tests: 65
✅ Passed: 58
❌ Failed: 2
⚠️ Skipped: 5
Pass Rate: 89.23%
```

También se genera un archivo JSON con los resultados detallados:
```
test-results-20251002-143025.json
```

---

## 📝 MÉTODO 2: PRUEBAS MANUALES CON REST CLIENT

Este método te permite probar endpoints individuales en VS Code.

### Paso 1: Instalar REST Client en VS Code

1. Abre VS Code
2. Ve a Extensions (Ctrl+Shift+X)
3. Busca "REST Client" de Huachao Mao
4. Instala la extensión

### Paso 2: Abrir el archivo de pruebas

En VS Code, abre:
```
metalix-backend/API_TESTS.http
```

### Paso 3: Obtener token de autenticación

1. Busca la sección "AUTHENTICATION"
2. Encuentra la petición "POST Login"
3. Haz clic en "Send Request" sobre la petición
4. Copia el token de la respuesta

### Paso 4: Configurar el token

En la línea 13 del archivo, reemplaza:
```http
@token = YOUR_TOKEN_HERE
```

Por tu token real:
```http
@token = eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Paso 5: Ejecutar pruebas individuales

Para cada endpoint que quieras probar:
1. Haz clic en "Send Request" sobre la petición
2. La respuesta aparecerá en una nueva pestaña
3. Verifica el código de estado y los datos

### Endpoints organizados por módulo:

- 🏠 **Home** (línea 27): Verificación básica
- 🔐 **Authentication** (línea 35): Login y registro
- 👥 **Users** (línea 67): Gestión de usuarios
- 🏛️ **Municipalities** (línea 107): Municipalidades
- 📍 **Zones** (línea 147): Zonas geográficas
- 🗑️ **Waste Collectors** (línea 177): Contenedores
- ♻️ **Waste Collections** (línea 226): Recolecciones
- 📊 **Sensor Data** (línea 265): Datos de sensores
- 🎁 **Rewards** (línea 294): Recompensas
- 💰 **Reward Transactions** (línea 335): Transacciones
- 💳 **RFID Cards** (línea 364): Tarjetas RFID
- 📈 **Monitoring** (línea 417): Reportes y métricas

---

## 🌐 MÉTODO 3: USAR SWAGGER UI

El método más visual e interactivo.

### Paso 1: Abrir Swagger UI

Visita en tu navegador:
```
http://localhost:8080/swagger-ui/index.html
```

### Paso 2: Autenticarte

1. Busca el endpoint `/api/v1/auth/login`
2. Haz clic en "Try it out"
3. Ingresa:
   ```json
   {
     "email": "admin@metalix.com",
     "password": "password123"
   }
   ```
4. Haz clic en "Execute"
5. Copia el `token` de la respuesta
6. Haz clic en el botón "Authorize" 🔓 (arriba a la derecha)
7. Pega el token en el campo: `Bearer {token}`
8. Haz clic en "Authorize" y luego "Close"

### Paso 3: Probar endpoints

Ahora puedes probar cualquier endpoint:
1. Expande el módulo que quieras probar
2. Haz clic en el endpoint
3. Haz clic en "Try it out"
4. Modifica los parámetros si es necesario
5. Haz clic en "Execute"
6. Revisa la respuesta

### Ventajas de Swagger UI:
- ✅ Interfaz visual amigable
- ✅ Documentación integrada
- ✅ Validación de parámetros
- ✅ Ejemplos de respuestas

---

## 📊 RESUMEN DE ENDPOINTS

### Total: **74 Endpoints**

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

---

## 🔍 ENDPOINTS DETALLADOS POR MÓDULO

### 🏠 HOME (1 endpoint)
- `GET /` - Verificar estado del servidor

### 🔐 AUTHENTICATION (2 endpoints)
- `POST /api/v1/auth/register` - Registrar nuevo usuario
- `POST /api/v1/auth/login` - Iniciar sesión

### 👥 USERS (11 endpoints)
- `GET /api/v1/users` - Listar todos los usuarios
- `GET /api/v1/users/{id}` - Obtener usuario por ID
- `GET /api/v1/users/{id}/points` - Obtener puntos del usuario
- `GET /api/v1/users/role/{role}` - Obtener usuarios por rol
- `GET /api/v1/users/municipality/{municipalityId}` - Usuarios por municipalidad
- `GET /api/v1/users/{id}/profile` - Perfil del usuario
- `GET /api/v1/users/{id}/stats` - Estadísticas del usuario
- `GET /api/v1/users/{id}/activity` - Actividad del usuario
- `PUT /api/v1/users/{id}` - Actualizar usuario
- `PATCH /api/v1/users/{id}/deactivate` - Desactivar usuario
- `DELETE /api/v1/users/{id}` - Eliminar usuario

### 🏛️ MUNICIPALITIES (8 endpoints)
- `GET /api/v1/municipalities` - Listar municipalidades
- `GET /api/v1/municipalities/{id}` - Obtener municipalidad por ID
- `GET /api/v1/municipalities/code/{code}` - Obtener por código
- `GET /api/v1/municipalities/{id}/stats` - Estadísticas
- `GET /api/v1/municipalities/{id}/dashboard` - Dashboard
- `POST /api/v1/municipalities` - Crear municipalidad
- `PUT /api/v1/municipalities/{id}` - Actualizar municipalidad
- `DELETE /api/v1/municipalities/{id}` - Eliminar municipalidad

### 📍 ZONES (6 endpoints)
- `GET /api/v1/zones` - Listar zonas
- `GET /api/v1/zones/{id}` - Obtener zona por ID
- `GET /api/v1/zones/municipality/{municipalityId}` - Zonas por municipalidad
- `POST /api/v1/zones` - Crear zona
- `PUT /api/v1/zones/{id}` - Actualizar zona
- `DELETE /api/v1/zones/{id}` - Eliminar zona

### 🗑️ WASTE COLLECTORS (9 endpoints)
- `GET /api/v1/waste-collectors` - Listar contenedores
- `GET /api/v1/waste-collectors/{id}` - Obtener contenedor por ID
- `GET /api/v1/waste-collectors/municipality/{municipalityId}` - Por municipalidad
- `GET /api/v1/waste-collectors/zone/{zoneId}` - Por zona
- `GET /api/v1/waste-collectors/full` - Contenedores llenos
- `POST /api/v1/waste-collectors` - Crear contenedor
- `PUT /api/v1/waste-collectors/{id}` - Actualizar contenedor
- `PATCH /api/v1/waste-collectors/{id}/empty` - Vaciar contenedor
- `DELETE /api/v1/waste-collectors/{id}` - Eliminar contenedor

### ♻️ WASTE COLLECTIONS (8 endpoints)
- `GET /api/v1/waste-collections` - Listar recolecciones
- `GET /api/v1/waste-collections/{id}` - Obtener por ID
- `GET /api/v1/waste-collections/user/{userId}` - Por usuario
- `GET /api/v1/waste-collections/collector/{collectorId}` - Por contenedor
- `GET /api/v1/waste-collections/municipality/{municipalityId}` - Por municipalidad
- `POST /api/v1/waste-collections` - Crear recolección
- `PUT /api/v1/waste-collections/{id}` - Actualizar recolección
- `DELETE /api/v1/waste-collections/{id}` - Eliminar recolección

### 📊 SENSOR DATA (6 endpoints)
- `GET /api/v1/sensor-data` - Listar datos de sensores
- `GET /api/v1/sensor-data/{id}` - Obtener por ID
- `GET /api/v1/sensor-data/collector/{collectorId}` - Por contenedor
- `GET /api/v1/sensor-data/collector/{collectorId}/latest` - Último dato
- `POST /api/v1/sensor-data` - Crear dato de sensor
- `DELETE /api/v1/sensor-data/{id}` - Eliminar dato

### 🎁 REWARDS (8 endpoints)
- `GET /api/v1/rewards` - Listar recompensas
- `GET /api/v1/rewards/{id}` - Obtener por ID
- `GET /api/v1/rewards/active` - Recompensas activas
- `GET /api/v1/rewards/municipality/{municipalityId}` - Por municipalidad
- `GET /api/v1/rewards/affordable/{maxPoints}` - Recompensas asequibles
- `POST /api/v1/rewards` - Crear recompensa
- `PUT /api/v1/rewards/{id}` - Actualizar recompensa
- `DELETE /api/v1/rewards/{id}` - Eliminar recompensa

### 💰 REWARD TRANSACTIONS (5 endpoints)
- `GET /api/v1/reward-transactions` - Listar transacciones
- `GET /api/v1/reward-transactions/{id}` - Obtener por ID
- `GET /api/v1/reward-transactions/user/{userId}` - Por usuario
- `POST /api/v1/reward-transactions/redeem` - Canjear recompensa
- `DELETE /api/v1/reward-transactions/{id}` - Eliminar transacción

### 💳 RFID CARDS (10 endpoints)
- `GET /api/v1/rfid-cards` - Listar tarjetas
- `GET /api/v1/rfid-cards/{id}` - Obtener por ID
- `GET /api/v1/rfid-cards/number/{cardNumber}` - Por número de tarjeta
- `GET /api/v1/rfid-cards/user/{userId}` - Por usuario
- `POST /api/v1/rfid-cards` - Crear tarjeta
- `POST /api/v1/rfid-cards/assign` - Asignar tarjeta a usuario
- `POST /api/v1/rfid-cards/use/{cardNumber}` - Usar tarjeta
- `PUT /api/v1/rfid-cards/{id}` - Actualizar tarjeta
- `PATCH /api/v1/rfid-cards/{id}/block` - Bloquear tarjeta
- `DELETE /api/v1/rfid-cards/{id}` - Eliminar tarjeta

### 📈 MONITORING (10 endpoints)
- `GET /api/v1/monitoring/reports` - Listar reportes
- `GET /api/v1/monitoring/reports/{id}` - Obtener reporte por ID
- `GET /api/v1/monitoring/reports/municipality/{municipalityId}` - Por municipalidad
- `POST /api/v1/monitoring/reports` - Crear reporte
- `GET /api/v1/monitoring/metrics` - Listar métricas
- `GET /api/v1/monitoring/metrics/{id}` - Obtener métrica por ID
- `GET /api/v1/monitoring/metrics/municipality/{municipalityId}` - Por municipalidad
- `POST /api/v1/monitoring/metrics` - Crear métrica
- `GET /api/v1/monitoring/alerts` - Listar alertas
- `GET /api/v1/monitoring/alerts/{id}` - Obtener alerta por ID
- `GET /api/v1/monitoring/alerts/municipality/{municipalityId}` - Por municipalidad
- `GET /api/v1/monitoring/alerts/unread` - Alertas no leídas
- `POST /api/v1/monitoring/alerts` - Crear alerta

---

## ❗ SOLUCIÓN DE PROBLEMAS

### Problema 1: "Cannot connect to server"

**Causa:** El backend no está ejecutándose

**Solución:**
```bash
cd metalix-backend
mvn spring-boot:run
```

### Problema 2: "401 Unauthorized"

**Causa:** Token inválido o expirado

**Solución:**
1. Ejecuta el login de nuevo
2. Copia el nuevo token
3. Actualiza el token en tus peticiones

### Problema 3: "403 Forbidden"

**Causa:** No tienes permisos para ese endpoint

**Solución:**
- Usa una cuenta con el rol adecuado:
  - `SYSTEM_ADMIN` - acceso completo
  - `MUNICIPALITY_ADMIN` - gestión municipal
  - `CITIZEN` - operaciones básicas

### Problema 4: "404 Not Found"

**Causa:** Endpoint incorrecto o recurso no existe

**Solución:**
1. Verifica la URL del endpoint
2. Asegúrate de que el ID del recurso existe
3. Revisa la documentación en Swagger UI

### Problema 5: "500 Internal Server Error"

**Causa:** Error en el servidor

**Solución:**
1. Revisa los logs del backend
2. Verifica que la base de datos esté ejecutándose
3. Asegúrate de que los datos de prueba estén cargados

### Problema 6: Script de PowerShell no se ejecuta

**Causa:** Política de ejecución de scripts

**Solución:**
```powershell
# Abrir PowerShell como Administrador
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Luego ejecuta el script
.\test-all-endpoints.ps1
```

---

## 📌 NOTAS IMPORTANTES

### Roles y Permisos

- **SYSTEM_ADMIN**: Puede hacer TODO
- **MUNICIPALITY_ADMIN**: Gestiona su municipalidad, zonas y contenedores
- **CITIZEN**: Operaciones básicas (ver recompensas, canjear, etc.)

### Endpoints Públicos (sin autenticación)

Solo estos endpoints NO requieren token:
- `GET /`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`

### Datos de Prueba

Los usuarios de prueba en `data-sample.sql`:

| Email | Password | Role | Puntos |
|-------|----------|------|--------|
| admin@metalix.com | password123 | SYSTEM_ADMIN | 0 |
| admin.lima@metalix.com | password123 | MUNICIPALITY_ADMIN | 0 |
| maria.lopez@email.com | password123 | CITIZEN | 150 |
| jose.perez@email.com | password123 | CITIZEN | 320 |
| ana.torres@email.com | password123 | CITIZEN | 200 |

---

## ✅ CHECKLIST DE VERIFICACIÓN

Usa este checklist para asegurarte de que todo funciona:

- [ ] Backend ejecutándose en http://localhost:8080
- [ ] MySQL ejecutándose y base de datos creada
- [ ] Variables de entorno configuradas
- [ ] Datos de prueba cargados
- [ ] Endpoint raíz (`/`) responde correctamente
- [ ] Swagger UI accesible
- [ ] Login funciona y devuelve token
- [ ] Script de PowerShell ejecuta sin errores
- [ ] Al menos 80% de pruebas pasan
- [ ] Endpoints críticos funcionan (auth, users, municipalities)

---

## 🎯 PRÓXIMOS PASOS

Después de verificar que todos los endpoints funcionan:

1. ✅ Integra el frontend Angular
2. ✅ Configura el ambiente de producción
3. ✅ Implementa pruebas unitarias
4. ✅ Configura CI/CD
5. ✅ Documenta casos de uso específicos

---

**Fecha de creación**: 2 de Octubre, 2025  
**Versión del Backend**: 1.0.0  
**Total de Endpoints**: 74

