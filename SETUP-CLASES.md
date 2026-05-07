# 🚀 Guía de Setup para Clases - Sistema RedNorte

## 📋 **Resumen**

Esta guía te permite ejecutar el sistema completo de monitoreo RedNorte en cualquier computadora para demostrar en clases.

---

## 🎯 **¿Qué vas a demostrar?**

- **API Gateway** con Spring Cloud Gateway
- **Prometheus** para monitoreo de métricas
- **Grafana** para visualización de dashboards
- **Mock Services** simulando microservicios
- **Rate limiting** y **Circuit Breakers**

---

## 🛠️ **Requisitos Previos**

### **Software Necesario:**
1. **Docker Desktop** (Windows/Mac) o **Docker + Docker Compose** (Linux)
2. **Git** para clonar el repositorio
3. **Navegador web** (Chrome, Firefox, Edge)

### **Verificación:**
```bash
# Verificar Docker
docker --version
docker-compose --version

# Verificar Git
git --version
```

---

## 🚀 **Setup Rápido (5 minutos)**

### **Paso 1: Clonar Repositorio**
```bash
# Clonar el repositorio
git clone https://github.com/Marco-Parra25/FullStack-3.git
cd FullStack-3

# Cambiar a la rama correcta
git checkout feature/api-gateway-infraestructura
```

### **Paso 2: Iniciar Sistema**
```bash
# Iniciar todo el sistema
docker-compose -f docker-compose-simple-testing.yml up -d

# Esperar 30 segundos a que los servicios inicien
```

### **Paso 3: Verificar Funcionamiento**
```bash
# Verificar que todos los contenedores estén corriendo
docker-compose -f docker-compose-simple-testing.yml ps
```

---

## 🔗 **URLs de Acceso**

### **Servicios Principales:**
- **🟢 Prometheus**: http://localhost:9090
- **🟢 Grafana**: http://localhost:3000
  - **Usuario:** admin
  - **Contraseña:** admin123
- **🟢 Mock Gateway**: http://localhost:8085

### **Mock Services:**
- **🟡 Mock Lista Espera**: http://localhost:8081
- **🟡 Mock Reasignación**: http://localhost:8082
- **🟡 Mock Notificaciones**: http://localhost:8083

---

## 📊 **Demostración Paso a Paso**

### **1. Demostrar Prometheus**
```bash
# Abrir en navegador
http://localhost:9090

# Mostrar:
# - Targets status
# - Queries básicas
# - Métricas recolectadas
```

### **2. Demostrar Grafana**
```bash
# Abrir en navegador
http://localhost:3000
# Usuario: admin
# Contraseña: admin123

# Mostrar:
# - Datasource configurado (Prometheus)
# - Dashboards disponibles
# - Visualización de métricas
```

### **3. Demostrar API Gateway**
```bash
# Health Check
curl http://localhost:8085/actuator/health

# Fallback (cuando servicios caen)
curl http://localhost:8085/fallback/lista-espera
```

---

## 🎯 **Demostración Práctica**

### **Escenario 1: Sistema Funcionando**
1. **Mostrar Prometheus UI** con todos los targets up
2. **Mostrar Grafana** con dashboards cargados
3. **Mostrar Gateway** respondiendo correctamente

### **Escenario 2: Simular Caída de Servicio**
```bash
# Detener un mock service
docker-compose -f docker-compose-simple-testing.yml stop mock-lista-espera

# Probar Gateway (debería mostrar fallback)
curl http://localhost:8085/fallback/lista-espera

# Mostrar en Prometheus las métricas de error
```

### **Escenario 3: Recuperación**
```bash
# Reiniciar el servicio
docker-compose -f docker-compose-simple-testing.yml start mock-lista-espera

# Mostrar que todo vuelve a funcionar
```

---

## 🛠️ **Comandos Útiles**

### **Verificar Estado:**
```bash
# Estado de todos los servicios
docker-compose -f docker-compose-simple-testing.yml ps

# Logs de un servicio específico
docker-compose -f docker-compose-simple-testing.yml logs prometheus
docker-compose -f docker-compose-simple-testing.yml logs grafana
```

### **Reiniciar Servicios:**
```bash
# Reiniciar todo
docker-compose -f docker-compose-simple-testing.yml restart

# Reiniciar servicio específico
docker-compose -f docker-compose-simple-testing.yml restart prometheus
```

