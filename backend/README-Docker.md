# 🐳 Lubricentro Backend - Docker Setup

Este proyecto está completamente dockerizado para facilitar el desarrollo y despliegue sin necesidad de instalar dependencias locales.

## 📋 Prerrequisitos

- [Docker](https://docs.docker.com/get-docker/) (versión 20.10+)
- [Docker Compose](https://docs.docker.com/compose/install/) (versión 2.0+)

## 🚀 Inicio Rápido

### 1. Clonar y navegar al proyecto
```bash
cd lubricentro-backend/backend
```

### 2. Ejecutar con Docker Compose
```bash
# Construir e iniciar todos los servicios
docker-compose up --build

# O ejecutar en segundo plano
docker-compose up -d --build
```

### 3. Verificar que todo esté funcionando
```bash
# Ver logs de los servicios
docker-compose logs -f

# Ver estado de los servicios
docker-compose ps
```

## 🌐 Acceso a la Aplicación

- **Backend API**: http://localhost:8080
- **Swagger/OpenAPI**: http://localhost:8080/swagger-ui.html
- **Base de datos**: localhost:5432

## 🗄️ Base de Datos

- **Host**: localhost
- **Puerto**: 5432
- **Base de datos**: lubricentro_db
- **Usuario**: postgres
- **Contraseña**: postgres123

## 🔧 Comandos Útiles

### Gestión de servicios
```bash
# Iniciar servicios
docker-compose up

# Iniciar en segundo plano
docker-compose up -d

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Reconstruir e iniciar
docker-compose up --build

# Ver logs de un servicio específico
docker-compose logs -f backend
docker-compose logs -f postgres
```

### Gestión de contenedores
```bash
# Ver contenedores ejecutándose
docker ps

# Ejecutar comandos en el contenedor del backend
docker exec -it lubricentro_backend bash

# Ejecutar comandos en PostgreSQL
docker exec -it lubricentro_postgres psql -U postgres -d lubricentro_db

# Ver logs del contenedor
docker logs lubricentro_backend
```

### Base de datos
```bash
# Conectar a PostgreSQL desde el host
psql -h localhost -U postgres -d lubricentro_db

# Ejecutar migraciones manualmente (si es necesario)
docker exec -it lubricentro_backend ./mvnw flyway:migrate
```

## 🐛 Troubleshooting

### Problemas comunes

1. **Puerto 8080 ocupado**
   ```bash
   # Cambiar puerto en docker-compose.yml
   ports:
     - "8081:8080"  # Cambiar 8081 por el puerto deseado
   ```

2. **Puerto 5432 ocupado**
   ```bash
   # Cambiar puerto en docker-compose.yml
   ports:
     - "5433:5432"  # Cambiar 5433 por el puerto deseado
   ```

3. **Problemas de permisos en macOS/Linux**
   ```bash
   # Dar permisos al Maven wrapper
   chmod +x mvnw
   ```

4. **Limpiar Docker completamente**
   ```bash
   # Eliminar todos los contenedores, imágenes y volúmenes
   docker system prune -a --volumes
   ```

### Verificar logs
```bash
# Ver logs del backend
docker-compose logs backend

# Ver logs de PostgreSQL
docker-compose logs postgres

# Ver logs en tiempo real
docker-compose logs -f
```

## 🔄 Desarrollo

### Reconstruir después de cambios
```bash
# Reconstruir solo el backend
docker-compose build backend

# Reconstruir y reiniciar
docker-compose up --build backend
```

### Hot reload (desarrollo)
Para desarrollo con hot reload, puedes montar el código fuente:
```yaml
# En docker-compose.yml, agregar al servicio backend:
volumes:
  - ./src:/app/src
```

## 📁 Estructura de Archivos Docker

- `Dockerfile` - Configuración del contenedor de la aplicación
- `docker-compose.yml` - Orquestación de servicios
- `.dockerignore` - Archivos a excluir del build
- `application-docker.yml` - Configuración específica para Docker

## 🔐 Variables de Entorno

Las siguientes variables están configuradas en `docker-compose.yml`:

- `SPRING_DATASOURCE_URL` - URL de conexión a PostgreSQL
- `SPRING_DATASOURCE_USERNAME` - Usuario de la base de datos
- `SPRING_DATASOURCE_PASSWORD` - Contraseña de la base de datos
- `APP_SECURITY_JWT_SECRET` - Clave secreta para JWT (Base64)
- `APP_SECURITY_JWT_ACCESS_MINUTES` - Tiempo de expiración del token de acceso
- `APP_SECURITY_JWT_REFRESH_DAYS` - Tiempo de expiración del token de refresh

## 🚀 Producción

Para producción, considera:

1. Cambiar las contraseñas por defecto
2. Usar variables de entorno para configuraciones sensibles
3. Configurar volúmenes persistentes para la base de datos
4. Implementar health checks más robustos
5. Configurar logging centralizado

## 📞 Soporte

Si encuentras problemas:

1. Verifica que Docker esté ejecutándose
2. Revisa los logs con `docker-compose logs`
3. Asegúrate de que los puertos no estén ocupados
4. Verifica que tienes permisos para ejecutar Docker
