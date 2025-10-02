# 🚂 Despliegue en Railway

## 📋 Pasos Rápidos

### 1. Crear Proyecto en Railway
1. Ve a [Railway](https://railway.app/)
2. Click en **"New Project"**
3. Selecciona **"Deploy from GitHub repo"**
4. Conecta tu repositorio

### 2. Agregar MySQL
1. En tu proyecto, click **"+ New"**
2. Selecciona **"Database"** → **"MySQL"**
3. Railway creará la base de datos automáticamente

### 3. Configurar Variables de Entorno

Ve a tu servicio de la aplicación → **"Variables"** y agrega:

```bash
DATABASE_URL = jdbc:mysql://[MYSQLHOST]:[MYSQLPORT]/railway?createDatabaseIfNotExist=true&useSSL=true&serverTimezone=UTC
DATABASE_USERNAME = [MYSQLUSER]
DATABASE_PASSWORD = [MYSQLPASSWORD]

JWT_SECRET = 5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
JWT_EXPIRATION = 86400000

SPRING_PROFILES_ACTIVE = prod
```

**¿Dónde obtener las credenciales de MySQL?**
- Click en el servicio **MySQL** en Railway
- Ve a **"Connect"**
- Copia los valores de `MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, `MYSQLPASSWORD`

### 4. Deploy
Railway desplegará automáticamente. Espera 5-10 minutos.

## 🔗 URLs

Una vez desplegado:
- **API Base**: `https://tu-app.up.railway.app`
- **Swagger**: `https://tu-app.up.railway.app/api/v1/swagger-ui.html`

## ✅ Listo

Cada push a GitHub redesplegará automáticamente.

