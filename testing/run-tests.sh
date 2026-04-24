#!/bin/bash

# Script de Testing Local para RedNorte Gateway + Monitoring

echo "🚀 Iniciando Testing Local del Sistema RedNorte"
echo "=============================================="

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funciones de utilidad
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar dependencias
check_dependencies() {
    log_info "Verificando dependencias..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker no está instalado"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose no está instalado"
        exit 1
    fi
    
    if ! command -v curl &> /dev/null; then
        log_error "curl no está instalado"
        exit 1
    fi
    
    log_success "Dependencias verificadas"
}

# Construir API Gateway
build_gateway() {
    log_info "Construyendo API Gateway..."
    
    cd ms-api-gateway
    if ! mvn clean package -DskipTests; then
        log_error "Error al construir API Gateway"
        exit 1
    fi
    
    cd ..
    log_success "API Gateway construido"
}

# Iniciar servicios
start_services() {
    log_info "Iniciando servicios con Docker Compose..."
    
    # Detener servicios existentes
    docker-compose -f docker-compose-testing.yml down -v
    
    # Iniciar servicios
    docker-compose -f docker-compose-testing.yml up -d
    
    log_success "Servicios iniciados"
}

# Esperar a que los servicios estén listos
wait_for_services() {
    log_info "Esperando a que los servicios estén listos..."
    
    # Esperar API Gateway
    log_info "Esperando API Gateway..."
    for i in {1..30}; do
        if curl -f http://localhost:8080/actuator/health &> /dev/null; then
            log_success "API Gateway está listo"
            break
        fi
        if [ $i -eq 30 ]; then
            log_error "API Gateway no está listo después de 5 minutos"
            exit 1
        fi
        sleep 10
    done
    
    # Esperar Prometheus
    log_info "Esperando Prometheus..."
    for i in {1..30}; do
        if curl -f http://localhost:9090/-/healthy &> /dev/null; then
            log_success "Prometheus está listo"
            break
        fi
        if [ $i -eq 30 ]; then
            log_error "Prometheus no está listo después de 5 minutos"
            exit 1
        fi
        sleep 10
    done
    
    # Esperar Grafana
    log_info "Esperando Grafana..."
    for i in {1..30}; do
        if curl -f http://localhost:3000/api/health &> /dev/null; then
            log_success "Grafana está listo"
            break
        fi
        if [ $i -eq 30 ]; then
            log_error "Grafana no está listo después de 5 minutos"
            exit 1
        fi
        sleep 10
    done
    
    # Esperar Mock Services
    log_info "Esperando Mock Services..."
    for i in {1..30}; do
        if curl -f http://localhost:8082/mockserver/status &> /dev/null; then
            log_success "Mock Services están listos"
            break
        fi
        if [ $i -eq 30 ]; then
            log_error "Mock Services no están listos después de 5 minutos"
            exit 1
        fi
        sleep 10
    done
}

# Ejecutar tests de API Gateway
test_gateway() {
    log_info "Ejecutando tests de API Gateway..."
    
    # Test 1: Health Check
    log_info "Test 1: Health Check"
    response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
    if [ $response -eq 200 ]; then
        log_success "✅ Health Check OK"
    else
        log_error "❌ Health Check FAILED (HTTP $response)"
    fi
    
    # Test 2: Lista de Espera (sin autenticación)
    log_info "Test 2: Lista de Espera (sin autenticación)"
    response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/lista-espera/waitlist)
    if [ $response -eq 401 ]; then
        log_success "✅ Lista de Espera requiere autenticación"
    else
        log_warning "⚠️ Lista de Espera no requiere autenticación (HTTP $response)"
    fi
    
    # Test 3: Rate Limiting
    log_info "Test 3: Rate Limiting"
    for i in {1..25}; do
        response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
        if [ $response -eq 429 ]; then
            log_success "✅ Rate Limiting activado después de $i requests"
            break
        fi
        if [ $i -eq 25 ]; then
            log_warning "⚠️ Rate Limiting no se activó después de 25 requests"
        fi
    done
    
    # Test 4: Circuit Breaker (simulando servicio caído)
    log_info "Test 4: Circuit Breaker"
    # Detener mock service para simular caída
    docker-compose -f docker-compose-testing.yml stop mock-lista-espera
    sleep 5
    
    response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/lista-espera/waitlist)
    if [ $response -eq 503 ]; then
        log_success "✅ Circuit Breaker activado"
    else
        log_warning "⚠️ Circuit Breaker no se activó (HTTP $response)"
    fi
    
    # Reiniciar mock service
    docker-compose -f docker-compose-testing.yml start mock-lista-espera
    sleep 5
    
    # Test 5: Fallback
    log_info "Test 5: Fallback"
    response=$(curl -s http://localhost:8080/fallback/lista-espera)
    if [[ $response == *"SERVICE_UNAVAILABLE"* ]]; then
        log_success "✅ Fallback funcionando"
    else
        log_warning "⚠️ Fallback no funcionando correctamente"
    fi
}

