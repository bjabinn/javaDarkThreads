#!/bin/bash

# Script maestro para construir todas las imágenes Docker

# Obtener el directorio donde está este script
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "🚀 Construyendo todas las imágenes Docker..."
echo "================================================"
echo ""

# Ejecutar scripts de construcción
"$SCRIPT_DIR/01-create-network.sh"
echo ""

# "$SCRIPT_DIR/02-build-postgres.sh"
# echo ""

# "$SCRIPT_DIR/03-build-kafka.sh"
# echo ""

"$SCRIPT_DIR/04-build-prometheus.sh"
echo ""

"$SCRIPT_DIR/05-build-grafana.sh"
echo ""

echo "================================================"
echo "✅ ¡Todas las imágenes han sido construidas!"
echo ""
echo "Para iniciar los servicios ejecuta:"
echo "  ./scripts/start-containers.sh"
