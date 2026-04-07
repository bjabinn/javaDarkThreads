#!/bin/bash

# Script para ejecutar el proyecto Spring Boot
# Uso: ./run.sh

echo "🚀 Iniciando aplicación Spring Boot..."
echo "================================================"
echo "Proyecto: Platform Threads PoC"
echo "Puerto: 8090"
echo "================================================"
echo ""
echo "📝 Una vez iniciado, accede a:"
echo "   - Aplicación: http://localhost:8090"
echo "   - Swagger UI: http://localhost:8090/swagger-ui.html"
echo "   - API Docs: http://localhost:8090/api-docs"
echo "   - Actuator: http://localhost:8090/actuator"
echo ""
echo "⏸️  Presiona Ctrl+C para detener la aplicación"
echo "================================================"
echo ""

# Ejecutar la aplicación con Maven
# --enable-native-access=ALL-UNNAMED: Suprime warnings de acceso nativo en Java 21+
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="--enable-native-access=ALL-UNNAMED"
