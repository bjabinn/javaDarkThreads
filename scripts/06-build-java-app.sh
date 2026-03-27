#!/bin/bash

# Script para construir la imagen de la aplicación Java

echo "☕ Construyendo imagen de la aplicación Java (Platform Threads)..."

cd web || exit 1
docker build -t poc-java-platform-threads:latest .

if [ $? -eq 0 ]; then
    echo "✅ Imagen poc-java-platform-threads construida exitosamente"
else
    echo "❌ Error al construir la imagen de la aplicación Java"
    exit 1
fi

cd ..