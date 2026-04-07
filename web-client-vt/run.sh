#!/bin/bash

# Script para ejecutar el proyecto Spring Boot
# Uso: ./run.sh

echo "🚀 Iniciando aplicación Spring Boot..."
echo "================================================"
echo "Proyecto: Virtual Threads PoC"
echo "Puerto: 8095"
echo "================================================"
echo ""
echo "📝 Una vez iniciado, accede a:"
echo "   - Aplicación: http://localhost:8095"
echo "   - Swagger UI: http://localhost:8095/swagger-ui.html"
echo "   - API Docs: http://localhost:8095/api-docs"
echo "   - Actuator: http://localhost:8095/actuator"
echo ""
echo "⏸️  Presiona Ctrl+C para detener la aplicación"
echo "================================================"
echo ""

# Ejecutar la aplicación con Maven
# --enable-native-access=ALL-UNNAMED: Suprime warnings de acceso nativo en Java 21+
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="--enable-native-access=ALL-UNNAMED"
