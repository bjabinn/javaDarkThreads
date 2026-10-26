#!/bin/bash

# Script para construir la imagen de Prometheus

# Obtener el directorio del script y el directorio del proyecto
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "📊 Construyendo imagen de Prometheus..."

podman build -t poc-prometheus:latest "$PROJECT_DIR/prometheus"

if [ $? -eq 0 ]; then
    echo "✅ Imagen poc-prometheus construida exitosamente"
else
    echo "❌ Error al construir la imagen de Prometheus"
    exit 1
fi
