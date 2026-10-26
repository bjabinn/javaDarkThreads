#!/bin/bash

# Script para ejecutar Grafana

echo "📈 Iniciando contenedor de Grafana..."

# Verificar si el contenedor ya existe
if podman ps -a --format '{{.Names}}' | grep -q '^grafana$'; then
    echo "⚠️  El contenedor 'grafana' ya existe. Eliminándolo..."
    podman rm -f grafana
fi

# Crear volumen si no existe
podman volume create grafana-data 2>/dev/null || true

# Ejecutar contenedor con volumen persistente
podman run -d \
    --name grafana \
    --network app-network \
    -p 3000:3000 \
    -v grafana-data:/var/lib/grafana \
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