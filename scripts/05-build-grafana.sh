#!/bin/bash

# Script para construir la imagen de Grafana

echo "📈 Construyendo imagen de Grafana..."

cd grafana || exit 1
docker build -t poc-grafana:latest .

if [ $? -eq 0 ]; then
    echo "✅ Imagen poc-grafana construida exitosamente"
else
    echo "❌ Error al construir la imagen de Grafana"
    exit 1
fi

cd ..