# Integración BFF Frontend con API Gateway

## 📋 **Resumen**

Se ha adaptado el API Gateway para soportar el BFF Frontend desarrollado por la compañera, manteniendo la compatibilidad con el flujo existente sin causar interrupciones.

---

## 🏗️ **Arquitectura de Integración**

### **Flujo 1: Directo (Existente - Sin cambios)**
```
Frontend React → BFF (3001) → MS Lista Espera (8081) [DIRECTO]
```

### **Flujo 2: A través de Gateway (Nuevo)**
```
Frontend React → API Gateway (8080) → BFF (3001) → MS Lista Espera (8081)
```

---

## 🛠️ **Configuraciones Agregadas**

### **1. Rutas en API Gateway**

#### **Portal de Pacientes**
```yaml
- id: bff-portal
  uri: http://bff-frontend:3001
  predicates:
    - Path=/api/bff/portal/**
  filters:
    - StripPrefix=3
    - name: RequestRateLimiter
      args:
        redis-rate-limiter.replenishRate: 50
        redis-rate-limiter.burstCapacity: 100
```

#### **Panel Administrativo**
```yaml
- id: bff-admin
  uri: http://bff-frontend:3001
  predicates:
    - Path=/api/bff/admin/**
  filters:
    - StripPrefix=3
    - name: RequestRateLimiter
      args:
        redis-rate-limiter.replenishRate: 30
        redis-rate-limiter.burstCapacity: 60
```

#### **Health Check**
```yaml
- id: bff-health
  uri: http://bff-frontend:3001
  predicates:
    - Path=/api/bff/health
  filters:
    - StripPrefix=3
```

### **2. Circuit Breakers para BFF**

```yaml
bffPortalCircuitBreaker:
  registerHealthIndicator: true
  slidingWindowSize: 10
  minimumNumberOfCalls: 5
  permittedNumberOfCallsInHalfOpenState: 3
  automaticTransitionFromOpenToHalfOpenEnabled: true
  waitDurationInOpenState: 5s
  failureRateThreshold: 60

bffAdminCircuitBreaker:
  registerHealthIndicator: true
  slidingWindowSize: 10
  minimumNumberOfCalls: 5
  permittedNumberOfCallsInHalfOpenState: 3
  automaticTransitionFromOpenToHalfOpenEnabled: true
  waitDurationInOpenState: 5s
  failureRateThreshold: 60
```

### **3. CORS Actualizado**

```yaml
allowedOriginPatterns: 
  - "http://localhost:*"
  - "http://localhost:3000"    # Frontend development
  - "http://localhost:3001"    # BFF development
  - "https://rednorte.cl"
  - "https://*.rednorte.cl"
  - "https://bff.rednorte.cl"   # BFF production
```

---

## 📦 **Manifiestos Kubernetes Creados**

### **Deployment del BFF**
- **Archivo**: `k8s/deployments/bff-frontend.yaml`
- **Replicas**: 2 (escalable hasta 8)
- **Puerto**: 3001
- **Health Checks**: `/health`
- **Resources**: 256Mi-512Mi memory, 100m-250m CPU

### **Service del BFF**
- **Archivo**: `k8s/services/bff-frontend.yaml`
- **Tipo**: ClusterIP (interno)
- **Puerto**: 3001

### **HPA del BFF**
- **Archivo**: `k8s/hpa/bff-frontend-hpa.yaml`
- **Min Replicas**: 2
- **Max Replicas**: 8
- **Métricas**: CPU 70%, Memory 80%

---

## 🔄 **Endpoints Disponibles**

### **A través del API Gateway**

#### **Portal de Pacientes**
```bash
GET /api/bff/portal/estado/{id}
GET /api/bff/portal/especialidad/{especialidad}
GET /api/bff/portal/rut/{rut}
```

#### **Panel Administrativo**
```bash
GET /api/bff/admin/lista
POST /api/bff/admin/registrar
PATCH /api/bff/admin/cancelar/{id}
GET /api/bff/admin/pacientes
POST /api/bff/admin/pacientes
```

#### **Health Check**
```bash
GET /api/bff/health
```

### **Fallbacks**
```bash
GET /fallback/bff-portal    # Si el BFF portal falla
GET /fallback/bff-admin     # Si el BFF admin falla
```

---

## 🚀 **Despliegue**

### **1. Desplegar BFF**
```bash
kubectl apply -f k8s/deployments/bff-frontend.yaml
kubectl apply -f k8s/services/bff-frontend.yaml
kubectl apply -f k8s/hpa/bff-frontend-hpa.yaml
```

### **2. Actualizar API Gateway**
```bash
kubectl apply -f k8s/configmaps/api-gateway-config.yaml
kubectl rollout restart deployment/api-gateway -n rednorte
```

### **3. Verificar Integración**
```bash
# Verificar BFF
kubectl get pods -n rednorte -l app=bff-frontend

# Verificar Gateway
kubectl get pods -n rednorte -l app=ms-api-gateway

# Probar endpoints
kubectl port-forward svc/api-gateway 8080:8080 -n rednorte
curl http://localhost:8080/api/bff/health
```

---

## ⚠️ **Consideraciones Importantes**

### **1. Compatibilidad Mantenida**
- El BFF sigue conectándose directamente al MS Lista Espera
- No se rompe el flujo existente
- El Gateway es una capa adicional, no un reemplazo

### **2. Rate Limiting Diferenciado**
- **Portal**: 50 req/s (alto tráfico de pacientes)
- **Admin**: 30 req/s (tráfico administrativo)
- **Health**: Sin límite (monitoreo)

### **3. Escalado Independiente**
- BFF escala según su propia carga
- Gateway escala según su propia carga
- MS Lista Espera escala según su propia carga

---

## 🔮 **Próximos Pasos**

### **Coordinación con Compañera**
1. **Compartir esta documentación** con ella
2. **Explicarle el nuevo endpoint** del Gateway
3. **Actualizar su frontend** para usar `/api/bff/*` en lugar de `/portal/*` y `/admin/*`

### **Mejoras Futuras**
1. **Implementar JWT** en el BFF
2. **Migrar conexión** del BFF al Gateway
3. **Unificar autenticación** en todo el sistema

---

## 📞 **Soporte**

Para cualquier duda sobre la integración:
- Revisar los logs del Gateway: `kubectl logs -f deployment/api-gateway -n rednorte`
- Revisar los logs del BFF: `kubectl logs -f deployment/bff-frontend -n rednorte`
- Verificar health checks: `kubectl get pods -n rednorte -w`
