#!/bin/bash

# Script para construir la imagen de Grafana

# Obtener el directorio del script y el directorio del proyecto
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "📈 Construyendo imagen de Grafana..."

podman build -t poc-grafana:latest "$PROJECT_DIR/grafana"

if [ $? -eq 0 ]; then
    echo "✅ Imagen poc-grafana construida exitosamente"
else
    echo "❌ Error al construir la imagen de Grafana"
    exit 1
fi
