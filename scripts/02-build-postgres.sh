#!/bin/bash

# Script para construir la imagen de PostgreSQL

# Obtener el directorio del script y el directorio del proyecto
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "🐘 Construyendo imagen de PostgreSQL..."

docker build -t poc-postgres:latest "$PROJECT_DIR/postgres"

if [ $? -eq 0 ]; then
    echo "✅ Imagen poc-postgres construida exitosamente"
else
    echo "❌ Error al construir la imagen de PostgreSQL"
    exit 1
fi
