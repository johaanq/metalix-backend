# Integración IoT - Metalix Backend

## 📡 Endpoint Simple para Dispositivos IoT

### POST `/api/v1/iot/collections/register`

**Descripción:** Registra una colección de residuos y suma puntos/peso directamente al bañista por su ID.

**Tipo:** Endpoint PÚBLICO (no requiere autenticación)

**URL:** `http://localhost:8081/api/v1/iot/collections/register`

---

## 📋 Request Body

```json
{
  "userId": 7,
  "weight": 5.5,
  "collectorId": 1,
  "recyclableType": "PLASTIC",
  "points": 66
}
```

### Parámetros:

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `userId` | number | ✅ Sí | ID del bañista (usuario) |
| `weight` | number | ✅ Sí | Peso en kilogramos (debe ser positivo) |
| `collectorId` | number | ✅ Sí | ID del contenedor/colector |
| `recyclableType` | string | ✅ Sí | Tipo de material reciclable |
| `points` | number | ✅ Sí | Puntos a sumar (calculados por IoT) |

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
  "message": "Error: User (Bañista) not found with ID: 999"
}
```

### Posibles Errores:

| Error | Status | Causa |
|-------|--------|-------|
| `User (Bañista) not found` | 404 | El ID del bañista no existe |
| `User account is not active` | 400 | Cuenta del bañista desactivada |
| `Waste Collector not found` | 404 | El contenedor no existe |
| `Weight must be positive` | 400 | El peso debe ser mayor a 0 |
| `Points must be zero or positive` | 400 | Los puntos deben ser >= 0 |

---

## 🔧 Cálculo de Puntos (en el dispositivo IoT)

El dispositivo IoT debe calcular los puntos antes de enviarlos al backend.

**Fórmula:**
```
puntos = peso × 10 × multiplicador_tipo
```

**Multiplicadores por tipo:**
- PLASTIC: 1.2x
- METAL: 1.5x
- ELECTRONIC: 2.0x
- HAZARDOUS: 2.5x
- GLASS: 1.1x
- PAPER: 1.0x
- ORGANIC: 0.8x

**Ejemplos de cálculo:**
- 5.5 kg de PLASTIC: `5.5 × 10 × 1.2 = 66 puntos`
- 3.0 kg de METAL: `3.0 × 10 × 1.5 = 45 puntos`
- 2.0 kg de ELECTRONIC: `2.0 × 10 × 2.0 = 40 puntos`

---

## 🚀 Flujo Completo

1. **Bañista escanea su tarjeta RFID** en el dispositivo IoT
2. **Dispositivo identifica al bañista** (obtiene userId del RFID)
3. **Bañista deposita material** en el contenedor
4. **Dispositivo pesa automáticamente** el material
5. **Dispositivo calcula puntos** según peso y tipo (peso × 10 × multiplicador)
6. **Dispositivo envía POST** a `/api/v1/iot/collections/register` con:
   - ID del bañista (userId)
   - Peso registrado
   - Tipo de material
   - Puntos calculados
   - ID del contenedor
7. **Backend procesa:**
   - ✅ Valida que el bañista exista y esté activo
   - ✅ Suma puntos al total del bañista
   - ✅ Suma peso a estadísticas del bañista
   - ✅ Registra la colección en el historial
   - ✅ Actualiza nivel de llenado del contenedor
   - ✅ Marca último uso de RFID (si existe)
8. **Backend responde** con:
   - Nombre del bañista
   - Puntos ganados
   - Total de puntos acumulados
9. **Dispositivo muestra en pantalla:**
   ```
   ¡Bienvenido María López!
   +66 puntos
   Total: 7082 puntos
   ¡Gracias por reciclar!
   ```

---

## 🔍 Obtener ID del Bañista por RFID (en el dispositivo IoT)

El dispositivo IoT debe tener una tabla local (o consultar al backend) para mapear RFID → userId.

### Opción 1: Tabla local en IoT
```cpp
// Mapeo RFID -> UserID almacenado en el dispositivo
struct UserMap {
  String rfid;
  int userId;
};

