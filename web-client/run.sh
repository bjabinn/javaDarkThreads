#!/bin/bash

# Script para ejecutar el proyecto Spring Boot
# Uso: ./run.sh

echo "🚀 Iniciando aplicación Spring Boot..."
echo "================================================"
echo "Proyecto: Platform Threads PoC"
echo "Puerto: 8080"
echo "================================================"
echo ""
echo "📝 Una vez iniciado, accede a:"
echo "   - Aplicación: http://localhost:8080"
echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
echo "   - API Docs: http://localhost:8080/api-docs"
echo "   - Actuator: http://localhost:8080/actuator"
echo ""
echo "⏸️  Presiona Ctrl+C para detener la aplicación"
echo "================================================"
echo ""

# Ejecutar la aplicación con Maven
./mvnw spring-boot:run