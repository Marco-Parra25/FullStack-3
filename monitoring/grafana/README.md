# Grafana - Visualización de Métricas RedNorte

## **Resumen**

Configuración completa de Grafana para visualizar todas las métricas recolectadas por Prometheus del sistema de salud RedNorte, incluyendo dashboards de negocio, sistema e infraestructura Kubernetes.

---

## **Arquitectura de Visualización**

```
Prometheus (9090)     Grafana (3000)
       |                   |
       | Métricas          | Dashboards
       v                   v
  Sistema RedNorte  ->  Visualización
```

---

## **Componentes Desplegados**

### **1. Grafana Server**
- **Versión**: v10.2.0
- **Replicas**: 2 (alta disponibilidad)
- **Storage**: 10Gi (para dashboards y configuración)
- **Resources**: 512Mi-1Gi memory, 250m-500m CPU cores

### **2. Configuración**
- **Datasource**: Prometheus (principal)
- **Plugins**: grafana-piechart-panel, grafana-worldmap-panel, grafana-clock-panel
- **Autenticación**: Basic Auth (admin/admin123)
- **Alerting**: Email y Slack configurados

### **3. Dashboards Disponibles**

#### **Dashboard 1: RedNorte Overview**
- **Request Rate** por servicio
- **Tasa de Errores HTTP 5xx**
- **Servicios Activos** (contenedor)
- **Total en Lista de Espera** (contenedor)
- **Latencia P95** por servicio
- **Uso de Memoria** por pod
- **Uso de CPU** por pod

#### **Dashboard 2: RedNorte Business**
- **Total en Lista de Espera** (contenedor)
- **Tiempo Promedio de Espera** (contenedor)
- **Tasa de Cancelaciones** (contenedor)
- **Circuit Breakers Abiertos** (contenedor)
- **Evolución Lista de Espera**
- **Tiempo Promedio de Espera** (timeline)
- **Tasa de Citas por Minuto**
- **Tasa de Cancelaciones** (timeline)

---

## **Despliegue**

### **1. Crear Secrets**
```bash
kubectl apply -f grafana-secrets.yaml
```

### **2. Configurar RBAC**
```bash
kubectl apply -f grafana-rbac.yaml
```

### **3. Desplegar Storage**
```bash
kubectl apply -f grafana-pvc.yaml
```

### **4. Desplegar Grafana**
```bash
kubectl apply -f grafana-config.yaml
kubectl apply -f grafana-provisioning.yaml
kubectl apply -f grafana-deployment.yaml
kubectl apply -f grafana-service.yaml
```

### **5. Verificar Despliegue**
```bash
# Verificar pods
kubectl get pods -n rednorte -l app=grafana

# Verificar services
kubectl get services -n rednorte -l app=grafana

# Verificar logs
kubectl logs -f deployment/grafana -n rednorte

# Acceder a Grafana
kubectl port-forward svc/grafana 3000:3000 -n rednorte
```

---

## **Acceso a Grafana**

### **Development**
```bash
kubectl port-forward svc/grafana 3000:3000 -n rednorte
# Acceder en: http://localhost:3000
# Usuario: admin / Contraseña: admin123
```

### **Production**
```bash
# Obtener LoadBalancer IP
kubectl get svc grafana-external -n rednorte
# Acceder en: http://<LOAD_BALANCER_IP>:3000
```

---

## **Configuración de Datasources**

### **Prometheus (Principal)**
```yaml
- name: Prometheus
  type: prometheus
  access: proxy
  url: http://prometheus:9090
  isDefault: true
  timeInterval: 15s
  queryTimeout: 60s
```

### **Grafana Internal (Métricas propias)**
```yaml
- name: Grafana
  type: prometheus
  access: proxy
  url: http://grafana:3000/api/prometheus/metrics
  timeInterval: 30s
```

### **Loki (Futuro - Logs)**
```yaml
- name: Loki
  type: loki
  access: proxy
  url: http://loki:3100
  maxLines: 1000
```

---

## **Alerting en Grafana**

### **Contact Points Configurados**

#### **Email - Crítico**
- **Destinatarios**: admin@rednorte.cl, devops@rednorte.cl
- **Asunto**: [CRITICAL] RedNorte - {{ .GroupLabels.alertname }}
- **Severidad**: critical

#### **Email - Advertencia**
- **Destinatarios**: devops@rednorte.cl
- **Asunto**: [WARNING] RedNorte - {{ .GroupLabels.alertname }}
- **Severidad**: warning

#### **Slack - Crítico**
- **Canal**: #rednorte-alerts
- **Webhook**: Configurar con Slack Incoming Webhook
- **Severidad**: critical

### **Notification Policies**

#### **Alertas Críticas**
- **Receiver**: email-critical
- **Group By**: alertname, service, component
- **Group Wait**: 30s
- **Group Interval**: 5m
- **Repeat Interval**: 15m

#### **Alertas de Advertencia**
- **Receiver**: email-warning
- **Group By**: alertname, service
- **Group Wait**: 1m
- **Group Interval**: 10m
- **Repeat Interval**: 30m

