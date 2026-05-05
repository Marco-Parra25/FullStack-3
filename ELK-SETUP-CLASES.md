# 📊 ELK Stack Setup para Clases - RedNorte

## 📋 **Resumen**

Guía rápida para configurar y demostrar ELK Stack (Elasticsearch + Logstash + Kibana) para centralizar logs del sistema RedNorte.

---

## 🎯 **¿Qué vas a demostrar?**

- **Elasticsearch** - Motor de búsqueda y almacenamiento de logs
- **Logstash** - Procesamiento y transformación de logs
- **Kibana** - Visualización y análisis de logs en tiempo real
- **Filebeat** - Recolector automático de logs
- **Logs estructurados** de microservicios

---

## 🛠️ **Requisitos Previos**

### **Software Necesario:**
1. **Docker Desktop** o **Docker + Docker Compose**
2. **Git** para clonar el repositorio
3. **Navegador web** (Chrome, Firefox, Edge)
4. **4GB RAM mínimo** (ELK Stack consume recursos)

### **Verificación:**
```bash
# Verificar Docker
docker --version
docker-compose --version

# Verificar memoria disponible
docker system df
```

---

## 🚀 **Setup Rápido (10 minutos)**

### **Paso 1: Clonar Repositorio**
```bash
# Clonar el repositorio
git clone https://github.com/Marco-Parra25/FullStack-3.git
cd FullStack-3

# Cambiar a la rama correcta
git checkout feature/api-gateway-infraestructura
```

### **Paso 2: Iniciar ELK Stack**
```bash
# Iniciar ELK Stack
docker-compose -f docker-compose-elk-testing.yml up -d

# Esperar 2-3 minutos a que los servicios inicien
```

### **Paso 3: Verificar Funcionamiento**
```bash
# Verificar que todos los contenedores estén corriendo
docker-compose -f docker-compose-elk-testing.yml ps

# Verificar Elasticsearch
curl http://localhost:9200/_cluster/health

# Verificar Kibana
curl http://localhost:5601/api/status
```

---

## 🔗 **URLs de Acceso**

### **Servicios ELK:**
- **🟢 Elasticsearch**: http://localhost:9200
- **🟢 Kibana**: http://localhost:5601
- **🟢 Logstash API**: http://localhost:9600
- **🟢 Mock Gateway**: http://localhost:8086

---

## 📊 **Demostración Paso a Paso**

### **1. Demostrar Elasticsearch**
```bash
# Abrir en navegador
http://localhost:9200

# Mostrar:
# - Cluster health
# - Índices disponibles
# - Búsqueda básica
```

### **2. Demostrar Kibana**
```bash
# Abrir en navegador
http://localhost:5601

# Mostrar:
# - Stack Management
# - Index Patterns
# - Discover (logs en tiempo real)
# - Visualizaciones
```

### **3. Generar Logs de Prueba**
```bash
# Generar logs accediendo al mock gateway
curl http://localhost:8086/actuator/health
curl http://localhost:8086/actuator/prometheus
curl http://localhost:8086/fallback/lista-espera

# Generar errores
curl http://localhost:8086/api/lista-espera/nonexistent
```

---

## 🎯 **Demostración Práctica**

### **Escenario 1: Logs en Tiempo Real**
1. **Abrir Kibana** → Discover
2. **Generar logs** con curl al mock gateway
3. **Ver logs aparecer** en tiempo real
4. **Filtrar por servicio** y nivel de log

### **Escenario 2: Búsqueda y Filtrado**
1. **Buscar logs específicos** por mensaje
2. **Filtrar por nivel** (ERROR, WARN, INFO)
3. **Agrupar por servicio**
4. **Crear visualizaciones**

### **Escenario 3: Análisis de Negocio**
1. **Filtrar por dominio** (pacientes, citas, etc.)
2. **Ver timeline de eventos**
3. **Identificar patrones de errores**
4. **Correlacionar con métricas**

---

## 🛠️ **Comandos Útiles**

