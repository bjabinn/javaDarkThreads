# Platform Threads PoC - Aplicación Java

Aplicación Spring Boot para testing de rendimiento con **Platform Threads (threads clásicos)** y OpenTelemetry.

## 🎯 Características

- **REST API** con endpoint parametrizable para pruebas de carga
- **Platform Threads** (threads bloqueantes clásicos) usando ExecutorService
- **Micrometer + Prometheus** para métricas detalladas
- **OpenTelemetry** para observabilidad
- **Spring Boot Actuator** para health checks y métricas

## 📋 Endpoints Disponibles

### Endpoint de Prueba de Carga

**POST /api/load**

Ejecuta una prueba de carga con parámetros configurables.

**Request Body:**
```json
{
  "requests": 1000,      // Número total de peticiones a ejecutar
  "threads": 50,         // Número de threads en el pool
  "delayMs": 500,        // Delay simulado por petición (ms)
  "targetUrl": null      // URL externa a llamar (opcional)
}
```

**Response:**
```json
{
  "totalRequests": 1000,
  "threadsUsed": 50,
  "totalDurationMs": 10245,
  "throughput": 97.61,
  "avgResponseTimeMs": 502.3,
  "minResponseTimeMs": 500,
  "maxResponseTimeMs": 520,
  "successCount": 1000,
  "failureCount": 0,
  "startTime": "2024-03-27T19:00:00",
  "endTime": "2024-03-27T19:00:10",
  "additionalMetrics": {
    "activeThreads": 52,
    "totalThreads": 65,
    "availableProcessors": 8,
    "freeMemoryMB": 256,
    "totalMemoryMB": 512,
    "maxMemoryMB": 1024
  }
}
```

### Otros Endpoints

- **GET /api/health** - Estado de la aplicación
- **GET /api/info** - Información de la aplicación
- **GET /api/threads** - Información de threads activos
- **GET /actuator/health** - Health check de Actuator
- **GET /actuator/prometheus** - Métricas en formato Prometheus

## 🚀 Ejecución Local (sin Docker)

```bash
# Compilar
./mvnw clean package

# Ejecutar
java -jar target/platform-threads-poc-0.0.1-SNAPSHOT.jar

# O directamente
./mvnw spring-boot:run
```

La aplicación estará disponible en: http://localhost:8080

## 🐳 Ejecución con Docker

```bash
# Construir imagen
docker build -t poc-java-platform-threads:latest .

# Ejecutar contenedor
docker run -d \
  --name java-platform-app \
  -p 8080:8080 \
  poc-java-platform-threads:latest

# Ver logs
docker logs -f java-platform-app
```

## 📊 Métricas Disponibles

La aplicación exporta las siguientes métricas custom:

### Contadores
- `load.requests.total` - Total de peticiones ejecutadas
- `load.requests.success` - Peticiones exitosas
- `load.requests.failure` - Peticiones fallidas

### Timers
- `load.request.duration` - Duración de cada petición individual
- `http.server.requests` - Métricas HTTP estándar de Spring

### Métricas JVM (automáticas)
- `jvm.threads.live` - Threads activos
- `jvm.threads.peak` - Pico de threads
- `jvm.memory.used` - Memoria utilizada
- `jvm.memory.max` - Memoria máxima
- `jvm.gc.pause` - Pausas de GC
- `process.cpu.usage` - Uso de CPU
- `system.cpu.usage` - Uso de CPU del sistema

## 🧪 Ejemplos de Pruebas

### Prueba básica (100 requests, 10 threads)
```bash
curl -X POST http://localhost:8080/api/load \
  -H 'Content-Type: application/json' \
  -d '{
    "requests": 100,
    "threads": 10,
    "delayMs": 500
  }'
```

### Prueba de carga media (1000 requests, 50 threads)
```bash
curl -X POST http://localhost:8080/api/load \
  -H 'Content-Type: application/json' \
  -d '{
    "requests": 1000,
    "threads": 50,
    "delayMs": 500
  }'
```

### Prueba de alta concurrencia (5000 requests, 200 threads)
```bash
curl -X POST http://localhost:8080/api/load \
  -H 'Content-Type: application/json' \
  -d '{
    "requests": 5000,
    "threads": 200,
    "delayMs": 100
  }'
```

### Consultar estado de threads
```bash
curl http://localhost:8080/api/threads
```

## 📈 Monitorización

### Ver métricas en Prometheus
1. Accede a Prometheus: http://localhost:9090
2. Consulta métricas:
   - `load_requests_total`
   - `jvm_threads_live_threads`
   - `process_cpu_usage`
   - `jvm_memory_used_bytes`

### Visualizar en Grafana
1. Accede a Grafana: http://localhost:3000 (admin/admin)
2. El datasource de Prometheus ya está configurado
3. Crea dashboards o usa el dashboard precargado

## 🔧 Configuración

### Variables de Entorno
- `SERVER_PORT` - Puerto del servidor (default: 8080)
- `SPRING_PROFILES_ACTIVE` - Perfil activo (default, docker)

### Propiedades en application.yaml
- `thread-pool.core-size` - Tamaño mínimo del pool (default: 50)
- `thread-pool.max-size` - Tamaño máximo del pool (default: 200)
- `thread-pool.queue-capacity` - Capacidad de la cola (default: 1000)

## 🏗️ Arquitectura

```
LoadController
    ↓
LoadGeneratorService
    ↓
ExecutorService (FixedThreadPool)
    ↓
[Thread 1] [Thread 2] ... [Thread N]
    ↓
Simulated Blocking I/O (Thread.sleep)
    ↓
Micrometer Metrics
    ↓
Prometheus Scraping
    ↓
Grafana Visualization
```

## 📝 Notas Técnicas

- Usa **Platform Threads** (threads clásicos del JVM)
- Los threads son **bloqueantes** durante el delay
- Cada thread consume ~1MB de stack memory
- Thread pool configurado con política de rechazo por defecto
- Compatible con Java 21+