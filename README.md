# ⚠️ Deprecated

Este repositorio ya no se mantiene.  
Por favor usar: https://github.com/SeRoGra/ecohome-store

# 🌿 EcoHome Store — Backend API

Backend REST para la plataforma e-commerce de EcoHome Store, implementado con **Clean Architecture** siguiendo el estándar del plugin **Scaffold Bancolombia**.

## Stack

| Capa | Tecnología |
|------|-----------|
| Framework | Spring Boot 3.3 + WebFlux (reactivo) |
| Base de datos | PostgreSQL 15 (R2DBC) |
| Autenticación | JWT (JJWT 0.12) + BCrypt |
| Arquitectura | Clean Architecture (Scaffold Bancolombia) |
| Java | 21 |
| Build | Gradle 9 |

## Estructura de módulos (Scaffold Bancolombia)

```
ecohome-backend/
├── domain/                          ← Entidades y puertos (sin deps externas)
├── use-cases/                       ← Lógica de negocio
├── infrastructure/
│   ├── driven-adapters/
│   │   └── postgres-repository/     ← Repositorios R2DBC
│   └── entry-points/
│       └── reactive-web/            ← Handlers, Router, JWT, Security
└── applications/
    └── app-service/                 ← Main, configuración, SQL
```

## Inicio rápido

### 1. Levantar PostgreSQL con Docker
```bash
docker-compose up -d
```

### 2. Configurar variables de entorno
```bash
cp .env.example .env
```

### 3. Ejecutar la aplicación
```bash
./gradlew :applications:app-service:bootRun
```

El servidor arranca en `http://localhost:8080`.  
Las tablas y los datos semilla se crean automáticamente al primer arranque.

---

## API Reference

### Autenticación

#### `POST /api/auth/signup`
```json
// Request
{ "username": "juan", "email": "juan@ecohome.co", "password": "Eco$2025!", "role": "ADMIN" }

// Response 201
{ "id": "uuid", "email": "juan@ecohome.co", "role": "ADMIN" }
```

#### `POST /api/auth/login`
```json
// Request
{ "email": "juan@ecohome.co", "password": "Eco$2025!" }

// Response 200
{ "token": "eyJ...", "role": "ADMIN", "email": "juan@ecohome.co" }
```

### Productos (requieren `Authorization: Bearer <token>`)

| Método | Ruta | Rol mínimo | Descripción |
|--------|------|-----------|-------------|
| GET | `/api/products` | CLIENT | Listar todos |
| GET | `/api/products/:id` | CLIENT | Obtener uno |
| POST | `/api/products` | **ADMIN** | Crear |
| PUT | `/api/products/:id` | **ADMIN** | Reemplazar |
| PATCH | `/api/products/:id` | **ADMIN** | Actualización parcial |
| DELETE | `/api/products/:id` | **ADMIN** | Eliminar |

### Códigos de respuesta

| Código | Significado |
|--------|-------------|
| 200 | OK |
| 201 | Creado |
| 204 | Sin contenido (DELETE exitoso) |
| 400 | Datos inválidos (name/price faltante o price ≤ 0) |
| 401 | Sin token o token inválido/expirado |
| 403 | Token válido pero sin permisos (CLIENT intentando escribir) |
| 404 | Producto no encontrado |
| 409 | Email ya registrado |

---

## Flujo de prueba completo (cURL)

```bash
# 1. Crear usuario admin
curl -X POST http://localhost:8080/api/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{"username":"adminEco","email":"admin@ecohome.co","password":"Eco$2025!","role":"ADMIN"}'

# 2. Login y guardar token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@ecohome.co","password":"Eco$2025!"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "Token: $TOKEN"

# 3. Listar productos (GET público para autenticados)
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"

# 4. Crear producto
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Bolsa reutilizable L","price":6500,"stock":200}'

# 5. Actualizar (reemplazar completo)
curl -X PUT http://localhost:8080/api/products/<UUID> \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Bolsa reutilizable L Premium","price":7200,"stock":150}'

# 6. Actualización parcial (solo precio)
curl -X PATCH http://localhost:8080/api/products/<UUID> \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"price":6800}'

# 7. Eliminar
curl -X DELETE http://localhost:8080/api/products/<UUID> \
  -H "Authorization: Bearer $TOKEN"

# 8. Sin token → 401
curl http://localhost:8080/api/products

# 9. Crear usuario CLIENT e intentar crear producto → 403
curl -X POST http://localhost:8080/api/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{"username":"cliente1","email":"cliente@ecohome.co","password":"Pass123!","role":"CLIENT"}'

TOKEN_CLIENT=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"cliente@ecohome.co","password":"Pass123!"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN_CLIENT" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Producto ilegal","price":1}'
# → 403 Forbidden ✓
```

---

## Variables de entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | Host PostgreSQL |
| `DB_PORT` | `5432` | Puerto PostgreSQL |
| `DB_NAME` | `ecohome` | Nombre de la base de datos |
| `DB_USER` | `postgres` | Usuario BD |
| `DB_PASS` | `postgres` | Contraseña BD |
| `JWT_SECRET` | *(valor por defecto inseguro)* | Clave de firma JWT (mín. 32 chars) |
| `JWT_EXPIRATION_MS` | `86400000` | Expiración del token (24h) |

> ⚠️ En producción siempre define `JWT_SECRET` con un valor seguro generado aleatoriamente.
