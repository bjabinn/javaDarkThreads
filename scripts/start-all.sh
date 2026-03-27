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

# Iniciar aplicación Java
./scripts/14-run-java-app.sh
echo ""

echo "================================================"
echo "✅ ¡Todos los servicios están corriendo!"
echo ""
echo "📋 Servicios disponibles:"
echo "  - Java App:    http://localhost:8080"
echo "  - Prometheus:  http://localhost:9090"
echo "  - Grafana:     http://localhost:3000 (admin/admin)"
echo ""
echo "🎯 Endpoints de la aplicación:"
echo "  - Health:   GET  http://localhost:8080/api/health"
echo "  - Info:     GET  http://localhost:8080/api/info"
echo "  - Threads:  GET  http://localhost:8080/api/threads"
echo "  - Load Test: POST http://localhost:8080/api/load"
echo "  - Metrics:  GET  http://localhost:8080/actuator/prometheus"
echo ""
echo "📊 Ejemplo de uso:"
echo "  curl -X POST http://localhost:8080/api/load \\"
echo "    -H 'Content-Type: application/json' \\"
echo "    -d '{\"requests\": 100, \"threads\": 10, \"delayMs\": 500}'"
echo ""
echo "Para ver el estado de los contenedores:"
echo "  docker ps"
echo ""
echo "Para ver logs de un servicio:"
echo "  docker logs -f java-platform-app"