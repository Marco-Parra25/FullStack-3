# Resumen de cambios para estabilizar el pipeline

Rama trabajada: `test-unificacion`

## Objetivo

Dejar el pipeline de GitHub Actions funcionando sin romperse por dependencias desincronizadas, permisos de seguridad, Buildx, SonarCloud, Kubernetes o Slack no configurados.

## Cambios realizados

### 1. Sincronizacion del lockfile del BFF

Problema:

- El pipeline fallaba en `npm ci`.
- `bff/package.json` tenia `nodemon` en `devDependencies`, pero `bff/package-lock.json` no incluia `nodemon` ni sus dependencias.

Fix:

- Se actualizo `bff/package-lock.json`.
- Se valido localmente que `npm ci` del BFF pasara correctamente.

Commit:

- `a3afaa2 fix: sync bff package lock`

### 2. Permisos para subir resultados SARIF

Problema:

- El paso `github/codeql-action/upload-sarif` fallaba con `Resource not accessible by integration`.
- El workflow no tenia permisos suficientes para subir resultados a Code Scanning.

Fix:

- Se agregaron permisos al job `build-and-test`:
  - `contents: read`
  - `actions: read`
  - `security-events: write`
- Se actualizo `github/codeql-action/upload-sarif` de `v3` a `v4`.
- Se evito ejecutar el upload SARIF en PRs sin permisos.
- Se dejo `continue-on-error: true` para que el upload SARIF no rompa el build.

Commit:

- `ab8ccbb fix: allow sarif upload in ci`

### 3. Correccion de Docker Buildx y cache GHA

Problema:

- El job Docker fallaba con:
  - `Cache export is not supported for the docker driver`
- `cache-to: type=gha` requiere un builder Buildx compatible.

Fix:

- Se agrego `docker/setup-buildx-action@v3`.
- Se agregaron permisos al job `build-and-push`:
  - `contents: read`
  - `packages: write`

Commit:

- `96819d4 fix: stabilize docker and sonar ci jobs`

### 4. Contextos Docker por servicio

Problema:

- El workflow usaba `context: .` para todos los servicios.
- Algunos Dockerfiles esperan que el contexto sea su propio directorio.

Fix:

- Se cambio la matrix Docker para declarar contexto y Dockerfile por servicio:
  - `ms-lista-espera`: `./rednorte-project/ms-lista-espera`
  - `ms-reasignacion`: `./ms-reasignacion`
  - `ms-api-gateway`: `./ms-api-gateway`
  - `bff`: `./bff`

Commit:

- `96819d4 fix: stabilize docker and sonar ci jobs`

### 5. SonarCloud como paso opcional

Problema:

- SonarCloud fallaba porque faltaban:
  - `sonar.projectKey`
  - `sonar.organization`

Fix:

- SonarCloud ahora solo corre si estan configurados:
  - `SONAR_TOKEN`
  - `SONAR_PROJECT_KEY`
  - `SONAR_ORGANIZATION`
- Si falta alguna configuracion, el workflow lo salta sin romper el pipeline.

Commit:

- `96819d4 fix: stabilize docker and sonar ci jobs`

### 6. Deploy staging protegido

Problema:

- `deploy-staging` intentaba ejecutar `kubectl` sin kubeconfig valido.
- Al no tener `KUBE_CONFIG_STAGING`, `kubectl` terminaba intentando conectar a `localhost:8080`.

Fix:

- `deploy-staging` ahora solo corre si la variable del repo esta activa:
  - `ENABLE_STAGING_DEPLOY=true`
- Si se activa el deploy pero falta `KUBE_CONFIG_STAGING`, falla con mensaje claro.

Commit:

- `3739997 fix: gate optional deploy notifications`

### 7. Deploy production protegido

Problema:

- `deploy-production` podia fallar por la misma razon que staging si no habia kubeconfig.

Fix:

- `deploy-production` ahora solo corre si la variable del repo esta activa:
  - `ENABLE_PRODUCTION_DEPLOY=true`
- Si se activa el deploy pero falta `KUBE_CONFIG_PROD`, falla con mensaje claro.

Commit:

- `3739997 fix: gate optional deploy notifications`

### 8. Slack notifications como paso opcional

Problema:

- El job `notify` fallaba porque no existia `SLACK_WEBHOOK_URL`.
- Tambien usaba inputs no validos para `8398a7/action-slack@v3`:
  - `webhook_url`
  - `message`

Fix:

- Slack solo intenta notificar si existe:
  - `SLACK_WEBHOOK_URL` o `SLACK_WEBHOOK`
- Se cambio el webhook a variable de entorno `SLACK_WEBHOOK_URL`.
- Se cambio `message` por `text`.
- Si no hay webhook o no corrio deploy, el job lo salta sin romper el pipeline.

Commit:

- `3739997 fix: gate optional deploy notifications`

## Resultado

El pipeline quedo separado entre pasos obligatorios y pasos opcionales:

- Obligatorios:
  - build
  - tests
  - npm ci/build BFF
  - scan Trivy
  - docker build/push

- Opcionales segun configuracion:
  - SonarCloud
  - deploy staging
  - deploy production
  - Slack notifications

Con esto, el CI puede correr correctamente aunque el repositorio no tenga todavia configurados SonarCloud, Kubernetes o Slack.

## Variables y secrets opcionales

Para activar SonarCloud:

- Secret: `SONAR_TOKEN`
- Variable: `SONAR_PROJECT_KEY`
- Variable: `SONAR_ORGANIZATION`

Para activar deploy staging:

- Variable: `ENABLE_STAGING_DEPLOY=true`
- Secret: `KUBE_CONFIG_STAGING`

Para activar deploy production:

- Variable: `ENABLE_PRODUCTION_DEPLOY=true`
- Secret: `KUBE_CONFIG_PROD`

Para activar Slack:

- Secret: `SLACK_WEBHOOK_URL`

