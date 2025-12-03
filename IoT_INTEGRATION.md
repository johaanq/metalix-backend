# Integración IoT - Metalix Backend

## 📡 Flujo de 2 Pasos para Dispositivos IoT

### Proceso:
1. **PASO 1:** Se pesa el material → se calculan puntos
2. **PASO 2:** Usuario pasa su RFID → puntos y peso se asignan a su cuenta

---

## 🔵 PASO 1: Pesar Material

### POST `/api/v1/iot/collections/weigh`

**Descripción:** Pesa el material y calcula los puntos (sin asignar a usuario aún).

**Tipo:** Endpoint PÚBLICO (no requiere autenticación)

**URL:** `http://localhost:8081/api/v1/iot/collections/weigh`

### Request Body

```json
{
  "weight": 5.5,
  "collectorId": 1,
  "recyclableType": "PLASTIC",
  "deviceId": "IOT-DEVICE-001"
}
```

### Parámetros:

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `weight` | number | ✅ Sí | Peso en kilogramos (debe ser positivo) |
| `collectorId` | number | ✅ Sí | ID del contenedor/colector |
| `recyclableType` | string | ✅ Sí | Tipo de material reciclable |
| `deviceId` | string | ❌ No | ID del dispositivo IoT (opcional) |

### Tipos de Materiales Reciclables:

- `PLASTIC` - Plástico (1.2x puntos)
- `GLASS` - Vidrio (1.1x puntos)
- `METAL` - Metal (1.5x puntos)
- `PAPER` - Papel (1.0x puntos)
- `ORGANIC` - Orgánico (0.8x puntos)
- `ELECTRONIC` - Electrónico (2.0x puntos)
- `HAZARDOUS` - Peligroso (2.5x puntos)

### Response Paso 1 (Éxito):

**Status:** `201 Created`

```json
{
  "sessionToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "weight": 5.5,
  "recyclableType": "PLASTIC",
  "calculatedPoints": 66,
  "message": "Material weighed successfully. 66 points ready to be assigned. Please scan your RFID card.",
  "expiresInSeconds": 300
}
```

**Importante:** Guardar el `sessionToken` para usarlo en el Paso 2.

---

## 🟢 PASO 2: Confirmar con RFID

### POST `/api/v1/iot/collections/confirm`

**Descripción:** Usuario escanea su RFID y los puntos/peso se asignan a su cuenta.

**Tipo:** Endpoint PÚBLICO (no requiere autenticación)

**URL:** `http://localhost:8081/api/v1/iot/collections/confirm`

### Request Body

```json
{
  "sessionToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "rfidCardNumber": "RFID10000000"
}
```

### Parámetros:

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `sessionToken` | string | ✅ Sí | Token de sesión del Paso 1 |
| `rfidCardNumber` | string | ✅ Sí | Número de tarjeta RFID del usuario |

### Response Paso 2 (Éxito):

**Status:** `200 OK`

```json
{
  "collectionId": 123,
  "userId": 7,
  "userEmail": "maría.lópez0@email.com",
  "userName": "María López",
  "weight": 5.5,
  "pointsEarned": 66,
  "totalUserPoints": 4507,
  "recyclableType": "PLASTIC",
  "timestamp": "2025-12-03T10:30:00",
  "success": true,
  "message": "Collection completed successfully! 66 points awarded to María López"
}
```

---

## ❌ Posibles Errores

**Paso 1 - Weigh:**
| Error | Causa |
|-------|-------|
| `Waste Collector not found` | El contenedor no existe |
| `Weight must be positive` | El peso debe ser mayor a 0 |

**Paso 2 - Confirm:**
| Error | Causa |
|-------|-------|
| `Pending collection not found or expired` | Token inválido o sesión expirada (>5 min) |
| `This collection has already been completed` | La colección ya fue confirmada |
| `RFID Card not found` | La tarjeta RFID no existe |
| `RFID Card is not valid or expired` | Tarjeta bloqueada o expirada |
| `RFID Card is not linked to any user` | Tarjeta no asignada a ningún usuario |
| `User account is not active` | Cuenta del usuario desactivada |

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

const char* serverUrl = "http://your-server:8081/api/v1/iot/collections";
String sessionToken = "";

