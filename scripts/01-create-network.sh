#!/bin/bash

# Script para crear la red Podman necesaria para los servicios

echo "🌐 Creando red Podman 'app-network'..."

# Verificar si la red ya existe
if podman network inspect app-network >/dev/null 2>&1; then
    echo "✅ La red 'app-network' ya existe"
else
    podman network create app-network
    echo "✅ Red 'app-network' creada exitosamente"
fi