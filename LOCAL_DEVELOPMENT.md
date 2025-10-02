# 🧑‍💻 Desarrollo Local - Metalix Backend

Esta guía te ayudará a configurar el backend para desarrollo local.

## Opción 1: MySQL Local (Recomendado para Desarrollo)

### Requisitos Previos
- Java 21 (JDK)
- MySQL 8.0+ instalado y corriendo
- Maven (o usar el wrapper `./mvnw`)

### Configuración

1. **Crear base de datos local:**
```sql
CREATE DATABASE metalix_db;
```

2. **Configurar variables de entorno en IntelliJ:**

Ve a **Run → Edit Configurations** → **Environment variables** y agrega:

```
MYSQL_HOST=localhost;MYSQL_PORT=3306;MYSQL_DATABASE=metalix_db;MYSQL_ROOT_PASSWORD=tu_password_local;JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
```

3. **Ejecutar la aplicación:**
- Desde IntelliJ: Click en el botón ▶️ Run
- Desde terminal: `./mvnw spring-boot:run`

4. **Verificar:**
- API: http://localhost:8081
- Swagger UI: http://localhost:8081/api/v1/swagger-ui.html

### Ventajas de MySQL Local
✅ Desarrollo sin internet  
✅ Baja latencia  
✅ Datos aislados de producción  
✅ Libertad para experimentar

---

## Opción 2: Conectar a Railway Database

Útil cuando quieres trabajar con datos de producción o compartir datos con el equipo.

### Paso 1: Obtener Credenciales de Railway

1. Ve a https://railway.app → Tu proyecto
2. Click en el servicio **MySQL**
3. Ve a **"Connect"** → **"Public Networking"**
4. Copia estos valores:

```
MYSQLHOST: monorail.proxy.rlwy.net (ejemplo)
MYSQLPORT: 12345 (ejemplo)
MYSQLUSER: root
MYSQLPASSWORD: [tu password]
MYSQLDATABASE: railway
```

### Paso 2: Configurar Variables en IntelliJ

**Run → Edit Configurations → Environment variables:**

```
MYSQL_HOST=monorail.proxy.rlwy.net;MYSQL_PORT=12345;MYSQL_DATABASE=railway;MYSQL_ROOT_PASSWORD=tu_password_railway;JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
```

> **Nota:** Reemplaza `monorail.proxy.rlwy.net`, `12345` y `tu_password_railway` con tus valores reales de Railway.

### Paso 3: Ejecutar

Ejecuta la aplicación normalmente. Ahora estás conectado a la base de datos de Railway.

### Ventajas de Railway Database
✅ Datos compartidos con producción  
✅ No necesitas MySQL instalado  
✅ Sincronización con el equipo  
✅ Mismo entorno que producción

### ⚠️ Precauciones
- Los cambios en la BD afectan a producción
- Requiere conexión a internet
- Mayor latencia que localhost

---

## 🔧 Configuración para Otros IDEs

### Visual Studio Code

1. Instala la extensión "Spring Boot Extension Pack"
2. Crea un archivo `.env` en la raíz del proyecto:

```env
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=metalix_db
MYSQL_ROOT_PASSWORD=tu_password
JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
```

3. Ejecuta con: `./mvnw spring-boot:run`

### Terminal / Línea de Comandos

**Windows (PowerShell):**
```powershell
$env:MYSQL_HOST="localhost"
$env:MYSQL_PORT="3306"
$env:MYSQL_DATABASE="metalix_db"
$env:MYSQL_ROOT_PASSWORD="tu_password"
$env:JWT_SECRET="5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437"
./mvnw spring-boot:run
```

**Linux/Mac (Bash):**
```bash
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DATABASE=metalix_db
export MYSQL_ROOT_PASSWORD=tu_password
export JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
./mvnw spring-boot:run
```

---

## 🧪 Ejecutar Tests

```bash
./mvnw test
```

Los tests usan una base de datos H2 en memoria por defecto.

---

## 🔥 Hot Reload

El proyecto incluye Spring Boot DevTools. Los cambios en el código se recargarán automáticamente sin reiniciar la aplicación.

---

## 📦 Compilar sin Ejecutar

```bash
./mvnw clean install
```

El archivo JAR se generará en `target/metalix-backend-0.0.1-SNAPSHOT.jar`

---

## 🐛 Troubleshooting

### Error: "Could not resolve placeholder 'JWT_SECRET'"
**Solución:** Asegúrate de haber configurado las variables de entorno.

### Error: "Communications link failure"
**Solución:** Verifica que MySQL esté corriendo y las credenciales sean correctas.

### Puerto 8081 ya en uso
**Solución:** Cambia el puerto agregando la variable: `PORT=8082`

### Error de Java version
**Solución:** Este proyecto requiere Java 21. Verifica con `java -version`

---

## 📚 Recursos Adicionales

- [Swagger UI Local](http://localhost:8081/api/v1/swagger-ui.html)
- [API Docs](http://localhost:8081/api/v1/api-docs)
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Railway Docs](https://docs.railway.app/)

