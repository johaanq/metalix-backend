# Análisis de Endpoints del Backend - Metalix

## Resumen
Este documento analiza todos los endpoints del backend y determina cuáles se usan en el frontend y cuáles no.

---

## 1. AUTENTICACIÓN (`/api/v1/auth`)

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/auth/login` | POST | ✅ Sí | ✅ Sí | **MANTENER** |
| `/auth/register` | POST | ✅ Sí | ✅ Sí | **MANTENER** |
| `/auth/register-municipality` | POST | ✅ Sí | ✅ Sí | **MANTENER** |

**Conclusión**: Todos los endpoints de autenticación se usan. MANTENER TODOS.

---

## 2. USUARIOS (`/api/v1/users`)

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/users` (paginado) | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/users/all` (sin paginación) | GET | ❌ No | ❌ No | **ELIMINAR** |
| `/users/{id}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/users/{id}/points` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/users/{id}/profile` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/users/{id}/stats` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/users/{id}/activity` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/users/role/{role}` | GET | ❌ No | ⚠️ Útil | **MANTENER** |
| `/users/municipality/{municipalityId}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/users/{id}` | PUT | ✅ Sí | ✅ Sí | **MANTENER** |
| `/users/{id}` | DELETE | ❌ No | ⚠️ Útil | **MANTENER** |
| `/users/{id}/deactivate` | PATCH | ❌ No | ⚠️ Útil | **MANTENER** |

**Conclusión**: Eliminar `/users/all` (duplicado innecesario). Los demás son útiles.

---

## 3. MUNICIPALIDADES (`/api/v1/municipalities`)

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/municipalities` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/municipalities/{id}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/municipalities/code/{code}` | GET | ❌ No | ❌ No | **ELIMINAR** |
| `/municipalities/{id}/stats` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/municipalities/{id}/dashboard` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/municipalities` | POST | ✅ Sí | ✅ Sí | **MANTENER** |
| `/municipalities/{id}` | PUT | ✅ Sí | ✅ Sí | **MANTENER** |
| `/municipalities/{id}` | DELETE | ❌ No | ⚠️ Útil | **MANTENER** |

**Conclusión**: Eliminar `/municipalities/code/{code}` (no se usa).

---

## 4. ZONAS (`/api/v1/zones`)

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/zones` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/zones/{id}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/zones/municipality/{municipalityId}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/zones` | POST | ✅ Sí | ✅ Sí | **MANTENER** |
| `/zones/{id}` | PUT | ✅ Sí | ✅ Sí | **MANTENER** |
| `/zones/{id}` | DELETE | ✅ Sí | ✅ Sí | **MANTENER** |

**Conclusión**: Todos se usan. MANTENER TODOS.

---

## 5. COLECTORES DE RESIDUOS (`/api/v1/waste-collectors`)

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/waste-collectors` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/waste-collectors/{id}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/waste-collectors/municipality/{municipalityId}` | GET | ❌ No | ⚠️ Útil | **MANTENER** |
| `/waste-collectors/zone/{zoneId}` | GET | ❌ No | ⚠️ Útil | **MANTENER** |
| `/waste-collectors/full` | GET | ❌ No | ⚠️ Útil | **MANTENER** |
| `/waste-collectors` | POST | ✅ Sí | ✅ Sí | **MANTENER** |
| `/waste-collectors/{id}` | PUT | ✅ Sí | ✅ Sí | **MANTENER** |
| `/waste-collectors/{id}/empty` | PATCH | ❌ No | ⚠️ Útil | **MANTENER** |
| `/waste-collectors/{id}` | DELETE | ❌ No | ⚠️ Útil | **MANTENER** |

**Conclusión**: Todos son útiles para funcionalidad administrativa. MANTENER TODOS.

---

## 6. RECOLECCIONES DE RESIDUOS (`/api/v1/waste-collections`)

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/waste-collections` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/waste-collections/{id}` | GET | ❌ No | ⚠️ Útil | **MANTENER** |
| `/waste-collections/user/{userId}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/waste-collections/collector/{collectorId}` | GET | ❌ No | ⚠️ Útil | **MANTENER** |
| `/waste-collections/municipality/{municipalityId}` | GET | ❌ No | ⚠️ Útil | **MANTENER** |
| `/waste-collections` | POST | ✅ Sí | ✅ Sí | **MANTENER** |
| `/waste-collections/{id}` | PUT | ❌ No | ❌ No | **ELIMINAR** |
| `/waste-collections/{id}` | DELETE | ❌ No | ❌ No | **ELIMINAR** |

**Conclusión**: Eliminar PUT y DELETE. Las recolecciones no deberían editarse/eliminarse una vez creadas.

---

## 7. RECOMPENSAS (`/api/v1/rewards`)

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/rewards` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/rewards/{id}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/rewards/active` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/rewards/municipality/{municipalityId}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/rewards/affordable/{maxPoints}` | GET | ❌ No | ❌ No | **ELIMINAR** |
| `/rewards` | POST | ✅ Sí | ✅ Sí | **MANTENER** |
| `/rewards/{id}` | PUT | ✅ Sí | ✅ Sí | **MANTENER** |
| `/rewards/{id}` | DELETE | ✅ Sí | ✅ Sí | **MANTENER** |

**Conclusión**: Eliminar `/rewards/affordable/{maxPoints}` (filtrado se puede hacer en frontend).

---

