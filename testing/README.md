# Testing Local - Sistema RedNorte

## 📋 **Resumen**

Este entorno de testing local permite probar el sistema completo de RedNorte incluyendo:
- API Gateway con Spring Cloud Gateway
- Prometheus para monitoreo de métricas
- Grafana para visualización
- Mock Services simulando los microservicios
- Redis para rate limiting
- Keycloak para autenticación

---

## 🚀 **Requisitos Previos**

### **Software Necesario:**
- Docker Desktop (Windows/Mac) o Docker + Docker Compose (Linux)
- Java 21+ JDK
- Maven 3.8+
- Git

### **Verificación:**
```bash
# Verificar Docker
docker --version
docker-compose --version

# Verificar Java
java -version

# Verificar Maven
mvn --version
```

---

## 🛠️ **Ejecución del Testing**

### **Opción 1: Script Automático (Recomendado)**

```bash
# Ejecutar todos los tests automáticamente
./testing/run-tests.sh
```

### **Opción 2: Manual Paso a Paso**

#### **1. Construir API Gateway**
```bash
cd ms-api-gateway
mvn clean package -DskipTests
cd ..
```

#### **2. Iniciar Servicios**
```bash
# Iniciar todos los servicios
docker-compose -f docker-compose-testing.yml up -d

# Verificar estado
docker-compose -f docker-compose-testing.yml ps
```

#### **3. Esperar a que los servicios estén listos**
```bash
# API Gateway
curl http://localhost:8080/actuator/health

# Prometheus
curl http://localhost:9090/-/healthy

# Grafana
curl http://localhost:3000/api/health
```

#### **4. Ejecutar Tests Manuales**

##### **Tests de API Gateway**
```bash
# Health Check
curl http://localhost:8080/actuator/health

# Lista de Espera (debería requerir autenticación)
curl http://localhost:8080/api/lista-espera/waitlist

# Rate Limiting (hacer 25 requests rápidos)
for i in {1..25}; do curl http://localhost:8080/actuator/health; done

# Circuit Breaker
# Detener mock service y probar
docker-compose -f docker-compose-testing.yml stop mock-lista-espera
curl http://localhost:8080/api/lista-espera/waitlist
# Reiniciar
docker-compose -f docker-compose-testing.yml start mock-lista-espera

# Fallback
curl http://localhost:8080/fallback/lista-espera
```

##### **Tests de Prometheus**
```bash
# Health Check
curl http://localhost:9090/-/healthy

# Targets
curl http://localhost:9090/api/v1/targets

# Metrics
curl http://localhost:9090/api/v1/query?query=up
```

##### **Tests de Grafana**
```bash
# Health Check
curl http://localhost:3000/api/health

# Datasources
curl -u admin:admin123 http://localhost:3000/api/datasources

# Dashboards
curl -u admin:admin123 http://localhost:3000/api/search
```

---

## 🔗 **URLs de Acceso**

### **Servicios Principales:**
- **API Gateway**: http://localhost:8080
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin123)

### **Mock Services:**
- **Mock Lista Espera**: http://localhost:8082
- **Mock Reasignación**: http://localhost:8083
- **Mock Notificaciones**: http://localhost:8084

### **Servicios de Apoyo:**
- **Redis**: localhost:6379
- **Keycloak**: http://localhost:8081 (admin/admin123)

---

## 📊 **Qué Probar**

### **1. API Gateway**
- ✅ **Health Check**: Verificar que el Gateway esté healthy
- ✅ **Autenticación**: Probar endpoints protegidos
- ✅ **Rate Limiting**: Verificar límites de requests
- ✅ **Circuit Breaker**: Probar fallback cuando servicios caen
- ✅ **Enrutamiento**: Verificar que las rutas funcionen

### **2. Prometheus**
- ✅ **Scraping**: Verificar que recolecte métricas
- ✅ **Targets**: Verificar que todos los servicios estén up
- ✅ **Queries**: Probar queries básicas

### **3. Grafana**
- ✅ **Conexión Prometheus**: Verificar datasource
- ✅ **Dashboards**: Verificar dashboards cargados
- ✅ **Visualización**: Probar gráficos

### **4. Mock Services**
- ✅ **Endpoints**: Verificar que respondan correctamente
- ✅ **CORS**: Verificar configuración CORS
- ✅ **Métricas**: Verificar que expongan métricas

---

## 🧪 **Casos de Test**

### **Test 1: Flujo Completo**
```bash
# 1. Obtener token de Keycloak (simulado)
# 2. Llamar a API Gateway con token
# 3. Gateway redirige a mock service
# 4. Verificar métricas en Prometheus
# 5. Verificar visualización en Grafana
```

