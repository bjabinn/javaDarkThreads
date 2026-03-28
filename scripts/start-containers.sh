#!/bin/bash

# Script maestro para iniciar todos los servicios

echo "🚀 Iniciando todos los servicios..."
echo "================================================"
echo ""

# Crear red si no existe
./scripts/01-create-network.sh
echo ""

# Iniciar servicios de infraestructura primero
./scripts/12-run-prometheus.sh
echo ""

./scripts/13-run-grafana.sh
echo ""

# Opcional: PostgreSQL y Kafka (descomentados si los necesitas)
# ./scripts/10-run-postgres.sh
# echo ""
# ./scripts/11-run-kafka.sh
# echo ""

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