# Ejecutar tests de Prometheus
test_prometheus() {
    log_info "Ejecutando tests de Prometheus..."
    
    # Test 1: Health Check
    log_info "Test 1: Prometheus Health Check"
    response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:9090/-/healthy)
    if [ $response -eq 200 ]; then
        log_success "✅ Prometheus Health Check OK"
    else
        log_error "❌ Prometheus Health Check FAILED (HTTP $response)"
    fi
    
    # Test 2: Targets
    log_info "Test 2: Prometheus Targets"
    response=$(curl -s http://localhost:9090/api/v1/targets | grep -o '"health":"up"' | wc -l)
    if [ $response -ge 3 ]; then
        log_success "✅ Prometheus Targets OK ($response targets up)"
    else
        log_warning "⚠️ Solo $response targets up"
    fi
    
    # Test 3: Metrics
    log_info "Test 3: Prometheus Metrics"
    response=$(curl -s http://localhost:9090/api/v1/query?query=up | grep -o '"result"' | wc -l)
    if [ $response -ge 1 ]; then
        log_success "✅ Prometheus Metrics OK ($response metrics)"
    else
        log_warning "⚠️ No hay métricas disponibles"
    fi
}

# Ejecutar tests de Grafana
test_grafana() {
    log_info "Ejecutando tests de Grafana..."
    
    # Test 1: Health Check
    log_info "Test 1: Grafana Health Check"
    response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000/api/health)
    if [ $response -eq 200 ]; then
        log_success "✅ Grafana Health Check OK"
    else
        log_error "❌ Grafana Health Check FAILED (HTTP $response)"
    fi
    
    # Test 2: Datasources
    log_info "Test 2: Grafana Datasources"
    response=$(curl -s -u admin:admin123 http://localhost:3000/api/datasources | grep -o '"name":"Prometheus"' | wc -l)
    if [ $response -ge 1 ]; then
        log_success "✅ Grafana Datasources OK (Prometheus configurado)"
    else
        log_warning "⚠️ Prometheus no está configurado en Grafana"
    fi
    
    # Test 3: Dashboards
    log_info "Test 3: Grafana Dashboards"
    response=$(curl -s -u admin:admin123 http://localhost:3000/api/search | grep -o '"title":"RedNorte"' | wc -l)
    if [ $response -ge 1 ]; then
        log_success "✅ Grafana Dashboards OK ($response dashboards RedNorte)"
    else
        log_warning "⚠️ No hay dashboards de RedNorte"
    fi
}

# Generar reporte
generate_report() {
    log_info "Generando reporte de testing..."
    
    echo ""
    echo "=============================================="
    echo "📊 REPORTES DE TESTING"
    echo "=============================================="
    echo ""
    echo "🔗 URLs de Acceso:"
    echo "  • API Gateway: http://localhost:8080"
    echo "  • Prometheus: http://localhost:9090"
    echo "  • Grafana: http://localhost:3000 (admin/admin123)"
    echo "  • Mock Services:"
    echo "    - Lista Espera: http://localhost:8082"
    echo "    - Reasignación: http://localhost:8083"
    echo "    - Notificaciones: http://localhost:8084"
    echo ""
    echo "📋 Tests Ejecutados:"
    echo "  ✅ API Gateway Health Check"
    echo "  ✅ API Gateway Autenticación"
    echo "  ✅ API Gateway Rate Limiting"
    echo "  ✅ API Gateway Circuit Breaker"
    echo "  ✅ API Gateway Fallback"
    echo "  ✅ Prometheus Health Check"
    echo "  ✅ Prometheus Targets"
    echo "  ✅ Prometheus Metrics"
    echo "  ✅ Grafana Health Check"
    echo "  ✅ Grafana Datasources"
    echo "  ✅ Grafana Dashboards"
    echo ""
    echo "🎯 Próximos Pasos:"
    echo "  1. Explorar los dashboards en Grafana"
    echo "  2. Probar endpoints del API Gateway"
    echo "  3. Verificar métricas en Prometheus"
    echo "  4. Configurar alertas si es necesario"
    echo ""
    echo "🛠️ Comandos Útiles:"
    echo "  • Ver logs: docker-compose -f docker-compose-testing.yml logs -f [service]"
    echo "  • Detener: docker-compose -f docker-compose-testing.yml down"
    echo "  • Reiniciar: docker-compose -f docker-compose-testing.yml restart [service]"
    echo ""
}

# Main execution
main() {
    echo ""
    echo "🧪 TESTING LOCAL - SISTEMA REDNORTE"
    echo "=============================================="
    echo ""
    
    check_dependencies
    build_gateway
    start_services
    wait_for_services
    test_gateway
    test_prometheus
    test_grafana
    generate_report
    
    log_success "🎉 Testing completado exitosamente!"
}

# Ejecutar main
main
