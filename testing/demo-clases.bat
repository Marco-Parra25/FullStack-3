@echo off
REM Script de Demo para Clases - Sistema RedNorte (Windows)

echo 🚀 Iniciando Demo RedNorte...
echo ================================

REM Verificar Docker
echo [INFO] Verificando Docker...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker no está instalado
    echo Por favor, instala Docker Desktop desde: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker no está corriendo
    echo Por favor, inicia Docker Desktop
    pause
    exit /b 1
)

echo [SUCCESS] Docker está listo

REM Verificar Docker Compose
echo [INFO] Verificando Docker Compose...
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker Compose no está instalado
    pause
    exit /b 1
)

echo [SUCCESS] Docker Compose está listo

REM Limpiar servicios existentes
echo [INFO] Limpiando servicios existentes...
docker-compose -f docker-compose-simple-testing.yml down -v >nul 2>&1

REM Iniciar servicios
echo [INFO] Iniciando servicios...
docker-compose -f docker-compose-simple-testing.yml up -d

echo [SUCCESS] Servicios iniciados

REM Esperar a que los servicios estén listos
echo [INFO] Esperando a que los servicios estén listos...
timeout /t 30 /nobreak >nul

REM Verificar estado de servicios
echo [INFO] Verificando estado de servicios...
echo.
echo 📊 Estado de Contenedores:
docker-compose -f docker-compose-simple-testing.yml ps

echo.
echo 🔍 Health Checks:

REM Prometheus
curl -f http://localhost:9090/-/healthy >nul 2>&1
if %errorlevel% equ 0 (
    echo [SUCCESS] ✅ Prometheus: http://localhost:9090
) else (
    echo [WARNING] ⚠️ Prometheus: No responde
)

REM Grafana
curl -f http://localhost:3000/api/health >nul 2>&1
if %errorlevel% equ 0 (
    echo [SUCCESS] ✅ Grafana: http://localhost:3000 (admin/admin123)
) else (
    echo [WARNING] ⚠️ Grafana: No responde
)

REM Gateway
curl -f http://localhost:8085/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo [SUCCESS] ✅ Gateway: http://localhost:8085
) else (
    echo [WARNING] ⚠️ Gateway: No responde
)

echo.
echo ================================
echo 🎯 DEMO REDNORTE - LISTA
echo ================================
echo.
echo 🌐 URLs de Acceso:
echo   • Prometheus: http://localhost:9090
echo   • Grafana: http://localhost:3000 (admin/admin123)
echo   • Gateway: http://localhost:8085
echo   • Mock Services: http://localhost:8081,8082,8083
echo.
echo 📋 Pasos de Demo:
echo   1. Mostrar Prometheus UI con targets
echo   2. Mostrar Grafana con dashboards
echo   3. Probar Gateway health check
echo   4. Probar Gateway fallback
echo   5. Simular caída de servicio
echo   6. Mostrar recuperación
echo.
echo 🛠️ Comandos Útiles:
echo   • Ver logs: docker-compose -f docker-compose-simple-testing.yml logs -f [servicio]
echo   • Reiniciar: docker-compose -f docker-compose-simple-testing.yml restart [servicio]
echo   • Detener: docker-compose -f docker-compose-simple-testing.yml down
echo.
echo 🎉 ¡Demo lista para comenzar!
echo.

REM Preguntar si quiere abrir navegadores
set /p respuesta=¿Quieres abrir los navegadores automáticamente? (y/n): 
if /i "%respuesta%"=="y" (
    echo.
    echo 🌐 Abriendo navegadores...
    start http://localhost:9090
    start http://localhost:3000
    start http://localhost:8085
)

echo.
echo ✅ Demo lista para comenzar!
echo Presiona cualquier tecla para salir...
pause >nul
