#!/bin/bash

# Script para ejecutar el web-server con Spring Boot
# Uso: ./run.sh

echo "🚀 Iniciando Web Server..."
echo "================================================"
echo "Proyecto: Web Server - Codemotion"
echo "Puerto: 8081"
echo "================================================"
echo ""
echo "📝 Una vez iniciado, accede a:"
echo "   - Aplicación: http://localhost:8081"
echo "   - Endpoint Greeting: http://localhost:8081/api/greeting"
echo "   - Swagger UI: http://localhost:8081/swagger-ui.html"
echo "   - API Docs: http://localhost:8081/api-docs"
echo "   - Health Check: http://localhost:8081/actuator/health"
echo "   - Prometheus Metrics: http://localhost:8081/actuator/prometheus"
echo "   - All Metrics: http://localhost:8081/actuator/metrics"
echo ""
echo "⏸️  Presiona Ctrl+C para detener la aplicación"
echo "================================================"
echo ""

# Ejecutar la aplicación con Maven
./mvnw spring-boot:run