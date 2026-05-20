# RedNorte - Sistema de Lista de Espera Médica

Sistema de gestión de lista de espera médica con microservicios, desarrollado con Spring Boot, Kafka, Keycloak y Docker.

## Requisitos previos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y corriendo
- Git

## Levantar el proyecto

### 1. Clonar el repositorio (rama v3)

```bash
git clone -b v3 https://github.com/Marco-Parra25/FullStack-3.git rednorte
cd rednorte
```

### 2. Crear el archivo `.env`

Crea un archivo `.env` en la raíz del proyecto con el siguiente contenido:

```env
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=test@test.com
SPRING_MAIL_PASSWORD=test
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_SMTP_STARTTLS_ENABLE=true
NOTIFICACIONES_EMAIL_ENABLED=false
NOTIFICACIONES_EMAIL_FROM=test@test.com
```

> Este archivo no se sube a git por seguridad. Solo hay que crearlo una vez por máquina.

### 3. Iniciar todos los servicios

```bash
docker compose up --build -d
```

La primera vez tarda varios minutos porque descarga las imágenes y compila los microservicios.

### 4. Verificar que todo esté corriendo

```bash
docker compose ps
```

Todos los contenedores deben aparecer como `Up` o `healthy`.

---

## URLs de acceso

| Servicio | URL |
|---|---|
| **Frontend** | http://localhost:3000 |
| **API Gateway** | http://localhost:8088 |
| **Keycloak Admin** | http://localhost:8090 |
| **Grafana** | http://localhost:3002 |
| **Prometheus** | http://localhost:9090 |
| **Mailhog** (emails) | http://localhost:8025 |

---

## Usuarios de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `medico` | `medico123` | Médico |
| `paciente` | `paciente123` | Paciente |

> Los pacientes creados desde el frontend usan su **RUT como usuario** y el **RUT sin puntos ni guión como contraseña** (ej: usuario `12345678-9`, contraseña `123456789`).

---

## Comandos útiles

```bash
# Levantar (sin reconstruir imágenes)
docker compose up -d

# Levantar y reconstruir imágenes (cuando hay cambios en el código)
docker compose up --build -d

# Bajar todos los servicios
docker compose down

# Ver logs de un servicio específico
docker compose logs ms-lista-espera --tail=50

# Ver logs en tiempo real
docker compose logs -f ms-reasignacion
```

---

## Arquitectura

El sistema está compuesto por los siguientes microservicios:

- **ms-lista-espera** — Gestión de pacientes y lista de espera (Spring Boot + PostgreSQL)
- **ms-reasignacion** — Reasigna cupos liberados automáticamente (Spring Boot + Kafka)
- **ms-notificaciones** — Envía notificaciones por email (Spring Boot + Kafka)
- **ms-api-gateway** — Enrutamiento, seguridad JWT y circuit breaker (Spring Cloud Gateway)
- **bff** — Backend for Frontend, intermediario entre el frontend y los microservicios (Node.js)
- **frontend** — Interfaz de usuario (React + Vite)

**Infraestructura:** Kafka (mensajería), Keycloak (autenticación OAuth2), PostgreSQL (persistencia), Prometheus + Grafana (monitoreo).

---

## Ramas del repositorio

| Rama | Descripción |
|---|---|
| `v3` | Versión estable integrada con todos los microservicios |
| `testv1` | Versión de prueba anterior |
| `test-v2` | Versión de integración |
| `feature/api-gateway-infraestructura` | Desarrollo del API Gateway |
