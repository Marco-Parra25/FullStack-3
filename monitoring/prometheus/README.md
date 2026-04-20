# Prometheus - Sistema de Monitoreo RedNorte

## 📋 **Resumen**

Configuración completa de Prometheus para monitorear todos los microservicios del sistema de salud RedNorte, incluyendo métricas de aplicación, infraestructura Kubernetes y alertas de negocio.

---

## 🏗️ **Arquitectura de Monitoreo**

```
┌─────────────────────────────────────────────────────────────┐
│                    Prometheus Cluster                   │
│                  (rednorte namespace)                  │
│                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │
│  │ API Gateway │  │ MS Lista    │  │ MS Reasig │ │
│  │ (9090)     │  │ (8081)      │  │ (8082)    │ │
│  └─────────────┘  └─────────────┘  └───────────┘ │
│                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │
│  │ MS Notif    │  │ MS Monitor   │  │ BFF        │ │
│  │ (8083)      │  │ (8084)      │  │ (3001)     │ │
│  └─────────────┘  └─────────────┘  └───────────┘ │
│                                                     │
│  ┌─────────────┐  ┌─────────────┐              │
│  │ K8s Nodes   │  │ K8s Pods    │              │
│  │             │  │             │              │
│  └─────────────┘  └─────────────┘              │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 **Componentes Desplegados**

### **1. Prometheus Server**
- **Versión**: v2.47.0
- **Replicas**: 1
- **Storage**: 50Gi (15 días de retención)
- **Resources**: 2-4Gi memory, 1-2 CPU cores

### **2. Configuración**
- **Scrape Interval**: 15s
- **Evaluation Interval**: 15s
- **Retention**: 15 días
- **Compression**: WAL compression habilitada

### **3. Jobs de Scrape**

#### **Microservicios**
```yaml
# API Gateway
- job_name: 'api-gateway'
  metrics_path: /actuator/prometheus
  scrape_interval: 15s

# MS Lista de Espera
- job_name: 'ms-lista-espera'
  metrics_path: /actuator/prometheus
  scrape_interval: 15s

# MS Reasignación
- job_name: 'ms-reasignacion'
  metrics_path: /actuator/prometheus
  scrape_interval: 15s

# MS Notificaciones
- job_name: 'ms-notificaciones'
  metrics_path: /actuator/prometheus
  scrape_interval: 15s

# MS Monitoreo
- job_name: 'ms-monitoreo'
  metrics_path: /actuator/prometheus
  scrape_interval: 15s

# BFF Frontend
- job_name: 'bff-frontend'
  metrics_path: /actuator/prometheus
  scrape_interval: 30s
```

#### **Infraestructura Kubernetes**
```yaml
# Nodes
- job_name: 'kubernetes-nodes'
  scheme: https
  bearer_token_file: /var/run/secrets/kubernetes.io/serviceaccount/token

# Pods
- job_name: 'kubernetes-pods'
  namespaces: [rednorte]

# Services
- job_name: 'kubernetes-services'
  namespaces: [rednorte]
```

---

## 🚨 **Sistema de Alertas**

### **Grupos de Alertas**

#### **1. System Alerts**
- **ServicioDown**: Servicios caídos
- **AltaCPU**: Uso de CPU > 80%
- **AltaMemoria**: Uso de memoria > 85%
- **DiscoLleno**: Espacio en disco < 10%
- **ReiniciosFrecuentes**: Más de 2 reinicios en 15 min
- **CircuitBreakerAbierto**: Circuit Breaker en estado OPEN

#### **2. Business Alerts**
- **ListaEsperaCritica**: > 50,000 pacientes en espera
- **TiempoEsperaElevado**: > 12 horas promedio
- **TasaCancelacionesAlta**: > 10% de cancelaciones

#### **3. Performance Alerts**
- **AltaLatenciaHTTP**: P95 > 2s
- **TasaErroresHTTPAlta**: > 5% de errores 5xx
- **ThroughputBajo**: < 1 req/s

#### **4. Kubernetes Alerts**
- **PodsNoReady**: Pods no en estado ready
- **HPAMaximo**: HPA en máximo de réplicas
- **PodsPending**: Pods en estado pending

---

## 🚀 **Despliegue**

### **1. Crear Namespace**
```bash
kubectl apply -f ../k8s/namespace.yaml
```

### **2. Configurar RBAC**
```bash
kubectl apply -f prometheus-rbac.yaml
```

### **3. Desplegar Storage**
```bash
kubectl apply -f prometheus-pvc.yaml
```

### **4. Desplegar Prometheus**
```bash
kubectl apply -f prometheus-config.yaml
kubectl apply -f prometheus-rules.yaml
kubectl apply -f prometheus-deployment.yaml
kubectl apply -f prometheus-service.yaml
```

### **5. Verificar Despliegue**
```bash
# Verificar pods
kubectl get pods -n rednorte -l app=prometheus

# Verificar services
kubectl get services -n rednorte -l app=prometheus

# Verificar configuración
kubectl logs -f deployment/prometheus -n rednorte

