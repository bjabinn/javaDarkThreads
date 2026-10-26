#!/bin/bash

# Script maestro para iniciar todos los servicios

# Obtener el directorio donde está este script
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "🚀 Iniciando todos los servicios..."
echo "================================================"
echo ""

# Crear red si no existe
"$SCRIPT_DIR/01-create-network.sh"
echo ""

# Iniciar servicios de infraestructura primero
"$SCRIPT_DIR/12-run-prometheus.sh"
echo ""

"$SCRIPT_DIR/13-run-grafana.sh"
echo ""

echo "================================================"
echo "✅ ¡Todos los servicios están corriendo!"
echo ""
echo "📋 Servicios disponibles:"
echo "  - Prometheus:  http://localhost:9090"
echo "  - Grafana:     http://localhost:3000 (admin)"
echo ""
echo "Para ver el estado de los contenedores:"
echo "  docker ps"
echo ""
echo "Para ver logs de un servicio:"
echo "  docker logs -f java-platform-app"