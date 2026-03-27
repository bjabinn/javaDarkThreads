#!/bin/bash

# Script para ejecutar PostgreSQL

echo "🐘 Iniciando contenedor de PostgreSQL..."

# Verificar si el contenedor ya existe
if docker ps -a --format '{{.Names}}' | grep -q '^postgres$'; then
    echo "⚠️  El contenedor 'postgres' ya existe. Eliminándolo..."
    docker rm -f postgres
fi

# Ejecutar contenedor
docker run -d \
    --name postgres \
    --network app-network \
    -p 5432:5432 \
    --restart unless-stopped \
    poc-postgres:latest

if [ $? -eq 0 ]; then
    echo "✅ PostgreSQL iniciado exitosamente"
    echo "   - Puerto: 5432"
    echo "   - Usuario: postgres"
    echo "   - Contraseña: postgres"
    echo "   - Base de datos: testdb"
else
    echo "❌ Error al iniciar PostgreSQL"
    exit 1
fi