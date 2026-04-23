# MS Lista de Espera

Microservicio RedNorte para gestionar pacientes y registros de lista de espera.

## Stack

- Java 21
- Spring Boot 3.3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Spring Boot Actuator
- JUnit 5, Mockito, H2

## Configuracion

El servicio se configura por variables de entorno.

| Variable | Descripcion | Valor local sugerido |
| --- | --- | --- |
| `SERVER_PORT` | Puerto HTTP del microservicio | `8081` |
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/lista_espera_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario PostgreSQL | `rednorte` |
| `SPRING_DATASOURCE_PASSWORD` | Password PostgreSQL | definir en `.env` |
| `SPRING_FLYWAY_ENABLED` | Ejecuta migraciones Flyway | `true` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Validacion de esquema JPA | `validate` |

Usa `.env.example` como base para crear tu `.env` local.

## Ejecucion local con Maven

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/lista_espera_db
export SPRING_DATASOURCE_USERNAME=rednorte
export SPRING_DATASOURCE_PASSWORD=change_me_dev_only
mvn spring-boot:run
```

## Ejecucion con Docker Compose

```bash
cp .env.example .env
mvn clean package
docker compose up --build
```

Servicios expuestos:

- API: `http://localhost:8081`
- Health: `http://localhost:8081/actuator/health`
- PostgreSQL: `localhost:5432`

## Endpoints principales

- `POST /api/v1/pacientes`
- `GET /api/v1/pacientes`
- `GET /api/v1/pacientes/{id}`
- `POST /api/v1/waitlist`
- `GET /api/v1/waitlist`
- `GET /api/v1/waitlist/{id}`
- `GET /api/v1/waitlist/especialidad/{especialidad}`
- `GET /api/v1/waitlist/prioridad`
- `GET /api/v1/waitlist/count`
- `PATCH /api/v1/waitlist/{id}/cancelar`

## Pruebas

```bash
mvn test
```

La suite cubre dominio, controladores REST y persistencia JPA con H2.

## Requisitos para despliegue Kubernetes

Esta rama no define manifiestos Kubernetes para no intervenir la rama de infraestructura global del equipo.

Para desplegar este microservicio en Kubernetes, el responsable de infraestructura debe considerar:

- Imagen Docker generada desde este `Dockerfile`.
- Puerto de aplicacion: `8081`.
- Healthcheck general: `/actuator/health`.
- Readiness probe sugerida: `/actuator/health/readiness`.
- Liveness probe sugerida: `/actuator/health/liveness`.
- Variables requeridas: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.
- Base de datos PostgreSQL con esquema creado por Flyway.
