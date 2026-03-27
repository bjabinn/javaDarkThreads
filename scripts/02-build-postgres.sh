#!/bin/bash

# Script para construir la imagen de PostgreSQL

echo "🐘 Construyendo imagen de PostgreSQL..."

cd postgres || exit 1
docker build -t poc-postgres:latest .

if [ $? -eq 0 ]; then
    echo "✅ Imagen poc-postgres construida exitosamente"
else
    echo "❌ Error al construir la imagen de PostgreSQL"
    exit 1
fi

cd ..