---

## **Mute Time Intervals**

### **Ventana de Mantenimiento**
- **Domingo**: 02:00 - 04:00
- **Propósito**: Mantenimiento programado

### **Horario No Laboral**
- **Lunes-Viernes**: 19:00 - 08:00
- **Sábado-Domingo**: 00:00 - 23:59
- **Propósito**: Reducir ruido durante horario no laboral

---

## **Queries Importantes**

### **Disponibilidad de Servicios**
```promql
up{job=~"api-gateway|ms-lista-espera|ms-reasignacion|ms-notificaciones|ms-monitoreo|bff-frontend"}
```

### **Request Rate por Servicio**
```promql
sum(rate(http_requests_total[5m])) by (job)
```

### **Tasa de Errores HTTP**
```promql
sum(rate(http_requests_total{status=~"5.."}[5m])) by (job) / sum(rate(http_requests_total[5m])) by (job) * 100
```

### **Latencia P95**
```promql
histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) by (job)
```

### **Uso de Recursos por Pod**
```promql
sum(container_memory_usage_bytes / container_spec_memory_limit_bytes * 100) by (pod)
sum(rate(container_cpu_usage_seconds_total[5m]) * 100) by (pod)
```

### **Métricas de Negocio**
```promql
rednorte_lista_espera_total
rednorte_tiempo_promedio_espera_horas
rate(rednorte_citas_canceladas_total[5m]) / rate(rednorte_citas_totales[5m]) * 100
```

---

## **Personalización de Dashboards**

### **Variables**
Los dashboards pueden incluir variables para filtrar:
- **Servicio**: job
- **Pod**: pod
- **Namespace**: namespace
- **Time Range**: time_range

### **Panel Types**
- **Timeseries**: Para métricas temporales
- **Stat**: Para valores actuales
- **Table**: Para datos tabulares
- **Pie Chart**: Para distribuciones
- **Worldmap**: Para geolocalización

### **Alertas en Dashboards**
Cada panel puede tener alertas configuradas:
- **Thresholds**: Límites de alerta
- **Conditions**: Condiciones de disparo
- **Notifications**: Canales de notificación

---

## **Optimización y Rendimiento**

### **1. Performance Tuning**
- **Refresh Rate**: 5s para dashboards en tiempo real
- **Query Timeout**: 60s para consultas complejas
- **Data Points**: Limitar a 1000 puntos por serie

### **2. Storage Optimization**
- **PVC Size**: 10Gi suficiente para dashboards y configuración
- **Backup**: Regular backups de datos de Grafana
- **Retention**: Configurar según políticas de retención

### **3. High Availability**
- **Replicas**: 2 para alta disponibilidad
- **Load Balancer**: Para distribución de carga
- **Health Checks**: Liveness y readiness probes

---

## **Troubleshooting**

### **Problemas Comunes**

#### **Dashboard no carga datos**
```bash
# Verificar conexión con Prometheus
kubectl exec -it deployment/grafana -n rednorte -- curl http://prometheus:9090/api/v1/query?query=up

# Verificar datasource configuration
kubectl get configmap grafana-provisioning -n rednorte -o yaml
```

#### **Alertas no se envían**
```bash
# Verificar configuración SMTP
kubectl logs deployment/grafana -n rednorte | grep -i smtp

# Verificar contact points
kubectl exec -it deployment/grafana -n rednorte -- curl -X GET http://localhost:3000/api/provisioning/contact-points
```

#### **Login fallido**
```bash
# Verificar secrets
kubectl get secret grafana-secrets -n rednorte -o yaml

# Resetear contraseña
kubectl exec -it deployment/grafana -n rednorte -- grafana-cli admin reset-admin-password
```

---

## **Próximos Pasos**

1. **Configurar Slack Webhook** para alertas en canal
2. **Crear dashboards adicionales** para equipos específicos
3. **Configurar alertas avanzadas** con Machine Learning
4. **Implementar autenticación LDAP** para usuarios
5. **Configurar snapshots** para compartir dashboards
6. **Implementar RBAC granular** por equipo

---

## **Soporte**

### **Logs de Grafana**
```bash
kubectl logs -f deployment/grafana -n rednorte
```

### **Configuración Actual**
```bash
kubectl exec -it deployment/grafana -n rednorte -- cat /etc/grafana/grafana.ini
```

### **Datasources Status**
```bash
kubectl exec -it deployment/grafana -n rednorte -- curl -X GET http://localhost:3000/api/datasources
```

### **Health Check**
```bash
kubectl exec -it deployment/grafana -n rednorte -- curl http://localhost:3000/api/health
```

---

## **Enlaces Útiles**

- **Grafana Documentation**: https://grafana.com/docs/
- **Prometheus Integration**: https://grafana.com/docs/grafana/latest/datasources/prometheus/
- **Alerting Guide**: https://grafana.com/docs/grafana/latest/alerting/
- **Dashboard Gallery**: https://grafana.com/grafana/dashboards/
