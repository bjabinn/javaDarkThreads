# Guía Completa - PoC Platform Threads con OpenTelemetry

Esta guía completa te ayudará a ejecutar y probar la aplicación Java con Platform Threads, Prometheus y Grafana.

## 📋 Servicios Disponibles

- **Java App**: http://localhost:8080
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

## 🚀 Inicio Rápido

```bash
# 1. Dar permisos
chmod +x scripts/*.sh

# 2. Construir todo
./scripts/build-all.sh

# 3. Iniciar servicios
./scripts/start-all.sh

# 4. Verificar estado
./scripts/status.sh
```

## 💻 Endpoints Principales

### POST /api/load - Prueba de Carga
```bash
curl -X POST http://localhost:8080/api/load \
  -H 'Content-Type: application/json' \
  -d '{
    "requests": 1000,
    "threads": 50,
    "delayMs": 500
  }'
```

### GET /api/health - Estado
```bash
curl http://localhost:8080/api/health
```

### GET /api/threads - Info Threads
```bash
curl http://localhost:8080/api/threads
```

## 🧪 Escenarios de Prueba Recomendados

### 1. Warmup (Calentar JVM)
```bash
curl -X POST http://localhost:8080/api/load \
  -H 'Content-Type: application/json' \
  -d '{"requests": 50, "threads": 5, "delayMs": 100}'
```

### 2. Bajo Paralelismo
```bash
curl -X POST http://localhost:8080/api/load \
  -H 'Content-Type: application/json' \
  -d '{"requests": 1000, "threads": 10, "delayMs": 500}'
```

### 3. Paralelismo Medio (Recomendado)
```bash
curl -X POST http://localhost:8080/api/load \
  -H 'Content-Type: application/json' \
  -d '{"requests": 1000, "threads": 50, "delayMs": 500}'
```

### 4. Alto Paralelismo
```bash
curl -X POST http://localhost:8080/api/load \
  -H 'Content-Type: application/json' \
  -d '{"requests": 5000, "threads": 200, "delayMs": 500}'
```

### 5. Estrés Test
```bash
curl -X POST http://localhost:8080/api/load \
  -H 'Content-Type: application/json' \
  -d '{"requests": 10000, "threads": 500, "delayMs": 100}'
```

## 📊 Métricas en Prometheus

Accede a http://localhost:9090 y consulta:

```promql
# Threads activos
jvm_threads_live_threads

# CPU usage
process_cpu_usage

# Memoria heap
jvm_memory_used_bytes{area="heap"}

# Peticiones totales
load_requests_total

# Throughput
rate(load_requests_total[1m])

# Latencia P95
histogram_quantile(0.95, rate(load_request_duration_seconds_bucket[5m]))
```

## 📈 Dashboards en Grafana

1. Accede a http://localhost:3000
2. Login: admin/admin
3. El datasource de Prometheus ya está configurado
4. Puedes usar el dashboard precargado o crear el tuyo

## 🛑 Parar Servicios

```bash
./scripts/stop-all.sh
```

## 📝 Notas Importantes

- **Platform Threads** son threads bloqueantes del OS
- Cada thread consume ~1MB de memoria
- Con 500 threads = ~500MB solo en stacks
- Ideal para comparar con Virtual Threads después