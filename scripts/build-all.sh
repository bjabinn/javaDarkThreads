#!/bin/bash

# Script maestro para construir todas las imágenes Docker

echo "🚀 Construyendo todas las imágenes Docker..."
echo "================================================"
echo ""

# Ejecutar scripts de construcción
./scripts/01-create-network.sh
echo ""

./scripts/02-build-postgres.sh
echo ""

./scripts/03-build-kafka.sh
echo ""

./scripts/04-build-prometheus.sh
echo ""

./scripts/05-build-grafana.sh
echo ""

./scripts/06-build-java-app.sh
echo ""

echo "================================================"
echo "✅ ¡Todas las imágenes han sido construidas!"
echo ""
echo "Para iniciar los servicios ejecuta:"
echo "  ./scripts/start-all.sh"