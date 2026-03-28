#!/bin/bash

# Script para construir la imagen de Prometheus

echo "📊 Construyendo imagen de Prometheus..."

cd prometheus || exit 1
docker build -t poc-prometheus:latest .

if [ $? -eq 0 ]; then
    echo "✅ Imagen poc-prometheus construida exitosamente"
else
    echo "❌ Error al construir la imagen de Prometheus"
    exit 1
fi

cd ..