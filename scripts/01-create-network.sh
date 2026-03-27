#!/bin/bash

# Script para crear la red Docker necesaria para los servicios

echo "🌐 Creando red Docker 'app-network'..."

# Verificar si la red ya existe
if docker network inspect app-network >/dev/null 2>&1; then
    echo "✅ La red 'app-network' ya existe"
else
    docker network create app-network
    echo "✅ Red 'app-network' creada exitosamente"
fi