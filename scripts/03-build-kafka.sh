#!/bin/bash

# Script para construir la imagen de Kafka

# Obtener el directorio del script y el directorio del proyecto
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "📨 Construyendo imagen de Kafka..."

docker build -t poc-kafka:latest "$PROJECT_DIR/kafka"

if [ $? -eq 0 ]; then
    echo "✅ Imagen poc-kafka construida exitosamente"
else
    echo "❌ Error al construir la imagen de Kafka"
    exit 1
fi
