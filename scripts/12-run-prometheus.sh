#!/bin/bash

# Script para ejecutar Prometheus

echo "📊 Iniciando contenedor de Prometheus..."

# Verificar si el contenedor ya existe
if podman ps -a --format '{{.Names}}' | grep -q '^prometheus$'; then
    echo "⚠️  El contenedor 'prometheus' ya existe. Eliminándolo..."
    podman rm -f prometheus
fi

# Ejecutar contenedor
podman run -d \
    --name prometheus \
    --network app-network \
    -p 9090:9090 \
    --restart unless-stopped \
    poc-prometheus:latest

if [ $? -eq 0 ]; then
    echo "✅ Prometheus iniciado exitosamente"
    echo "   - URL: http://localhost:9090"
    echo "   - Targets: http://localhost:9090/targets"
else
    echo "❌ Error al iniciar Prometheus"
    exit 1
fi