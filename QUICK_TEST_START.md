# ⚡ INICIO RÁPIDO - Prueba TODOS los Endpoints en 3 Pasos

## 🎯 Objetivo
Probar **74 endpoints** del backend Metalix de forma automática en menos de 2 minutos.

---

## ✅ OPCIÓN 1: Prueba Automatizada (RECOMENDADO)

### Paso 1: Asegúrate de que el backend está ejecutándose
```bash
cd metalix-backend
mvn spring-boot:run
```

Espera a ver este mensaje:
```
Started MetalixBackendApplication in X.XXX seconds
```

### Paso 2: Ejecuta el script de pruebas
Abre PowerShell y ejecuta:
```powershell
cd C:\Users\quino\OneDrive\Documents\metalix-backend
.\test-all-endpoints.ps1
```

### Paso 3: Revisa los resultados
Verás un resumen al final:
```
TEST RESULTS SUMMARY
====================
Total Tests: 74
✅ Passed: XX
❌ Failed: XX
⚠️ Skipped: XX
Pass Rate: XX%
```

✅ **¡LISTO!** Todos los endpoints han sido probados.

---

## 🌐 OPCIÓN 2: Prueba Visual con Swagger

### Paso 1: Abre Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

### Paso 2: Autentica
1. Busca `/api/v1/auth/login`
2. Click en "Try it out"
3. Ingresa:
   ```json
   {
     "email": "admin@metalix.com",
     "password": "password123"
   }
   ```
4. Click "Execute"
5. Copia el `token`
6. Click en "Authorize" 🔓
7. Pega: `Bearer {tu_token}`

### Paso 3: Prueba cualquier endpoint
- Expande el módulo
- Click en "Try it out"
- Click en "Execute"

---

## 📝 OPCIÓN 3: Prueba Manual con VS Code

### Paso 1: Instala REST Client
En VS Code: Extensions → Busca "REST Client" → Instala

### Paso 2: Abre el archivo de pruebas
```
metalix-backend/API_TESTS.http
```

### Paso 3: Obtén el token
- Busca "POST Login"
- Click en "Send Request"
- Copia el token

### Paso 4: Configura el token
Línea 13, reemplaza:
```http
@token = TU_TOKEN_AQUI
```

### Paso 5: Prueba endpoints
Click en "Send Request" sobre cualquier endpoint.

---

## 📊 RESUMEN DE ENDPOINTS

| Módulo | Total | Descripción |
|--------|-------|-------------|
| 🏠 Home | 1 | Status del servidor |
| 🔐 Auth | 2 | Login y registro |
| 👥 Users | 11 | Gestión de usuarios |
| 🏛️ Municipalities | 8 | Municipalidades |
| 📍 Zones | 6 | Zonas geográficas |
| 🗑️ Waste Collectors | 9 | Contenedores |
| ♻️ Waste Collections | 8 | Recolecciones |
| 📊 Sensor Data | 6 | Datos de sensores |
| 🎁 Rewards | 8 | Recompensas |
| 💰 Reward Transactions | 5 | Canjes |
| 💳 RFID Cards | 10 | Tarjetas RFID |
| 📈 Monitoring | 10 | Reportes y alertas |
| **TOTAL** | **74** | |

---

## 🔧 Solución Rápida de Problemas

### ❌ "Cannot connect"
```bash
# Verifica que el backend esté ejecutándose
curl http://localhost:8080
```

### ❌ "401 Unauthorized"
Obtén un nuevo token:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@metalix.com","password":"password123"}'
```

### ❌ Script PowerShell bloqueado
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

---

## 📁 Archivos Creados

1. ✅ `API_TESTS.http` - Pruebas manuales con REST Client
2. ✅ `test-all-endpoints.ps1` - Script automatizado de PowerShell
3. ✅ `TESTING_GUIDE.md` - Guía completa y detallada
4. ✅ `QUICK_TEST_START.md` - Este archivo (inicio rápido)

---

## 🎯 Siguiente Paso

Una vez que todos los endpoints pasen las pruebas:

1. Verifica que el frontend se conecta correctamente
2. Revisa `CONEXION_FRONTEND_BACKEND.md` para la integración
3. Ejecuta ambos proyectos simultáneamente

---

**¿Necesitas más ayuda?** Consulta `TESTING_GUIDE.md` para información detallada.

**Backend Version**: 1.0.0  
**Endpoints Totales**: 74  
**Fecha**: 2 de Octubre, 2025

