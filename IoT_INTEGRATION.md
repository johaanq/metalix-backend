# Integración IoT - Metalix Backend

## 📡 Endpoint para Dispositivos IoT

### POST `/api/v1/iot/collections/register`

**Descripción:** Registra una recolección de residuos desde un dispositivo IoT usando tarjeta RFID.

**Tipo:** Endpoint PÚBLICO (no requiere autenticación)

**URL:** `http://localhost:8081/api/v1/iot/collections/register`

---

## 📋 Request Body

```json
{
  "rfidCardNumber": "RFID10000000",
  "weight": 5.5,
  "collectorId": 1,
  "recyclableType": "PLASTIC",
  "coordinates": "optional-gps-coordinates"
}
```

### Parámetros:

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `rfidCardNumber` | string | ✅ Sí | Número de la tarjeta RFID escaneada |
| `weight` | number | ✅ Sí | Peso en kilogramos (debe ser positivo) |
| `collectorId` | number | ✅ Sí | ID del contenedor/colector |
| `recyclableType` | string | ✅ Sí | Tipo de material reciclable |
| `coordinates` | string | ❌ No | Coordenadas GPS del dispositivo (opcional) |

### Tipos de Materiales Reciclables:

- `PLASTIC` - Plástico (1.2x puntos)
- `GLASS` - Vidrio (1.1x puntos)
- `METAL` - Metal (1.5x puntos)
- `PAPER` - Papel (1.0x puntos)
- `ORGANIC` - Orgánico (0.8x puntos)
- `ELECTRONIC` - Electrónico (2.0x puntos)
- `HAZARDOUS` - Peligroso (2.5x puntos)

---

## ✅ Response (Éxito)

**Status:** `201 Created`

```json
{
  "collectionId": 123,
  "userId": 7,
  "userEmail": "maria.lopez0@email.com",
  "userName": "María López",
  "weight": 5.5,
  "pointsEarned": 66,
  "totalUserPoints": 216,
  "recyclableType": "PLASTIC",
  "timestamp": "2025-12-03T10:30:00",
  "success": true,
  "message": "Collection registered successfully! 66 points awarded."
}
```

---

## ❌ Response (Error)

**Status:** `400 Bad Request`

```json
{
  "success": false,
  "message": "Error: RFID Card not found: RFID99999999"
}
```

### Posibles Errores:

| Error | Causa |
|-------|-------|
| `RFID Card not found` | La tarjeta RFID no existe en el sistema |
| `RFID Card is not valid or expired` | La tarjeta está bloqueada o expirada |
| `RFID Card is not linked to any user` | La tarjeta no está asignada a ningún usuario |
| `User account is not active` | La cuenta del usuario está desactivada |
| `Waste Collector not found` | El contenedor no existe |

---

## 🔧 Cálculo de Puntos

**Fórmula:**
```
puntos = peso × 10 × multiplicador_tipo
```

**Ejemplo:**
- Peso: 5.5 kg
- Tipo: PLASTIC (multiplicador 1.2)
- Cálculo: 5.5 × 10 × 1.2 = **66 puntos**

---

## 🔗 Vincular Tarjeta RFID a Usuario

### POST `/api/v1/rfid-cards/assign`

**Requiere autenticación:** ✅ Sí (SYSTEM_ADMIN o MUNICIPALITY_ADMIN)

```json
{
  "cardNumber": "RFID10000000",
  "userId": 7
}
```

---

## 🧪 Ejemplo de Uso desde Arduino/ESP32

```cpp
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

const char* serverUrl = "http://your-server:8081/api/v1/iot/collections/register";

void registerCollection(String rfidCard, float weight, int collectorId, String recyclableType) {
  if(WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(serverUrl);
    http.addHeader("Content-Type", "application/json");
    
    // Crear JSON
    StaticJsonDocument<256> doc;
    doc["rfidCardNumber"] = rfidCard;
    doc["weight"] = weight;
    doc["collectorId"] = collectorId;
    doc["recyclableType"] = recyclableType;
    
    String requestBody;
    serializeJson(doc, requestBody);
    
    // Enviar POST
    int httpResponseCode = http.POST(requestBody);
    
    if (httpResponseCode == 201) {
      String response = http.getString();
      Serial.println("Success: " + response);
      
      // Parsear respuesta
      StaticJsonDocument<512> responseDoc;
      deserializeJson(responseDoc, response);
      
      int pointsEarned = responseDoc["pointsEarned"];
      String userName = responseDoc["userName"];
      
      Serial.printf("User %s earned %d points!\\n", userName.c_str(), pointsEarned);
    } else {
      Serial.printf("Error: %d\\n", httpResponseCode);
    }
    
    http.end();
  }
}
```

---

## 📊 Datos de Prueba

### Tarjetas RFID Disponibles:

| RFID Card | Usuario | Email |
|-----------|---------|-------|
| `RFID10000000` | María López | maría.lópez0@email.com |
| `RFID10000001` | José Pérez | josé.pérez1@email.com |
| `RFID10000002` | Ana García | ana.garcía2@email.com |

### Contenedores Disponibles:

IDs: 1, 2, 3, ... (1200 contenedores en total)

---

## 🔒 Seguridad

- ✅ El endpoint es **público** para facilitar la integración IoT
- ✅ Validación de tarjetas RFID (solo tarjetas activas y válidas)
- ✅ Validación de usuarios (solo usuarios activos)
- ✅ Validación de datos (peso positivo, tipo de material válido)
- ✅ Todas las transacciones se registran con timestamp

---

## 📈 Monitoreo

### GET `/api/v1/iot/collections/health`

Verifica que el servicio IoT esté funcionando.

**Response:**
```
IoT Collection Service is running
```

---

## 🚀 Flujo Completo

1. **Usuario escanea su tarjeta RFID** en el dispositivo IoT
2. **Dispositivo pesa los residuos** automáticamente
3. **Usuario selecciona el tipo de material** (o el dispositivo lo detecta)
4. **Dispositivo envía datos** al endpoint `/api/v1/iot/collections/register`
5. **Backend valida** la tarjeta y el usuario
6. **Backend calcula puntos** según peso y tipo de material
7. **Backend actualiza**:
   - Puntos del usuario
   - Registro de recolección
   - Nivel de llenado del contenedor
   - Último uso de la tarjeta RFID
8. **Backend responde** con los puntos ganados y total acumulado
9. **Dispositivo muestra** mensaje de confirmación al usuario

---

## 📞 Soporte

Para más información, consulta la documentación completa en Swagger UI:
- URL: `http://localhost:8081/swagger-ui.html`
- Sección: **IoT Collections**