UserMap users[] = {
  {"RFID10000000", 7},
  {"RFID10000001", 8},
  {"RFID10000002", 9}
};
```

### Opción 2: Consultar backend (Endpoint disponible)

**GET** `/api/v1/rfid-cards/number/{cardNumber}`

Retorna la información de la tarjeta incluyendo el userId.

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

// Mapeo RFID -> UserID (actualizar según base de datos)
struct UserMap {
  String rfid;
  int userId;
};

UserMap users[] = {
  {"A1B2C3D4", 7},   // RFID10000000 -> María López
  {"E5F6G7H8", 8},   // RFID10000001 -> José Pérez
  {"I9J0K1L2", 9}    // RFID10000002 -> Ana García
};

// Función para obtener userId del RFID
int getUserIdFromRfid(String rfidCard) {
  for (int i = 0; i < sizeof(users)/sizeof(users[0]); i++) {
    if (users[i].rfid == rfidCard) {
      return users[i].userId;
    }
  }
  return -1; // No encontrado
}

// Función para calcular puntos
int calculatePoints(float weight, String recyclableType) {
  float multiplier = 1.0;
  
  if (recyclableType == "PLASTIC") multiplier = 1.2;
  else if (recyclableType == "METAL") multiplier = 1.5;
  else if (recyclableType == "GLASS") multiplier = 1.1;
  else if (recyclableType == "ELECTRONIC") multiplier = 2.0;
  else if (recyclableType == "HAZARDOUS") multiplier = 2.5;
  else if (recyclableType == "ORGANIC") multiplier = 0.8;
  
  return (int)(weight * 10 * multiplier);
}

// Función para registrar colección
void registerCollection(int userId, float weight, String recyclableType, int points) {
  if(WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(serverUrl);
    http.addHeader("Content-Type", "application/json");
    
    // Crear JSON
    StaticJsonDocument<256> doc;
    doc["userId"] = userId;
    doc["weight"] = weight;
    doc["collectorId"] = collectorId;
    doc["recyclableType"] = recyclableType;
    doc["points"] = points;
    
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
      
      // Mostrar en LCD
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("Bienvenido!");
      lcd.setCursor(0, 1);
      lcd.print(userName);
      delay(2000);
      
      lcd.clear();
      lcd.setCursor(0, 0);
      lcd.print("+");
      lcd.print(pointsEarned);
      lcd.print(" puntos");
      lcd.setCursor(0, 1);
      lcd.print("Total: ");
      lcd.print(totalPoints);
      delay(5000);
      
      Serial.printf("Success! %s earned %d points. Total: %d\n", 
                    userName.c_str(), pointsEarned, totalPoints);
      
    } else {
      Serial.printf("ERROR: HTTP %d\n", httpResponseCode);
      lcd.clear();
      lcd.print("Error!");
      lcd.setCursor(0, 1);
      lcd.print("Intente de nuevo");
    }
    
    http.end();
  }
}

// Loop principal
void loop() {
  // 1. Esperar escaneo RFID
  lcd.print("Pase su RFID");
  
  if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
    String rfidCard = readRfidCard();
    
    // 2. Obtener userId del RFID
    int userId = getUserIdFromRfid(rfidCard);
    
    if (userId == -1) {
      lcd.clear();
      lcd.print("RFID no valido");
      return;
    }
    
    // 3. Mostrar mensaje y esperar material
    lcd.clear();
    lcd.print("Deposite material");
    
    // 4. Esperar a que depositen material
    delay(2000);
    
    // 5. Pesar material
    float weight = scale.get_units(10);
    
    if (weight > 0.1) {
      // 6. Seleccionar tipo de material
      String materialType = selectMaterialType(); // Botones o automático
      
      // 7. Calcular puntos
      int points = calculatePoints(weight, materialType);
      
      // 8. Registrar en backend
      registerCollection(userId, weight, materialType, points);
    }
    
    // 9. Resetear balanza
    scale.tare();
  }
  
  delay(100);
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
- ✅ Validación de bañistas (usuarios) activos
- ✅ Validación de datos de entrada (peso, puntos, IDs)
- ✅ Todas las transacciones registradas con timestamp
- ✅ Logging completo de operaciones
- ℹ️ **Nota:** El dispositivo IoT debe gestionar la autenticación de RFID localmente

---

## 📈 Monitoreo

### GET `/api/v1/iot/collections/health`

Verifica que el servicio IoT esté funcionando.

**Response:**
```
IoT Collection Service is running
```

---

## 🧪 Prueba Manual

### Linux/Mac:
```bash
curl -X POST http://localhost:8081/api/v1/iot/collections/register \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 7,
    "weight": 5.5,
    "collectorId": 1,
    "recyclableType": "PLASTIC",
    "points": 66
  }'
```

### Windows PowerShell:
```powershell
$body = @{
    userId = 7
    weight = 5.5
    collectorId = 1
    recyclableType = "PLASTIC"
    points = 66
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/api/v1/iot/collections/register" `
    -Method Post -Body $body -ContentType "application/json"
```

---

## 💡 Lo Que Hace el Backend Automáticamente

1. ✅ **Valida al bañista** por su ID
2. ✅ **Suma puntos enviados por IoT** al total del bañista
3. ✅ **Suma peso** a estadísticas del bañista
4. ✅ **Guarda la recolección** en el historial
5. ✅ **Actualiza contenedor** (nivel de llenado + peso)
6. ✅ **Marca uso de RFID** si el bañista tiene tarjeta vinculada
7. ✅ **Retorna confirmación** con datos completos del bañista

---

## 📞 Soporte

- **Swagger UI:** `http://localhost:8081/swagger-ui.html`
- **Sección:** IoT Collections
- **Health Check:** `http://localhost:8081/api/v1/iot/collections/health`
