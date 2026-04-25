# Kubernetes Manifests - RedNorte Health System

## Estructura de Directorios

```
k8s/
├── namespace.yaml              # Namespace rednorte
├── configmaps/                 # Configuraciones de aplicaciones
│   └── api-gateway-config.yaml # Configuración API Gateway
├── secrets/                    # Secrets sensibles
│   └── api-gateway-secrets.yaml
├── deployments/                # Deployments de microservicios
│   └── api-gateway.yaml        # Deployment API Gateway
├── services/                   # Services para comunicación
│   └── api-gateway.yaml        # Services API Gateway
├── hpa/                        # Horizontal Pod Autoscalers
│   └── api-gateway-hpa.yaml    # HPA API Gateway
└── README.md                   # Este archivo
```

## Despliegue

### 1. Crear namespace
```bash
kubectl apply -f namespace.yaml
```

### 2. Desplegar API Gateway
```bash
# ConfigMaps y Secrets
kubectl apply -f configmaps/api-gateway-config.yaml
kubectl apply -f secrets/api-gateway-secrets.yaml

# Deployment
kubectl apply -f deployments/api-gateway.yaml

# Services
kubectl apply -f services/api-gateway.yaml

# HPA
kubectl apply -f hpa/api-gateway-hpa.yaml
```

### 3. Verificar despliegue
```bash
# Ver pods
kubectl get pods -n rednorte

# Ver services
kubectl get services -n rednorte

# Ver HPA
kubectl get hpa -n rednorte

# Ver logs
kubectl logs -f deployment/api-gateway -n rednorte
```

## Configuración

### Variables de Entorno
- **SPRING_PROFILES_ACTIVE**: perfil de Spring (kubernetes)
- **JAVA_OPTS**: opciones de JVM
- **LOG_LEVEL**: nivel de logging (INFO/DEBUG)

### Health Checks
- **Liveness Probe**: `/actuator/health` (60s delay, 30s period)
- **Readiness Probe**: `/actuator/health` (30s delay, 10s period)

### Recursos
- **Requests**: 512Mi memory, 250m CPU
- **Limits**: 1Gi memory, 500m CPU

### Escalado Automático
- **Min Replicas**: 2
- **Max Replicas**: 10
- **CPU Target**: 70%
- **Memory Target**: 80%

## Monitoreo

### Métricas Prometheus
- Endpoint: `/actuator/prometheus`
- Puerto: 8080

### Health Checks
- Endpoint: `/actuator/health`
- Puerto: 8080

## Seguridad

### Secrets
Los secrets deben ser configurados antes del despliegue:
```bash
# Generar secrets
kubectl create secret generic api-gateway-secrets \
  --from-literal=keycloak-client-secret=<CLIENT_SECRET> \
  --from-literal=redis-password=<REDIS_PASSWORD> \
  --from-literal=jwt-secret=<JWT_SECRET> \
  -n rednorte
```

### Network Policies
Para producción, configurar Network Policies para restringir tráfico entre pods.

## Consideraciones de Producción

1. **Resource Limits**: Ajustar según carga real
2. **Node Selectors**: Configurar para workloads específicos
3. **Tolerations**: Configurar para nodos dedicados
4. **Pod Disruption Budgets**: Configurar para alta disponibilidad
5. **Pod Security Policies**: Configurar para seguridad reforzada
