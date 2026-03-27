#!/bin/bash

# Script para construir la imagen de Kafka

echo "📨 Construyendo imagen de Kafka..."

cd kafka || exit 1
docker build -t poc-kafka:latest .

if [ $? -eq 0 ]; then
    echo "✅ Imagen poc-kafka construida exitosamente"
else
    echo "❌ Error al construir la imagen de Kafka"
    exit 1
fi

cd ..