### **Verificar Estado:**
```bash
# Estado de todos los servicios
docker-compose -f docker-compose-elk-testing.yml ps

# Logs de un servicio específico
docker-compose -f docker-compose-elk-testing.yml logs -f elasticsearch
docker-compose -f docker-compose-elk-testing.yml logs -f kibana
docker-compose -f docker-compose-elk-testing.yml logs -f logstash
```

### **Generar Logs de Prueba:**
```bash
# Logs de información
curl http://localhost:8086/actuator/health

# Logs de errores
curl http://localhost:8086/nonexistent

# Logs de negocio
curl http://localhost:8086/api/lista-espera/waitlist
curl http://localhost:8086/fallback/lista-espera
```

### **Consultar Elasticsearch:**
```bash
# Ver índices
curl http://localhost:9200/_cat/indices

# Buscar logs
curl -X GET "localhost:9200/rednorte-logs-*/_search?pretty"

# Ver cluster health
curl http://localhost:9200/_cluster/health?pretty
```

### **Reiniciar Servicios:**
```bash
# Reiniciar todo
docker-compose -f docker-compose-elk-testing.yml restart

# Reiniciar servicio específico
docker-compose -f docker-compose-elk-testing.yml restart elasticsearch
```

---

## 🔧 **Configuración de Kibana**

### **Crear Index Pattern:**
1. **Ir a Stack Management** → **Index Patterns**
2. **Crear nuevo pattern**: `rednorte-logs-*`
3. **Seleccionar campo de tiempo**: `@timestamp`
4. **Guardar pattern**

### **Configurar Discover:**
1. **Ir a Discover**
2. **Seleccionar index pattern**: `rednorte-logs-*`
3. **Configurar filtros**:
   - `service_name: mock-gateway-elk`
   - `log_level: ERROR`
   - `@timestamp: Last 15 minutes`

### **Crear Visualización:**
1. **Ir a Visualize**
2. **Crear nueva visualización**
3. **Seleccionar tipo**: Bar chart
4. **Configurar**:
   - X-axis: `service_name.keyword`
   - Y-axis: Count
   - Filtro: `log_level: ERROR`

---

## 📱 **Demo en Clases - Script**

### **Script Automático (Opcional):**
```bash
# Crear archivo elk-demo-clases.sh
#!/bin/bash

echo "📊 Iniciando Demo ELK Stack..."

# 1. Iniciar servicios
echo "📦 Iniciando ELK Stack..."
docker-compose -f docker-compose-elk-testing.yml up -d

# 2. Esperar a que inicien
echo "⏳ Esperando a que los servicios inicien..."
sleep 180

# 3. Verificar estado
echo "🔍 Verificando servicios..."
docker-compose -f docker-compose-elk-testing.yml ps

# 4. Verificar health checks
echo "🏥 Verificar health checks..."
curl http://localhost:9200/_cluster/health
curl http://localhost:5601/api/status

# 5. Generar logs de prueba
echo "📝 Generando logs de prueba..."
curl http://localhost:8086/actuator/health
curl http://localhost:8086/fallback/lista-espera
curl http://localhost:8086/nonexistent

# 6. Mostrar URLs
echo "🌐 URLs de acceso:"
echo "Elasticsearch: http://localhost:9200"
echo "Kibana: http://localhost:5601"
echo "Mock Gateway: http://localhost:8086"

echo "✅ ELK Demo lista para comenzar!"
```

---

## 🔍 **Queries de Ejemplo para Kibana**

### **Query 1: Todos los Logs del Gateway**
```json
{
  "query": {
    "match": {
      "service_name": "mock-gateway-elk"
    }
  }
}
```

### **Query 2: Solo Logs de Error**
```json
{
  "query": {
    "match": {
      "log_level": "ERROR"
    }
  }
}
```

### **Query 3: Logs por Rango de Tiempo**
```json
{
  "query": {
    "range": {
      "@timestamp": {
        "gte": "now-1h",
        "lte": "now"
      }
    }
  }
}
```

---

## 📈 **Visualizaciones Sugeridas**

### **Dashboard: RedNorte Logs Overview**
- **Panel 1**: Logs totales por hora
- **Panel 2**: Logs por nivel (INFO, WARN, ERROR)
- **Panel 3**: Logs por servicio
- **Panel 4**: Timeline de errores
- **Panel 5**: Top 10 mensajes de error