# Acceder a Prometheus
kubectl port-forward svc/prometheus 9090:9090 -n rednorte
```

---

## 📊 **Métricas Disponibles**

### **Microservicios**
- **HTTP Requests**: Total, por método, por status
- **Response Time**: Histogramas de latencia
- **Circuit Breaker**: Estado, llamadas, fallos
- **Rate Limiting**: Requests por segundo, rechazos
- **JVM Metrics**: Heap, GC, threads
- **Custom Metrics**: Métricas de negocio

### **Kubernetes**
- **Pod Metrics**: CPU, memoria, restarts
- **Node Metrics**: CPU, memoria, disco, red
- **HPA Metrics**: Réplicas actuales vs deseadas
- **Service Metrics**: Endpoints disponibles

---

## 🔧 **Configuración Avanzada**

### **1. Remote Write (para almacenamiento externo)**
```yaml
remote_write:
  - url: "https://your-remote-storage/api/v1/write"
    basic_auth:
      username: admin
      password: password
    queue_config:
      max_samples_per_send: 1000
      max_shards: 200
      capacity: 2500
```

### **2. Federation (para múltiples clusters)**
```yaml
federate:
  - job_name: 'federate'
    match: '{__name__=~".+"}'
    static_configs:
      - targets: ['prometheus-remote:9090']
```

### **3. Recording Rules**
```yaml
recording_rules:
  - name: rednorte-aggregates
    interval: 30s
    rules:
      - record: rednorte:http_requests_total
        expr: sum(http_requests_total) by (job, instance)
      
      - record: rednorte:http_request_rate
        expr: rate(http_requests_total[5m]) by (job, instance)
```

---

## 📱 **Acceso a Prometheus**

### **Development**
```bash
kubectl port-forward svc/prometheus 9090:9090 -n rednorte
# Acceder en: http://localhost:9090
```

### **Production**
```bash
# Obtener LoadBalancer IP
kubectl get svc prometheus-external -n rednorte
# Acceder en: http://<LOAD_BALANCER_IP>:9090
```

---

## 🔍 **Queries Útiles**

### **Disponibilidad de Servicios**
```promql
up{job=~"api-gateway|ms-lista-espera|ms-reasignacion|ms-notificaciones"}
```

### **Top 10 Endpoints Más Lentos**
```promql
topk(10, histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) by (job, uri))
```

### **Tasa de Errores HTTP**
```promql
sum(rate(http_requests_total{status=~"5.."}[5m])) by (job) / sum(rate(http_requests_total[5m])) by (job)
```

### **Uso de Recursos por Pod**
```promql
sum(container_memory_usage_bytes) by (pod) / sum(container_spec_memory_limit_bytes) by (pod) * 100
```

### **Estado de Circuit Breakers**
```promql
resilience4j_circuitbreaker_state
```

---

## 🚨 **Gestión de Alertas**

### **Ver Reglas Activas**
```bash
curl http://localhost:9090/api/v1/rules
```

### **Ver Alertas Activas**
```bash
curl http://localhost:9090/api/v1/alerts
```

### **Silenciar Alertas**
```bash
# Via UI: http://localhost:9090/alerts
# O API:
curl -X POST http://localhost:9090/api/v1/silences \
  -d '{
    "matchers": [{"name":"alertname","value":"AltaCPU"}],
    "startsAt":"2024-01-01T00:00:00Z",
    "endsAt":"2024-01-01T01:00:00Z",
    "createdBy":"admin",
    "comment":"Mantenimiento programado"
  }'
```

---

## 📈 **Optimización y Rendimiento**

### **1. Storage Optimization**
- **WAL Compression**: Reduce uso de disco 40-60%
- **Retention Policy**: 15 días balancea costo/historia
- **Remote Storage**: Para datos históricos largos

### **2. Performance Tuning**
- **Scrape Interval**: 15s (balanceo precisión/carga)
- **Evaluation Interval**: 15s (respuestas rápidas de alertas)
- **Resource Limits**: 2-4Gi memory, 1-2 CPU cores

### **3. High Availability**
- **Replicas**: Considerar 2 replicas para producción
- **Storage**: Usar storage clase replicada
- **Backup**: Regular backups de datos de Prometheus

---

## 🔮 **Próximos Pasos**

1. **Configurar AlertManager** para notificaciones
2. **Integrar con Grafana** para visualización
3. **Configurar Remote Write** para almacenamiento a largo plazo
4. **Implementar Recording Rules** para métricas agregadas
5. **Configurar Federation** para multi-cluster

---

## 📞 **Soporte**

### **Logs de Prometheus**
```bash
kubectl logs -f deployment/prometheus -n rednorte
```

### **Configuración Actual**
```bash
kubectl exec -it deployment/prometheus -n rednorte -- cat /etc/prometheus/prometheus.yml
```

### **Targets Status**
```bash
curl http://localhost:9090/api/v1/targets
```

### **Health Check**
```bash
curl http://localhost:9090/-/healthy
```
