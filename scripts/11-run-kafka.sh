#!/bin/bash

# Script para ejecutar Kafka

echo "📨 Iniciando contenedor de Kafka..."

# Verificar si el contenedor ya existe
if docker ps -a --format '{{.Names}}' | grep -q '^kafka$'; then
    echo "⚠️  El contenedor 'kafka' ya existe. Eliminándolo..."
    docker rm -f kafka
fi

# Ejecutar contenedor
docker run -d \
    --name kafka \
    --network app-network \
    -p 9092:9092 \
    -p 9093:9093 \
    --restart unless-stopped \
    poc-kafka:latest

if [ $? -eq 0 ]; then
    echo "✅ Kafka iniciado exitosamente (modo KRaft)"
    echo "   - Puerto broker: 9092"
    echo "   - Puerto controller: 9093"
    echo ""
    echo "   Esperando 10 segundos para que Kafka inicie completamente..."
    sleep 10
else
    echo "❌ Error al iniciar Kafka"
    exit 1
fi