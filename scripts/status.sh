#!/bin/bash

# Script para ver el estado de todos los servicios

echo "📊 Estado de los servicios del PoC"
echo "================================================"
echo ""

# Verificar contenedores
echo "🐳 Contenedores:"
podman ps -a --filter "name=prometheus" --filter "name=grafana" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "================================================"
echo ""

# Verificar red
if podman network inspect app-network >/dev/null 2>&1; then
    echo "🌐 Red 'app-network': ✅ Existe"
else
    echo "🌐 Red 'app-network': ❌ No existe"
fi

echo ""

# Verificar imágenes
echo "🖼️  Imágenes construidas:"
podman images --filter "reference=poc-*" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"