## 8. TRANSACCIONES DE RECOMPENSAS (`/api/v1/reward-transactions`)

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/reward-transactions` | GET | ❌ No | ⚠️ Útil | **MANTENER** |
| `/reward-transactions/{id}` | GET | ❌ No | ❌ No | **ELIMINAR** |
| `/reward-transactions/user/{userId}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/reward-transactions/redeem` | POST | ✅ Sí | ✅ Sí | **MANTENER** |
| `/reward-transactions/{id}` | DELETE | ❌ No | ❌ No | **ELIMINAR** |

**Conclusión**: Eliminar GET by ID y DELETE. Las transacciones no deberían modificarse.

---

## 9. TARJETAS RFID (`/api/v1/rfid-cards`)

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/rfid-cards` | GET | ❌ No | ⚠️ Útil | **MANTENER** |
| `/rfid-cards/{id}` | GET | ❌ No | ❌ No | **ELIMINAR** |
| `/rfid-cards/number/{cardNumber}` | GET | ❌ No | ❌ No | **ELIMINAR** |
| `/rfid-cards/user/{userId}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/rfid-cards` | POST | ❌ No | ⚠️ Útil | **MANTENER** |
| `/rfid-cards/assign` | POST | ✅ Sí | ✅ Sí | **MANTENER** |
| `/rfid-cards/link` | POST | ❌ No* | ✅ Sí | **MANTENER** |
| `/rfid-cards/use/{cardNumber}` | POST | ❌ No | ⚠️ Útil | **MANTENER** |
| `/rfid-cards/{id}` | PUT | ❌ No | ❌ No | **ELIMINAR** |
| `/rfid-cards/{id}/block` | PATCH | ✅ Sí | ✅ Sí | **MANTENER** |
| `/rfid-cards/{id}` | DELETE | ❌ No | ❌ No | **ELIMINAR** |

*Nota: `/link` se agregó pero el método en el servicio fue eliminado por el usuario.

**Conclusión**: Eliminar GET by ID, GET by number, PUT y DELETE.

---

## 10. MONITOREO (`/api/v1/monitoring`)

### Reportes

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/monitoring/reports` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/monitoring/reports/{id}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/monitoring/reports/municipality/{municipalityId}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/monitoring/reports` | POST | ✅ Sí | ✅ Sí | **MANTENER** |

### Métricas

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/monitoring/metrics` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/monitoring/metrics/{id}` | GET | ❌ No | ❌ No | **ELIMINAR** |
| `/monitoring/metrics/municipality/{municipalityId}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/monitoring/metrics` | POST | ❌ No | ⚠️ Útil | **MANTENER** |

### Alertas

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| `/monitoring/alerts` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/monitoring/alerts/{id}` | GET | ❌ No | ❌ No | **ELIMINAR** |
| `/monitoring/alerts/municipality/{municipalityId}` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/monitoring/alerts/unread` | GET | ✅ Sí | ✅ Sí | **MANTENER** |
| `/monitoring/alerts` | POST | ❌ No | ⚠️ Útil | **MANTENER** |
| `/monitoring/alerts/{id}/read` | PATCH | ❌ No | ❌ No | **ELIMINAR** |
| `/monitoring/alerts/{id}` | PATCH | ✅ Sí | ✅ Sí | **MANTENER** |

**Conclusión**: Eliminar varios GET by ID y el endpoint `/read` (usar el PATCH general).

---

## 11. SENSORES (`/api/v1/sensor-data`)

| Endpoint | Método | Usado | Necesario | Acción |
|----------|--------|-------|-----------|--------|
| Todos | * | ❌ No | ❌ No | **ELIMINAR CONTROLADOR** |

**Conclusión**: El SensorDataController no se usa en absoluto. ELIMINAR COMPLETO.

---

## RESUMEN DE ACCIONES

### Endpoints a ELIMINAR (19 total):

1. **UserController**:
   - ❌ `GET /users/all` - Duplicado innecesario

2. **MunicipalityController**:
   - ❌ `GET /municipalities/code/{code}` - No se usa

3. **WasteCollectionController**:
   - ❌ `PUT /waste-collections/{id}` - No debe editarse
   - ❌ `DELETE /waste-collections/{id}` - No debe eliminarse

4. **RewardController**:
   - ❌ `GET /rewards/affordable/{maxPoints}` - Filtrado en frontend

5. **RewardTransactionController**:
   - ❌ `GET /reward-transactions/{id}` - No se usa
   - ❌ `DELETE /reward-transactions/{id}` - No debe eliminarse

6. **RfidCardController**:
   - ❌ `GET /rfid-cards/{id}` - No se usa
   - ❌ `GET /rfid-cards/number/{cardNumber}` - No se usa
   - ❌ `PUT /rfid-cards/{id}` - No se usa
   - ❌ `DELETE /rfid-cards/{id}` - No debe eliminarse

7. **MonitoringController**:
   - ❌ `GET /monitoring/metrics/{id}` - No se usa
   - ❌ `GET /monitoring/alerts/{id}` - No se usa
   - ❌ `PATCH /monitoring/alerts/{id}/read` - Duplicado

8. **SensorDataController**:
   - ❌ **ELIMINAR CONTROLADOR COMPLETO** (6+ endpoints)

### Estadísticas:

- **Total endpoints actual**: ~50
- **Endpoints a eliminar**: ~19
- **Endpoints finales**: ~31
- **Reducción**: 38%

---

## BENEFICIOS DE LA LIMPIEZA:

1. ✅ **Menor superficie de ataque** - Menos endpoints = menos vulnerabilidades
2. ✅ **Código más limpio** - Más fácil de mantener
3. ✅ **Documentación más clara** - Solo endpoints que se usan
4. ✅ **Mejor rendimiento** - Menos rutas a procesar
5. ✅ **Menos confusión** - Desarrolladores solo ven lo necesario