// PASO 1: Pesar material
void weighMaterial(float weight, int collectorId, String recyclableType) {
  if(WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(String(serverUrl) + "/weigh");
    http.addHeader("Content-Type", "application/json");
    
    // Crear JSON
    StaticJsonDocument<256> doc;
    doc["weight"] = weight;
    doc["collectorId"] = collectorId;
    doc["recyclableType"] = recyclableType;
    doc["deviceId"] = "IOT-001";
    
    String requestBody;
    serializeJson(doc, requestBody);
    
    // Enviar POST
    int httpResponseCode = http.POST(requestBody);
    
    if (httpResponseCode == 201) {
      String response = http.getString();
      
      // Parsear respuesta
      StaticJsonDocument<512> responseDoc;
      deserializeJson(responseDoc, response);
      
      sessionToken = responseDoc["sessionToken"].as<String>();
      int points = responseDoc["calculatedPoints"];
      
      Serial.printf("Material weighed: %.2f kg\\n", weight);
      Serial.printf("Points calculated: %d\\n", points);
      Serial.println("Waiting for RFID scan...");
      
      // Mostrar en LCD/Display
      displayMessage("Weight: " + String(weight) + " kg");
      displayMessage("Points: " + String(points));
      displayMessage("Scan your RFID card");
      
    } else {
      Serial.printf("Error weighing: %d\\n", httpResponseCode);
    }
    
    http.end();
  }
}

// PASO 2: Confirmar con RFID
void confirmWithRfid(String rfidCard) {
  if(sessionToken == "" || WiFi.status() != WL_CONNECTED) {
    Serial.println("No active session or no WiFi");
    return;
  }
  
  HTTPClient http;
  http.begin(String(serverUrl) + "/confirm");
  http.addHeader("Content-Type", "application/json");
  
  // Crear JSON
  StaticJsonDocument<256> doc;
  doc["sessionToken"] = sessionToken;
  doc["rfidCardNumber"] = rfidCard;
  
  String requestBody;
  serializeJson(doc, requestBody);
  
  // Enviar POST
  int httpResponseCode = http.POST(requestBody);
  
  if (httpResponseCode == 200) {
    String response = http.getString();
    
    // Parsear respuesta
    StaticJsonDocument<512> responseDoc;
    deserializeJson(responseDoc, response);
    
    String userName = responseDoc["userName"];
    int pointsEarned = responseDoc["pointsEarned"];
    int totalPoints = responseDoc["totalUserPoints"];
    
    Serial.printf("Success! %s earned %d points!\\n", userName.c_str(), pointsEarned);
    Serial.printf("Total points: %d\\n", totalPoints);
    
    // Mostrar en LCD/Display
    displayMessage("Welcome " + userName + "!");
    displayMessage("+" + String(pointsEarned) + " points");
    displayMessage("Total: " + String(totalPoints));
    
    // Limpiar sesión
    sessionToken = "";
    
  } else {
    Serial.printf("Error confirming: %d\\n", httpResponseCode);
    Serial.println("Please try again or contact administrator");
  }
  
  http.end();
}

// Ejemplo de flujo completo
void loop() {
  // Cuando se detecta material en la balanza
  if (scaleReady && weightStable) {
    float weight = readScale();
    String materialType = detectMaterialType(); // O selector manual
    
    weighMaterial(weight, 1, materialType); // PASO 1
    
    // Esperar escaneo RFID (con timeout de 5 minutos)
    waitingForRfid = true;
  }
  
  // Cuando se detecta tarjeta RFID
  if (waitingForRfid && rfidDetected) {
    String rfidCard = readRfidCard();
    confirmWithRfid(rfidCard); // PASO 2
    
    waitingForRfid = false;
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

## 🚀 Flujo Completo del Sistema

### Secuencia de Eventos:

1. **Bañista deposita material** en el contenedor IoT
2. **Dispositivo pesa automáticamente** el material
3. **Usuario selecciona tipo de material** (PLASTIC, METAL, etc.) o el dispositivo lo detecta
4. **Dispositivo envía PASO 1** → `POST /api/v1/iot/collections/weigh`
   - Backend calcula puntos
   - Backend genera sessionToken
   - Backend responde con puntos calculados
5. **Dispositivo muestra en pantalla:**
   ```
   Peso: 5.5 kg
   Puntos: 66
   > PASE SU TARJETA RFID
   ```
6. **Bañista escanea su tarjeta RFID**
7. **Dispositivo envía PASO 2** → `POST /api/v1/iot/collections/confirm`
   - Backend valida RFID
   - Backend identifica usuario
   - Backend asigna puntos y peso al usuario
8. **Backend actualiza:**
   - ✅ Puntos del usuario
   - ✅ Peso total reciclado
   - ✅ Registro de recolección
   - ✅ Nivel de llenado del contenedor
   - ✅ Último uso de tarjeta RFID
9. **Dispositivo muestra confirmación:**
   ```
   ¡Bienvenido María López!
   +66 puntos
   Total: 4507 puntos
   ```

### ⏱️ Tiempo de Sesión:
- La sesión expira en **5 minutos**
- Si el usuario no escanea su RFID a tiempo, debe volver a pesar el material

---

## 📞 Soporte

Para más información, consulta la documentación completa en Swagger UI:
- URL: `http://localhost:8081/swagger-ui.html`
- Sección: **IoT Collections**

