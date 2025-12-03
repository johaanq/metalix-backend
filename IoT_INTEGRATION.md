# Integración IoT - Metalix Backend

## 📡 Endpoint Simple para Dispositivos IoT

### POST `/api/v1/iot/collections/register`

**Descripción:** Registra una colección de residuos y suma puntos/peso al bañista vinculado al RFID.

**Tipo:** Endpoint PÚBLICO (no requiere autenticación)

**URL:** `http://localhost:8081/api/v1/iot/collections/register`

---

## 📋 Request Body

```json
{
  "rfidCardNumber": "RFID10000000",
  "weight": 5.5,
  "collectorId": 1,
  "recyclableType": "PLASTIC"
}
```

### Parámetros:

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `rfidCardNumber` | string | ✅ Sí | Número de tarjeta RFID del bañista |
| `weight` | number | ✅ Sí | Peso en kilogramos (debe ser positivo) |
| `collectorId` | number | ✅ Sí | ID del contenedor/colector |
| `recyclableType` | string | ✅ Sí | Tipo de material reciclable |

### Tipos de Materiales Reciclables:

| Tipo | Multiplicador | Descripción |
|------|---------------|-------------|
| `PLASTIC` | 1.2x | Plástico |
| `GLASS` | 1.1x | Vidrio |
| `METAL` | 1.5x | Metal |
| `PAPER` | 1.0x | Papel |
| `ORGANIC` | 0.8x | Orgánico |
| `ELECTRONIC` | 2.0x | Electrónico |
| `HAZARDOUS` | 2.5x | Peligroso |

---

## ✅ Response (Éxito)

**Status:** `201 Created`

```json
{
  "success": true,
  "collectionId": 123,
  "userId": 7,
  "userEmail": "maría.lópez0@email.com",
  "userName": "María López",
  "weight": 5.5,
  "pointsEarned": 66,
  "totalUserPoints": 4507,
  "recyclableType": "PLASTIC",
  "timestamp": "2025-12-03T10:30:00",
  "message": "Collection completed! +66 points awarded to María. Total: 4507"
}
```

---

## ❌ Response (Error)

**Status:** `400 Bad Request` o `404 Not Found`

```json
{
  "success": false,
  "message": "Error: RFID Card not found: RFID99999999"
}
```

### Posibles Errores:

| Error | Status | Causa |
|-------|--------|-------|
| `RFID Card not found` | 404 | La tarjeta RFID no existe en el sistema |
| `RFID Card is not valid or expired` | 400 | Tarjeta bloqueada o expirada |
| `RFID Card is not linked to any user` | 400 | Tarjeta no vinculada a ningún usuario |
| `User account is not active` | 400 | Cuenta del usuario desactivada |
| `Waste Collector not found` | 404 | El contenedor no existe |

---

## 🔧 Cálculo de Puntos

**Fórmula:**
```
puntos = peso × 10 × multiplicador_tipo
```

**Ejemplos:**
- 5.5 kg de PLASTIC: `5.5 × 10 × 1.2 = 66 puntos`
- 3.0 kg de METAL: `3.0 × 10 × 1.5 = 45 puntos`
- 2.0 kg de ELECTRONIC: `2.0 × 10 × 2.0 = 40 puntos`

---

## 🚀 Flujo Completo

1. **Bañista deposita material** en el contenedor IoT
2. **Dispositivo pesa automáticamente** el material
3. **Bañista selecciona tipo de material** en el dispositivo
4. **Bañista escanea su tarjeta RFID**
5. **Dispositivo envía POST** a `/api/v1/iot/collections/register` con:
   - RFID card number
   - Peso
   - Tipo de material
   - ID del contenedor
6. **Backend procesa:**
   - ✅ Valida RFID y usuario
   - ✅ Calcula puntos según peso y tipo
   - ✅ Suma puntos al total del bañista
   - ✅ Registra peso en estadísticas
   - ✅ Actualiza nivel de contenedor
   - ✅ Marca último uso de RFID
7. **Backend responde** con:
   - Nombre del bañista
   - Puntos ganados
   - Total de puntos acumulados
8. **Dispositivo muestra en pantalla:**
   ```
   ¡Bienvenido María López!
   +66 puntos
   Total: 4507 puntos
   ¡Gracias por reciclar!
   ```

---

## 🔗 Vincular Tarjeta RFID a Bañista

### POST `/api/v1/rfid-cards/assign`

**Requiere autenticación:** ✅ Sí (SYSTEM_ADMIN o MUNICIPALITY_ADMIN)

```json
{
  "cardNumber": "RFID10000000",
  "userId": 7
}
```

**Respuesta:**
```json
{
  "id": 1,
  "cardNumber": "RFID10000000",
  "userId": 7,
  "status": "ACTIVE",
  "issuedDate": "2025-12-01",
  "expirationDate": "2027-12-01"
}
```

---

## 🧪 Ejemplo de Uso desde Arduino/ESP32

