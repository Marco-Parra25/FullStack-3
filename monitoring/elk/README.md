# 📊 ELK Stack - Logs Centralizados para RedNorte

## 📋 **Resumen**

ELK Stack (Elasticsearch + Logstash + Kibana) para centralizar, procesar y visualizar logs de todos los microservicios del sistema RedNorte.

---

## 🏗️ **Arquitectura del Sistema**

```
Microservicios → Filebeat → Logstash → Elasticsearch → Kibana
     ↓              ↓          ↓           ↓         ↓
   Logs         Recolector   Procesador   Base      Visualización
```

### **Componentes:**

1. **Elasticsearch** - Motor de búsqueda y almacenamiento
2. **Logstash** - Procesamiento y transformación de logs
3. **Kibana** - Visualización y análisis de logs
4. **Filebeat** - Recolector de logs ligero

---

## 🚀 **Configuración Kubernetes**

### **1. Elasticsearch**
```bash
# Aplicar configuración
kubectl apply -f monitoring/elasticsearch/elasticsearch-config.yaml
kubectl apply -f monitoring/elasticsearch/elasticsearch-rbac.yaml
kubectl apply -f monitoring/elasticsearch/elasticsearch-pvc.yaml
kubectl apply -f monitoring/elasticsearch/elasticsearch-deployment.yaml
kubectl apply -f monitoring/elasticsearch/elasticsearch-service.yaml
```

### **2. Logstash**
```bash
# Aplicar configuración
kubectl apply -f monitoring/logstash/logstash-config.yaml
kubectl apply -f monitoring/logstash/logstash-rbac.yaml
kubectl apply -f monitoring/logstash/logstash-deployment.yaml
kubectl apply -f monitoring/logstash/logstash-service.yaml
```

### **3. Kibana**
```bash
# Aplicar configuración
kubectl apply -f monitoring/kibana/kibana-config.yaml
kubectl apply -f monitoring/kibana/kibana-rbac.yaml
kubectl apply -f monitoring/kibana/kibana-deployment.yaml
kubectl apply -f monitoring/kibana/kibana-service.yaml
```

### **4. Filebeat**
```bash
# Aplicar configuración
kubectl apply -f monitoring/filebeat/filebeat-config.yaml
kubectl apply -f monitoring/filebeat/filebeat-rbac.yaml
kubectl apply -f monitoring/filebeat/filebeat-daemonset.yaml
```

---

## 🧪 **Testing Local**

### **Setup Rápido con Docker Compose**
```bash
# Iniciar ELK Stack
docker-compose -f docker-compose-elk-testing.yml up -d

# Verificar estado
docker-compose -f docker-compose-elk-testing.yml ps

# Ver logs
docker-compose -f docker-compose-elk-testing.yml logs -f
```

### **URLs de Acceso:**
- **Elasticsearch**: http://localhost:9200
- **Kibana**: http://localhost:5601
- **Logstash API**: http://localhost:9600

---

## 📊 **Configuración de Logs**

### **Formato de Logs Esperado**
```json
{
  "timestamp": "2024-04-24T17:10:00Z",
  "log_level": "INFO",
  "service_name": "ms-api-gateway",
  "logger_name": "cl.rednorte.gateway.controller",
  "log_message": "Request processed successfully",
  "business_domain": "gateway",
  "alert_level": "low"
}
```

### **Campos de Búsqueda:**
- `service_name` - Nombre del microservicio
- `log_level` - Nivel de log (DEBUG, INFO, WARN, ERROR)
- `business_domain` - Dominio de negocio (pacientes, citas, lista_espera)
- `alert_level` - Nivel de alerta (low, medium, high)

---

## 🔍 **Queries Útiles en Kibana**

### **1. Logs de Errores por Servicio**
```json
{
  "query": {
    "bool": {
      "must": [
        {"term": {"log_level": "ERROR"}}
      ]
    }
  },
  "aggs": {
    "services": {
      "terms": {
        "field": "service_name"
      }
    }
  }
}
```

### **2. Logs por Dominio de Negocio**
```json
{
  "query": {
    "bool": {
      "must": [
        {"exists": {"field": "business_domain"}}
      ]
    }
  },
  "aggs": {
    "domains": {
      "terms": {
        "field": "business_domain"
      }
    }
  }
}
```

### **3. Timeline de Logs**
```json
{
  "query": {
    "range": {
      "@timestamp": {
        "gte": "now-1h",
        "lte": "now"
      }
    }
  },
  "aggs": {
    "timeline": {
      "date_histogram": {
        "field": "@timestamp",
        "interval": "5m"
      }
    }
  }
}
```

---

## 🎯 **Dashboards en Kibana**

