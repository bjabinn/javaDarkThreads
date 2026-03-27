#!/bin/bash

# Script para ejecutar la aplicación Java

echo "☕ Iniciando contenedor de la aplicación Java (Platform Threads)..."

# Verificar si el contenedor ya existe
if docker ps -a --format '{{.Names}}' | grep -q '^java-platform-app$'; then
    echo "⚠️  El contenedor 'java-platform-app' ya existe. Eliminándolo..."
    docker rm -f java-platform-app
fi

# Ejecutar contenedor
docker run -d \
    --name java-platform-app \
    --network app-network \
    -p 8080:8080 \
    -e SPRING_PROFILES_ACTIVE=docker \
    --restart unless-stopped \
    poc-java-platform-threads:latest

if [ $? -eq 0 ]; then
    echo "✅ Aplicación Java iniciada exitosamente"
    echo "   - URL: http://localhost:8080"
    echo "   - Health: http://localhost:8080/api/health"
    echo "   - Info: http://localhost:8080/api/info"
    echo "   - Metrics: http://localhost:8080/actuator/prometheus"
    echo "   - Threads: http://localhost:8080/api/threads"
    echo ""
    echo "   Endpoint de carga: POST http://localhost:8080/api/load"
    echo ""
    echo "   Esperando 30 segundos para que la aplicación inicie..."
    sleep 30
    echo "   ✅ Aplicación lista!"
else
    echo "❌ Error al iniciar la aplicación Java"
    exit 1
fi