### **Test 2: Rate Limiting**
```bash
# Hacer requests rápidos hasta alcanzar límite
# Verificar respuesta 429
# Verificar métricas de rate limiting
```

### **Test 3: Circuit Breaker**
```bash
# Detener mock service
# Hacer request al Gateway
# Verificar fallback response
# Verificar métricas de circuit breaker
```

### **Test 4: Monitoreo**
```bash
# Verificar que todos los servicios estén up en Prometheus
# Verificar dashboards en Grafana
# Probar queries complejas
```

---

## 📈 **Métricas a Verificar**

### **API Gateway:**
- `http_requests_total` - Requests totales
- `http_request_duration_seconds` - Latencia
- `resilience4j_circuitbreaker_state` - Estado Circuit Breaker
- `resilience4j_ratelimiter_calls` - Rate limiting

### **Mock Services:**
- `mockserver_request_total` - Requests mock
- `mockserver_response_total` - Responses mock
- `jvm_memory_used_bytes` - Memoria usada

### **Infraestructura:**
- `container_cpu_usage_seconds_total` - CPU por contenedor
- `container_memory_usage_bytes` - Memoria por contenedor

---

## 🐛 **Troubleshooting**

### **Problemas Comunes:**

#### **1. API Gateway no inicia**
```bash
# Verificar logs
docker-compose -f docker-compose-testing.yml logs api-gateway

# Verificar que el jar existe
ls -la ms-api-gateway/target/
```

#### **2. Prometheus no scrapea métricas**
```bash
# Verificar targets
curl http://localhost:9090/api/v1/targets

# Verificar configuración
docker exec prometheus cat /etc/prometheus/prometheus.yml
```

#### **3. Grafana no se conecta a Prometheus**
```bash
# Verificar datasource
curl -u admin:admin123 http://localhost:3000/api/datasources

# Verificar que Prometheus esté accesible
curl http://prometheus:9090/-/healthy
```

#### **4. Rate Limiting no funciona**
```bash
# Verificar Redis
docker-compose -f docker-compose-testing.yml logs redis

# Verificar configuración Gateway
curl http://localhost:8080/actuator/configprops
```

#### **5. Circuit Breaker no se activa**
```bash
# Verificar configuración de timeouts
curl http://localhost:8080/actuator/configprops

# Verificar logs de Gateway
docker-compose -f docker-compose-testing.yml logs api-gateway
```

---

## 🛠️ **Comandos Útiles**

### **Logs:**
```bash
# Todos los servicios
docker-compose -f docker-compose-testing.yml logs -f

# Servicio específico
docker-compose -f docker-compose-testing.yml logs -f api-gateway
docker-compose -f docker-compose-testing.yml logs -f prometheus
docker-compose -f docker-compose-testing.yml logs -f grafana
```

### **Estado:**
```bash
# Estado de todos los servicios
docker-compose -f docker-compose-testing.yml ps

# Estado detallado
docker-compose -f docker-compose-testing.yml ps -a
```

### **Reiniciar:**
```bash
# Reiniciar todo
docker-compose -f docker-compose-testing.yml restart

# Reiniciar servicio específico
docker-compose -f docker-compose-testing.yml restart api-gateway
```

### **Limpiar:**
```bash
# Detener y eliminar volúmenes
docker-compose -f docker-compose-testing.yml down -v

# Limpiar imágenes
docker system prune -a
```

---

## 📝 **Notas Adicionales**

### **Performance:**
- Los mock services son ligeros y rápidos
- Redis es persistente entre reinicios
- Prometheus retiene datos por 1 día en testing

### **Seguridad:**
- Keycloak está configurado con credenciales por defecto
- Solo para testing, no usar en producción
- Rate limiting configurado para testing (10 req/s)

### **Extensión:**
- Se pueden agregar más mock services
- Se pueden personalizar las respuestas
- Se pueden agregar más tests al script

---

## 🎯 **Próximos Pasos**

### **Después del Testing:**
1. **Analizar resultados** de los tests
2. **Ajustar configuración** si es necesario
3. **Documentar problemas** encontrados
4. **Preparar para producción** con servicios reales

### **Mejoras Futuras:**
- Agregar tests de carga
- Configurar alertas reales
- Agregar más dashboards
- Integrar con servicios reales

---

## 📞 **Soporte**

Si encuentras problemas durante el testing:

1. **Revisa los logs** del servicio afectado
2. **Verifica la configuración** en los archivos YAML
3. **Consulta el troubleshooting** arriba
4. **Reinicia los servicios** si es necesario

¡Happy Testing! 🚀