### **Dashboard Principal: RedNorte Logs**
- **Panel 1**: Logs totales por hora
- **Panel 2**: Logs por nivel (ERROR, WARN, INFO)
- **Panel 3**: Logs por servicio
- **Panel 4**: Logs por dominio de negocio
- **Panel 5**: Timeline de errores
- **Panel 6**: Top 10 errores más frecuentes

### **Dashboard de Negocio: RedNorte Business**
- **Panel 1**: Logs relacionados con pacientes
- **Panel 2**: Logs relacionados con citas
- **Panel 3**: Logs relacionados con lista de espera
- **Panel 4**: Tiempo de respuesta promedio
- **Panel 5**: Tasa de éxito/fracaso

---

## 🚨 **Alertas con Kibana**

### **Alerta 1: Errores Críticos**
```json
{
  "condition": {
    "query": {
      "bool": {
        "must": [
          {"term": {"log_level": "ERROR"}},
          {"term": {"alert_level": "high"}}
        ]
      }
    }
  },
  "threshold": 5,
  "time_window": "5m"
}
```

### **Alerta 2: Alta Tasa de Errores**
```json
{
  "condition": {
    "query": {
      "bool": {
        "must": [
          {"term": {"log_level": "ERROR"}}
        ]
      }
    }
  },
  "threshold": 50,
  "time_window": "1h"
}
```

---

## 🔧 **Configuración de Microservicios**

### **Spring Boot Logging**
```yaml
# application.yml
logging:
  level:
    cl.rednorte: INFO
    org.springframework: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/app.log
```

### **Logback Configuration**
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>/var/log/app.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

---

## 📈 **Métricas y Monitoreo**

### **Métricas de ELK Stack**
- **Elasticsearch**: Índices, documentos, CPU, memoria
- **Logstash**: Eventos procesados, pipeline performance
- **Kibana**: Usuarios activos, dashboards cargados
- **Filebeat**: Logs recolectados, eventos enviados

### **Integración con Prometheus**
```yaml
# Prometheus configuration
scrape_configs:
  - job_name: 'elasticsearch'
    static_configs:
      - targets: ['elasticsearch:9200']
    metrics_path: /_prometheus/metrics
    
  - job_name: 'logstash'
    static_configs:
      - targets: ['logstash:9600']
    metrics_path: /_node/stats
```

---

## 🛠️ **Troubleshooting**

### **Problema: Elasticsearch no inicia**
```bash
# Verificar memoria disponible
docker stats elasticsearch

# Aumentar límite de memoria virtual
sysctl -w vm.max_map_count=262144
```

### **Problema: Logstash no procesa logs**
```bash
# Verificar pipeline
curl -X GET "localhost:9600/_node/stats/pipelines"

# Verificar logs de Logstash
docker logs logstash
```

### **Problema: Kibana no se conecta a Elasticsearch**
```bash
# Verificar conexión
curl -X GET "localhost:9200/_cluster/health"

# Verificar configuración de Kibana
docker logs kibana
```

### **Problema: Filebeat no envía logs**
```bash
# Verificar configuración
docker exec filebeat filebeat test config

# Verificar conexión a Logstash
docker exec filebeat filebeat test output
```

---

## 📚 **Buenas Prácticas**

### **1. Estructura de Logs**
- Usar formato JSON estructurado
- Incluir timestamps consistentes
- Agregar metadata del servicio
- Usar niveles de log apropiados

### **2. Retención de Logs**
- Configurar ILM (Index Lifecycle Management)
- Definir políticas de retención
- Archivar logs antiguos
- Optimizar índices

### **3. Seguridad**
- Configurar autenticación en producción
- Encriptar datos sensibles
- Limitar acceso a Kibana
- Auditar accesos

### **4. Performance**
- Optimizar queries en Kibana
- Usar índices por fecha
- Configurar sharding apropiado
- Monitorear recursos

---

## 🔄 **Integración con Monitoreo Existente**

### **Prometheus + ELK**
- **Prometheus**: Métricas numéricas
- **ELK**: Logs y eventos
- **Grafana**: Visualización de métricas
- **Kibana**: Análisis de logs

### **Dashboard Combinado**
- Métricas de rendimiento (Prometheus)
- Logs de errores (ELK)
- Correlación de eventos
- Análisis de causa raíz

---

## 📞 **Soporte**

Para problemas con ELK Stack:

1. **Verificar logs** de cada componente
2. **Usar health checks** internos
3. **Consultar documentación** oficial
4. **Revisar configuración** de red

---

## 🎉 **Conclusión**

ELK Stack proporciona:
- ✅ **Centralización** de todos los logs
- ✅ **Búsqueda** avanzada y filtrado
- ✅ **Visualización** en tiempo real
- ✅ **Alertas** automáticas
- ✅ **Análisis** de negocio

**¡Sistema de logging completo para RedNorte!** 🚀