### **Dashboard: Gateway Performance**
- **Panel 1**: Requests por minuto
- **Panel 2**: Response time distribution
- **Panel 3**: Error rate
- **Panel 4**: Status codes
- **Panel 5**: User agents

---

## 🚨 **Troubleshooting**

### **Problema: Elasticsearch no inicia**
```bash
# Verificar memoria disponible
docker stats

# Aumentar límite de memoria virtual (Linux/Mac)
sudo sysctl -w vm.max_map_count=262144

# Reiniciar contenedor
docker-compose -f docker-compose-elk-testing.yml restart elasticsearch
```

### **Problema: Kibana no se conecta**
```bash
# Verificar que Elasticsearch esté listo
curl http://localhost:9200/_cluster/health

# Reiniciar Kibana
docker-compose -f docker-compose-elk-testing.yml restart kibana

# Verificar logs
docker-compose -f docker-compose-elk-testing.yml logs kibana
```

### **Problema: No aparecen logs**
```bash
# Verificar que Filebeat esté corriendo
docker-compose -f docker-compose-elk-testing.yml ps filebeat

# Verificar configuración de Logstash
curl http://localhost:9600/_node/stats/pipelines

# Generar logs de prueba
curl http://localhost:8086/actuator/health
```

---

## 📚 **Explicaciones para Clases**

### **¿Por qué ELK Stack?**
- "Centraliza logs de todos los microservicios"
- "Permite búsqueda avanzada y filtrado"
- "Facilita debugging y análisis de problemas"
- "Integra con nuestro monitoreo existente"

### **¿Por qué Logs Estructurados?**
- "Facilitan búsqueda y análisis"
- "Permiten correlación de eventos"
- "Habilitan visualizaciones automáticas"
- "Mejoran performance de búsqueda"

### **¿Por qué Filebeat?**
- "Recolector ligero y eficiente"
- "Se ejecuta en cada nodo del cluster"
- "Filtra y enriquece logs automáticamente"
- "Integra perfectamente con Logstash"

---

## 🎯 **Puntos Clave para Demostrar**

### **Arquitectura:**
- **Centralización** - Todos los logs en un lugar
- **Procesamiento** - Transformación y enriquecimiento
- **Visualización** - Análisis en tiempo real
- **Escalabilidad** - Manejo de grandes volúmenes

### **Características Técnicas:**
- **Búsqueda全文** - Búsqueda en todo el contenido
- **Filtros avanzados** - Por campo, tiempo, nivel
- **Visualizaciones** - Gráficos y dashboards
- **Alertas** - Notificaciones automáticas

### **Integración:**
- **Docker** - Contenerización
- **Kubernetes** - Orquestación
- **Prometheus** - Métricas complementarias
- **Grafana** - Visualización unificada

---

## 🎉 **Conclusión de Demo**

### **Logros Demostrados:**
1. ✅ **Centralización de logs** funcionando
2. ✅ **Búsqueda y filtrado** en tiempo real
3. ✅ **Visualización** de logs en Kibana
4. ✅ **Procesamiento** con Logstash
5. ✅ **Recolección** automática con Filebeat

### **Próximos Pasos:**
- Integrar con microservicios reales
- Configurar alertas automáticas
- Crear dashboards de negocio
- Optimizar performance

---

## 📞 **Soporte Durante Demo**

Si algo falla durante la demo:

1. **No te preocupes** - Es parte del aprendizaje
2. **Verifica logs** - `docker-compose logs`
3. **Reinicia servicios** - `docker-compose restart`
4. **Simplifica** - Inicia solo Elasticsearch + Kibana

---

## 🚀 **¡Listo para Demostrar!**

Con esta guía puedes:
- ✅ **Setup en 10 minutos**
- ✅ **Demostrar sistema completo de logging**
- ✅ **Mostrar búsqueda y análisis en tiempo real**
- ✅ **Explicar arquitectura moderna de logs**

**¡Éxito en tu demo de ELK Stack!** 🎉
