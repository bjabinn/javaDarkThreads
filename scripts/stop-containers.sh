#!/bin/bash

# Script para parar y eliminar servicios
# Uso: ./stop-containers.sh [container1 container2 ...]
#   Sin argumentos: para todos los contenedores (prometheus grafana)
#   Con argumentos: para solo los contenedores indicados

ALL_CONTAINERS="prometheus grafana"

if [ $# -eq 0 ]; then
  TARGETS=$ALL_CONTAINERS
  echo "🛑 Parando todos los servicios..."
else
  TARGETS="$@"
  echo "🛑 Parando contenedores: $TARGETS"
fi

echo "================================================"
echo ""

echo "Parando contenedores..."
docker stop $TARGETS 2>/dev/null
docker rm $TARGETS 2>/dev/null

echo ""
echo "✅ Contenedores parados y eliminados: $TARGETS"
echo ""
echo "Para eliminar también las imágenes:"
echo "  docker rmi poc-prometheus poc-grafana"
echo ""
echo "Para eliminar también la red:"
echo "  docker network rm app-network"