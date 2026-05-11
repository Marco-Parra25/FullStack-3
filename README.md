# RedNorte

Repositorio de apoyo para el proyecto `FullStack-3`.

La rama `main` se usa como punto de entrada y documentacion general. El codigo del proyecto esta distribuido en ramas de trabajo por componente o por frente de infraestructura.

## Estado actual

- `main`: contiene solo este `README.md`.
- El microservicio de lista de espera vive en la rama `feature/Infraestructura-MS-ListaDeEspera`.

## Ramas remotas verificadas

- `main`
  Rama de documentacion general del repositorio.

- `feature/Infraestructura-MS-ListaDeEspera`
  Contiene el microservicio `rednorte-project/ms-lista-espera` desarrollado con Spring Boot, JPA, Flyway y pruebas automatizadas.

- `feature/api-gateway-infraestructura`
  Contiene trabajo de API Gateway, manifiestos de Kubernetes, monitoreo con Prometheus, integracion BFF y una copia del contexto `rednorte-project`.

- `feature/bff-frontend-dominique`
  Contiene el BFF y el frontend. Esta rama incluye dependencias versionadas dentro del arbol, por lo que conviene revisarla con cuidado antes de mezclar cambios.

- `feat-ms-reasignacion-hp`
  Contiene el microservicio `ms-reasignacion` y una version anterior del contexto `rednorte-project`.

## Uso recomendado

- Usa `main` para documentacion y referencia general del repo.
- Usa `feature/Infraestructura-MS-ListaDeEspera` para trabajar en el microservicio de lista de espera.
- Si vas a revisar otros componentes, hazlo directamente en la rama remota correspondiente.

## Nota

Las ramas locales `backup/...` son ramas de resguardo de trabajo y no forman parte del remoto oficial.
