#!/bin/bash

# Script de Demo para Clases - Sistema RedNorte

echo "🚀 Iniciando Demo RedNorte..."
echo "================================"

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

# Verificar Docker
check_docker() {
    log_info "Verificando Docker..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker no está instalado"
        echo "Por favor, instala Docker Desktop desde: https://www.docker.com/products/docker-desktop"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        log_error "Docker no está corriendo"
        echo "Por favor, inicia Docker Desktop"
        exit 1
    fi
    
    log_success "Docker está listo"
}

# Verificar Docker Compose
check_docker_compose() {
    log_info "Verificando Docker Compose..."
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose no está instalado"
        exit 1
    fi
    
    log_success "Docker Compose está listo"
}

# Detener servicios existentes
cleanup() {
    log_info "Limpiando servicios existentes..."
    docker-compose -f docker-compose-simple-testing.yml down -v 2>/dev/null
}

# Iniciar servicios
start_services() {
    log_info "Iniciando servicios..."
    
    # Iniciar servicios en background
    docker-compose -f docker-compose-simple-testing.yml up -d
    
    log_success "Servicios iniciados"
}

# Esperar a que los servicios estén listos
wait_for_services() {
    log_info "Esperando a que los servicios estén listos..."
    
    # Esperar Prometheus
    log_info "Esperando Prometheus..."
    for i in {1..30}; do
        if curl -f http://localhost:9090/-/healthy &> /dev/null; then
            log_success "Prometheus está listo"
            break
        fi
        if [ $i -eq 30 ]; then
            log_error "Prometheus no está listo después de 5 minutos"
            return 1
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
            return 1
        fi
        sleep 10
    done
    
    # Esperar Gateway
    log_info "Esperando Gateway..."
    for i in {1..30}; do
        if curl -f http://localhost:8085/actuator/health &> /dev/null; then
            log_success "Gateway está listo"
            break
        fi
        if [ $i -eq 30 ]; then
            log_error "Gateway no está listo después de 5 minutos"
            return 1
        fi
        sleep 10
    done
    
    return 0
}

# Verificar estado de servicios
verify_services() {
    log_info "Verificando estado de servicios..."
    
    echo ""
    echo "📊 Estado de Contenedores:"
    docker-compose -f docker-compose-simple-testing.yml ps
    
    echo ""
    echo "🔍 Health Checks:"
    
    # Prometheus
    if curl -f http://localhost:9090/-/healthy &> /dev/null; then
        log_success "✅ Prometheus: http://localhost:9090"
    else
        log_warning "⚠️ Prometheus: No responde"
    fi
    
    # Grafana
    if curl -f http://localhost:3000/api/health &> /dev/null; then
        log_success "✅ Grafana: http://localhost:3000 (admin/admin123)"
    else
        log_warning "⚠️ Grafana: No responde"
    fi
    
    # Gateway
    if curl -f http://localhost:8085/actuator/health &> /dev/null; then
        log_success "✅ Gateway: http://localhost:8085"
    else
        log_warning "⚠️ Gateway: No responde"
    fi
}

# Mostrar información de demo
show_demo_info() {
    echo ""
    echo "================================"
    echo "🎯 DEMO REDNORTE - LISTA"
    echo "================================"
    echo ""
    echo "🌐 URLs de Acceso:"
    echo "  • Prometheus: http://localhost:9090"
    echo "  • Grafana: http://localhost:3000 (admin/admin123)"
    echo "  • Gateway: http://localhost:8085"
    echo "  • Mock Services: http://localhost:8081,8082,8083"
    echo ""
    echo "📋 Pasos de Demo:"
    echo "  1. Mostrar Prometheus UI con targets"
    echo "  2. Mostrar Grafana con dashboards"
    echo "  3. Probar Gateway health check"
    echo "  4. Probar Gateway fallback"
    echo "  5. Simular caída de servicio"
    echo "  6. Mostrar recuperación"
    echo ""
    echo "🛠️ Comandos Útiles:"
    echo "  • Ver logs: docker-compose -f docker-compose-simple-testing.yml logs -f [servicio]"
    echo "  • Reiniciar: docker-compose -f docker-compose-simple-testing.yml restart [servicio]"
    echo "  • Detener: docker-compose -f docker-compose-simple-testing.yml down"
    echo ""
    echo "🎉 ¡Demo lista para comenzar!"
    echo ""
}

# Demo interactiva
interactive_demo() {
    echo ""
    echo "🎮 ¿Quieres ejecutar una demo interactiva? (y/n)"
    read -r response
    
    if [[ "$response" =~ ^[Yy]$ ]]; then
        echo ""
        echo "🚀 Iniciando demo interactiva..."
        
        echo ""
        echo "📊 1. Abriendo Prometheus en navegador..."
        if command -v xdg-open &> /dev/null; then
            xdg-open http://localhost:9090
        elif command -v open &> /dev/null; then
            open http://localhost:9090
        elif command -v start &> /dev/null; then
            start http://localhost:9090
        fi
        
        echo "📈 2. Abriendo Grafana en navegador..."
        if command -v xdg-open &> /dev/null; then
            xdg-open http://localhost:3000
        elif command -v open &> /dev/null; then
            open http://localhost:3000
        elif command -v start &> /dev/null; then
            start http://localhost:3000
        fi
        
        echo ""
        echo "🧪 3. Probando Gateway..."
        echo "Health Check:"
        curl -s http://localhost:8085/actuator/health | jq . 2>/dev/null || curl -s http://localhost:8085/actuator/health
        
        echo ""
        echo "Fallback:"
        curl -s http://localhost:8085/fallback/lista-espera | jq . 2>/dev/null || curl -s http://localhost:8085/fallback/lista-espera
        
        echo ""
        echo "⚠️ 4. Simulando caída de servicio..."
        docker-compose -f docker-compose-simple-testing.yml stop mock-lista-espera
        sleep 5
        
        echo "Probando fallback con servicio caído:"
        curl -s http://localhost:8085/fallback/lista-espera | jq . 2>/dev/null || curl -s http://localhost:8085/fallback/lista-espera
        
        echo ""
        echo "🔄 5. Recuperando servicio..."
        docker-compose -f docker-compose-simple-testing.yml start mock-lista-espera
        sleep 5
        
        echo "Servicio recuperado:"
        docker-compose -f docker-compose-simple-testing.yml ps mock-lista-espera
        
        echo ""
        echo "✅ Demo interactiva completada!"
    fi
}

# Main execution
main() {
    echo ""
    echo "🎓 DEMO REDNORTE - SETUP AUTOMÁTICO"
    echo "=================================="
    echo ""
    
    # Verificar requisitos
    check_docker
    check_docker_compose
    
    # Limpiar servicios existentes
    cleanup
    
    # Iniciar servicios
    start_services
    
    # Esperar a que estén listos
    if wait_for_services; then
        # Verificar estado
        verify_services
        
        # Mostrar información de demo
        show_demo_info
        
        # Ofrecer demo interactiva
        interactive_demo
        
        log_success "🎉 Demo lista para comenzar!"
    else
        log_error "❌ Hubo problemas al iniciar los servicios"
        echo ""
        echo "Verifica los logs:"
        echo "docker-compose -f docker-compose-simple-testing.yml logs"
        exit 1
    fi
}

# Ejecutar main
main