### **Detener Todo:**
```bash
# Detener y eliminar contenedores
docker-compose -f docker-compose-simple-testing.yml down

# Limpiar completamente (opcional)
docker-compose -f docker-compose-simple-testing.yml down -v
```

---

## 🔧 **Troubleshooting**

### **Problema: Puerto 8080 en uso**
```bash
# El Gateway usa puerto 8085 para evitar conflictos
# Si tienes otros servicios en 8085, cambiar en docker-compose-simple-testing.yml
```

### **Problema: Docker no inicia**
```bash
# Asegurar que Docker Desktop esté corriendo
# Reiniciar Docker Desktop
```

### **Problema: Servicios no responden**
```bash
# Esperar más tiempo (puede tardar 1-2 minutos la primera vez)
# Verificar logs para ver errores
docker-compose -f docker-compose-simple-testing.yml logs [servicio]
```

### **Problema: Grafana no carga datos**
```bash
# Verificar que Prometheus esté accesible desde Grafana
# Revisar datasource configuration en Grafana
```

---

## 📱 **Demo en Clases - Script**

### **Script Automático (Opcional):**
```bash
# Crear archivo demo-clases.sh
#!/bin/bash

echo "🚀 Iniciando Demo RedNorte..."

# 1. Iniciar servicios
echo "📦 Iniciando servicios..."
docker-compose -f docker-compose-simple-testing.yml up -d

# 2. Esperar a que inicien
echo "⏳ Esperando a que los servicios inicien..."
sleep 30

# 3. Verificar estado
echo "🔍 Verificando servicios..."
docker-compose -f docker-compose-simple-testing.yml ps

# 4. Mostrar URLs
echo "🌐 URLs de acceso:"
echo "Prometheus: http://localhost:9090"
echo "Grafana: http://localhost:3000 (admin/admin123)"
echo "Gateway: http://localhost:8085"

echo "✅ Demo lista para comenzar!"
```

---

## 🎯 **Puntos Clave para Demostrar**

### **Arquitectura:**
- **Gateway Central** - Enruta y protege
- **Monitoreo** - Prometheus recolecta métricas
- **Visualización** - Grafana muestra dashboards
- **Resiliencia** - Fallbacks cuando servicios caen

### **Características Técnicas:**
- **Rate Limiting** - Control de requests
- **Circuit Breaker** - Aislamiento de fallos
- **Health Checks** - Monitoreo de salud
- **Métricas** - Observabilidad completa

### **Integración:**
- **Docker** - Contenerización
- **Docker Compose** - Orquestación
- **Mock Services** - Testing independiente
- **Configuración Externalizada** - Flexibilidad

---

## 📚 **Explicaciones para Clases**

### **¿Por qué Mock Services?**
- "Permiten testing independiente"
- "No dependemos de otros equipos"
- "Simulan comportamiento real"

### **¿Por qué Prometheus + Grafana?**
- "Prometheus recolecta métricas automáticamente"
- "Grafana visualiza de forma intuitiva"
- "Permiten monitoreo en tiempo real"

### **¿Por qué API Gateway?**
- "Punto central de entrada"
- "Implementa seguridad y rate limiting"
- "Aísla los microservicios"

---

## 🎉 **Conclusión de Demo**

### **Logros Demostrados:**
1. ✅ **Sistema completo funcionando**
2. ✅ **Monitoreo en tiempo real**
3. ✅ **Resiliencia ante fallos**
4. ✅ **Arquitectura de microservicios**
5. ✅ **Infraestructura moderna (Docker + Monitoring)**

### **Próximos Pasos:**
- Integrar con microservicios reales
- Configurar alertas avanzadas
- Agregar más dashboards
- Implementar CI/CD

---

## 📞 **Soporte Durante Demo**

Si algo falla durante la demo:

1. **No te preocupes** - Es parte del aprendizaje
2. **Verifica logs** - `docker-compose logs`
3. **Reinicia servicios** - `docker-compose restart`
4. **Simplifica** - Inicia solo Prometheus + Grafana

---

## 🚀 **¡Listo para Demostrar!**

Con esta guía puedes:
- ✅ **Setup en 5 minutos**
- ✅ **Demostrar sistema completo**
- ✅ **Mostrar arquitectura moderna**
- ✅ **Explicar conceptos clave**

**¡Éxito en tu demo!** 🎉
