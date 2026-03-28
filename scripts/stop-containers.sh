#!/bin/bash

# Script para parar y eliminar todos los servicios

echo "🛑 Parando todos los servicios..."
echo "================================================"
echo ""

# Parar y eliminar contenedores
echo "Parando contenedores..."
docker stop prometheus grafana postgres kafka 2>/dev/null
docker rm prometheus grafana postgres kafka 2>/dev/null

echo ""
echo "✅ Todos los contenedores han sido parados y eliminados"
echo ""
echo "Para eliminar también las imágenes:"
echo "  docker rmi poc-prometheus poc-grafana poc-postgres poc-kafka"
echo ""
echo "Para eliminar también la red:"
echo "  docker network rm app-network"