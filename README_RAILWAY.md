# 🚂 Despliegue en Railway

## 📋 Pasos Rápidos

### 1. Crear Proyecto en Railway
1. Ve a [Railway](https://railway.app/)
2. Click en **"New Project"**
3. Selecciona **"Deploy from GitHub repo"**
4. Conecta tu repositorio: `https://github.com/johaanq/metalix-backend.git`

### 2. Agregar MySQL
1. En tu proyecto, click **"+ New"**
2. Selecciona **"Database"** → **"MySQL"**
3. Railway creará la base de datos automáticamente

### 3. Configurar Variables de Entorno

Ve a tu servicio de la aplicación → **"Variables"** y agrega:

```bash
# MySQL Configuration
MYSQL_HOST = [Copiar de MySQL service → Connect → MYSQLHOST]
MYSQL_PORT = [Copiar de MySQL service → Connect → MYSQLPORT]
MYSQL_DATABASE = railway
MYSQL_ROOT_PASSWORD = [Copiar de MySQL service → Connect → MYSQLPASSWORD]

# JWT Configuration
JWT_SECRET = 5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437

# Spring Profile (opcional)
SPRING_PROFILES_ACTIVE = prod
```

**¿Dónde obtener las credenciales de MySQL?**
- Click en el servicio **MySQL** en Railway
- Ve a la pestaña **"Connect"**
- Copia los valores de las variables

### 4. Deploy
Railway desplegará automáticamente. Espera 5-10 minutos.

## 🔗 URLs

Una vez desplegado:
- **API Base**: `https://tu-app.up.railway.app`
- **Swagger UI**: `https://tu-app.up.railway.app/api/v1/swagger-ui.html`
- **API Docs**: `https://tu-app.up.railway.app/api/v1/api-docs`

## 🧑‍💻 Desarrollo Local con Railway Database

Puedes ejecutar tu backend localmente conectado a la base de datos de Railway:

### Paso 1: Obtener Credenciales de Railway
1. Ve a tu proyecto en Railway
2. Click en el servicio **MySQL**
3. Ve a **"Connect"** → **"Public Networking"**
4. Copia las siguientes variables:
   - `MYSQLHOST`
   - `MYSQLPORT` 
   - `MYSQLUSER` (usualmente `root`)
   - `MYSQLPASSWORD`
   - `MYSQLDATABASE` (usualmente `railway`)

### Paso 2: Configurar Variables de Entorno Locales

**En IntelliJ IDEA:**
1. Ve a **Run → Edit Configurations**
2. Selecciona tu configuración `MetalixBackendApplication`
3. En **Environment variables**, agrega (reemplaza con tus valores de Railway):

```
MYSQL_HOST=monorail.proxy.rlwy.net;MYSQL_PORT=12345;MYSQL_DATABASE=railway;MYSQL_ROOT_PASSWORD=tu_password_de_railway;JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
```

**Nota:** Separa las variables con `;` en Windows o `:` en Mac/Linux.

### Paso 3: Ejecutar Localmente

Ejecuta la aplicación desde IntelliJ. La aplicación se conectará a la base de datos en Railway y podrás:
- Hacer pruebas localmente
- Ver logs en tu consola
- Usar el debugger
- Los cambios en la BD se reflejarán en Railway

### Ventajas
✅ Datos compartidos entre local y producción  
✅ No necesitas MySQL instalado localmente  
✅ Pruebas con datos reales  
✅ Sincronización automática con el equipo

### Desventajas
⚠️ Cambios en BD afectan a todos  
⚠️ Requiere conexión a internet  
⚠️ Latencia puede ser mayor que local

## 🔄 CI/CD Automático

Cada push a la rama `master` en GitHub redesplegará automáticamente en Railway.

## ✅ Verificar Despliegue

1. Abre tu URL de Railway: `https://tu-app.up.railway.app/api/v1/swagger-ui.html`
2. Prueba el endpoint de salud (si existe) o cualquier endpoint público
3. Verifica los logs en Railway Dashboard

## 🐛 Troubleshooting

**Error de conexión a MySQL:**
- Verifica que las variables `MYSQL_*` estén correctamente configuradas
- Asegúrate de usar el `MYSQLHOST` y `MYSQLPORT` del servicio MySQL en Railway

**Aplicación no inicia:**
- Revisa los logs en Railway Dashboard
- Verifica que `JWT_SECRET` esté configurado
- Asegúrate de que Java 21 esté configurado en Railway

**Timeout en despliegue:**
- Railway tiene un límite de 10 minutos para el build
- Si falla, intenta redeploy manualmente desde Railway Dashboard