```cpp
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <MFRC522.h>  // Para lector RFID
#include <HX711.h>    // Para balanza

const char* serverUrl = "http://your-server:8081/api/v1/iot/collections/register";
const int collectorId = 1;

// Función para registrar colección
void registerCollection(String rfidCard, float weight, String recyclableType) {
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
    
    Serial.println("Sending to backend...");
    Serial.println(requestBody);
    
    // Enviar POST
    int httpResponseCode = http.POST(requestBody);
    
    if (httpResponseCode == 201) {
      String response = http.getString();
      
      // Parsear respuesta
      StaticJsonDocument<512> responseDoc;
      deserializeJson(responseDoc, response);
      
      String userName = responseDoc["userName"];
      int pointsEarned = responseDoc["pointsEarned"];
      int totalPoints = responseDoc["totalUserPoints"];
      
      // Mostrar en LCD o Serial
      Serial.println("\n=== SUCCESS ===");
      Serial.printf("Welcome %s!\n", userName.c_str());
      Serial.printf("+%d points\n", pointsEarned);
      Serial.printf("Total: %d points\n", totalPoints);
      
      // Mostrar en display LCD
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Welcome ");
      lcd.print(userName);
      lcd.setCursor(0, 1);
      lcd.print("+");
      lcd.print(pointsEarned);
      lcd.print(" pts  Total:");
      lcd.print(totalPoints);
      
      delay(5000);
      
    } else {
      Serial.printf("ERROR: HTTP %d\n", httpResponseCode);
      String response = http.getString();
      Serial.println(response);
      
      lcd.clear();
      lcd.print("Error!");
      lcd.setCursor(0, 1);
      lcd.print("Try again");
    }
    
    http.end();
  } else {
    Serial.println("WiFi not connected");
  }
}

// Loop principal
void loop() {
  // 1. Esperar material en balanza
  if (scale.is_ready()) {
    float weight = scale.get_units(10); // Leer peso promedio
    
    if (weight > 0.1) { // Material detectado
      Serial.printf("Weight detected: %.2f kg\n", weight);
      
      // 2. Seleccionar tipo de material (ejemplo: botones)
      String materialType = selectMaterialType();
      
      // 3. Esperar escaneo RFID
      Serial.println("Waiting for RFID scan...");
      lcd.clear();
      lcd.print("Scan your RFID");
      
      String rfidCard = waitForRfidScan(); // Bloquea hasta leer RFID
      
      if (rfidCard != "") {
        // 4. Registrar colección
        registerCollection(rfidCard, weight, materialType);
      }
      
      // 5. Resetear balanza
      scale.tare();
    }
  }
  
  delay(100);
}

// Función helper para leer RFID
String waitForRfidScan() {
  unsigned long startTime = millis();
  unsigned long timeout = 60000; // 60 segundos timeout
  
  while (millis() - startTime < timeout) {
    if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
      String rfidStr = "";
      for (byte i = 0; i < mfrc522.uid.size; i++) {
        rfidStr += String(mfrc522.uid.uidByte[i], HEX);
      }
      rfidStr.toUpperCase();
      return "RFID" + rfidStr;
    }
    delay(50);
  }
  
  return ""; // Timeout
}
```

---

## 📊 Datos de Prueba

### Tarjetas RFID Vinculadas a Bañistas:

| RFID Card | Nombre | Email | Puntos Actuales |
|-----------|--------|-------|-----------------|
| `RFID10000000` | María López | maría.lópez0@email.com | Variable |
| `RFID10000001` | José Pérez | josé.pérez1@email.com | Variable |
| `RFID10000002` | Ana García | ana.garcía2@email.com | Variable |
| `RFID10000003` | Carlos Rodríguez | carlos.rodríguez3@email.com | Variable |
| ... | ... | ... | ... |

**Total:** 15 tarjetas RFID activas y vinculadas

### Contenedores Disponibles:

- IDs: 1 a 1200 (1200 contenedores en total)
- Distribuidos en 5 municipalidades
- Diferentes zonas y tipos

---

## 🔒 Seguridad

- ✅ Endpoint **público** (no requiere autenticación)
- ✅ Validación de tarjetas RFID activas y válidas
- ✅ Verificación de usuarios activos
- ✅ Validación de datos de entrada
- ✅ Todas las transacciones registradas con timestamp
- ✅ Logging completo de operaciones

---

## 📈 Monitoreo

### GET `/api/v1/iot/collections/health`

Verifica que el servicio IoT esté funcionando.

**Response:**
```
IoT Collection Service is running
```

---

## 🧪 Prueba Manual con Curl

### Linux/Mac:
```bash
curl -X POST http://localhost:8081/api/v1/iot/collections/register \
  -H "Content-Type: application/json" \
  -d '{
    "rfidCardNumber": "RFID10000000",
    "weight": 5.5,
    "collectorId": 1,
    "recyclableType": "PLASTIC"
  }'
```

### Windows PowerShell:
```powershell
$body = @{
    rfidCardNumber = "RFID10000000"
    weight = 5.5
    collectorId = 1
    recyclableType = "PLASTIC"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/api/v1/iot/collections/register" `
    -Method Post -Body $body -ContentType "application/json"
```

---

## 💡 Lo Que Hace el Backend Automáticamente

1. ✅ **Identifica al bañista** por su tarjeta RFID
2. ✅ **Calcula puntos** según peso y tipo de material
3. ✅ **Suma puntos** al total del bañista
4. ✅ **Registra peso** en estadísticas del bañista
5. ✅ **Guarda la recolección** en el historial
6. ✅ **Actualiza contenedor** (nivel de llenado)
7. ✅ **Marca uso de RFID** (timestamp de último uso)
8. ✅ **Retorna confirmación** con datos del bañista y puntos

---

## 📞 Soporte

- **Swagger UI:** `http://localhost:8081/swagger-ui.html`
- **Sección:** IoT Collections
- **Health Check:** `http://localhost:8081/api/v1/iot/collections/health`
