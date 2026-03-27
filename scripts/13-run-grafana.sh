#!/bin/bash

# Script para ejecutar Grafana

echo "📈 Iniciando contenedor de Grafana..."

# Verificar si el contenedor ya existe
if docker ps -a --format '{{.Names}}' | grep -q '^grafana$'; then
    echo "⚠️  El contenedor 'grafana' ya existe. Eliminándolo..."
    docker rm -f grafana
fi

# Ejecutar contenedor
docker run -d \
    --name grafana \
    --network app-network \
    -p 3000:3000 \
    --restart unless-stopped \
    poc-grafana:latest

if [ $? -eq 0 ]; then
    echo "✅ Grafana iniciado exitosamente"
    echo "   - URL: http://localhost:3000"
    echo "   - Usuario: admin"
    echo "   - Contraseña: admin"
else
    echo "❌ Error al iniciar Grafana"
    exit 1
fi