# PoC: Platform Threads vs Reactive vs Virtual Threads

Comparativa práctica de los tres modelos de concurrencia en Java/Spring Boot, monitorizada con Prometheus y Grafana.

---

## Arquitectura

Cada modelo de concurrencia tiene dos aplicaciones: un **servidor** que simula trabajo con I/O y un **cliente generador de carga** que dispara peticiones concurrentes contra el servidor.

```text
[web-client-*]  →  [web-server-*]  →  Prometheus  →  Grafana
```

---

## Aplicaciones Java

### Platform Threads (Hilos de Plataforma)

| Aplicación | Puerto | URL base | Descripción |
| --- | --- | --- | --- |
| web-client-blocking | 8080 | http://localhost:8080 | Generador de carga con hilos OS (RestTemplate + pool fijo) |
| web-server-blocking | 8081 | http://localhost:8081 | Servidor con Spring MVC bloqueante |

**Endpoints relevantes:**
- `POST http://localhost:8080/api/load` — lanza carga contra web-server-blocking
- `GET  http://localhost:8081/api/greeting` — endpoint del servidor
- `GET  http://localhost:8080/swagger-ui/index.html` — Swagger UI cliente
- `GET  http://localhost:8081/swagger-ui/index.html` — Swagger UI servidor
- `GET  http://localhost:8080/actuator/prometheus` — métricas Prometheus cliente
- `GET  http://localhost:8081/actuator/prometheus` — métricas Prometheus servidor

---

### Reactive (WebFlux + Project Reactor)

| Aplicación | Puerto | URL base | Descripción |
| --- | --- | --- | --- |
| web-client-reactive | 8090 | http://localhost:8090 | Generador de carga reactivo (WebClient + Flux) |
| web-server-reactive | 8091 | http://localhost:8091 | Servidor con Spring WebFlux no bloqueante |

**Endpoints relevantes:**
- `POST http://localhost:8090/api/load` — lanza carga contra web-server-reactive
- `GET  http://localhost:8091/api/greeting-reactive` — endpoint del servidor (simula 2s de delay)
- `GET  http://localhost:8090/swagger-ui/index.html` — Swagger UI cliente
- `GET  http://localhost:8091/swagger-ui/index.html` — Swagger UI servidor
- `GET  http://localhost:8090/actuator/prometheus` — métricas Prometheus cliente
- `GET  http://localhost:8091/actuator/prometheus` — métricas Prometheus servidor

---

### Virtual Threads (Project Loom — Java 21+)

| Aplicación | Puerto | URL base | Descripción |
| --- | --- | --- | --- |
| web-client-vt | 8095 | http://localhost:8095 | Generador de carga con virtual threads (RestTemplate + VirtualThreadPerTaskExecutor) |
| web-server-vt | 8096 | http://localhost:8096 | Servidor con Spring MVC + `spring.threads.virtual.enabled=true` |

**Endpoints relevantes:**
- `POST http://localhost:8095/api/load` — lanza carga contra web-server-vt
- `GET  http://localhost:8096/api/greeting` — endpoint del servidor
- `GET  http://localhost:8095/swagger-ui/index.html` — Swagger UI cliente
- `GET  http://localhost:8096/swagger-ui/index.html` — Swagger UI servidor
- `GET  http://localhost:8095/actuator/prometheus` — métricas Prometheus cliente
- `GET  http://localhost:8096/actuator/prometheus` — métricas Prometheus servidor

### Arrancar las aplicaciones Java

Cada aplicación tiene su propio `run.sh` en su directorio raíz:

```bash
# Platform Threads
cd web-server-blocking && ./run.sh   # puerto 8081
cd web-client-blocking && ./run.sh   # puerto 8080

# Reactive
cd web-server-reactive && ./run.sh   # puerto 8091
cd web-client-reactive && ./run.sh   # puerto 8090

# Virtual Threads
cd web-server-vt && ./run.sh         # puerto 8096
cd web-client-vt && ./run.sh         # puerto 8095
```

Arranca primero el servidor y luego el cliente generador de carga de cada modelo.

---

## Infraestructura (Podman)

| Servicio | Puerto | URL | Credenciales | Descripción |
| --- | --- | --- | --- | --- |
| Prometheus | 9090 | http://localhost:9090 | — | Scraping de métricas (1s por app Java) |
| Grafana | 3000 | http://localhost:3000 | admin / admin | Dashboards de visualización |

### Scripts disponibles

Todos los scripts están en el directorio `scripts/`:

```bash
# 1. Construir imágenes (Prometheus + Grafana)
./scripts/build-containers.sh

# 2. Arrancar la infraestructura (red + Prometheus + Grafana)
./scripts/start-containers.sh

# 3. Ver estado de contenedores e imágenes
./scripts/status.sh

# 4. Parar y eliminar todos los contenedores
./scripts/stop-containers.sh
```

> Para preparar el equipo (WSL2, VS Code, Podman, JDK) consulta [GUIA-ENTORNO-DESARROLLO.md](GUIA-ENTORNO-DESARROLLO.md).

### Ejecución manual con Podman

Equivale a lo que hacen los scripts. La red `app-network` es necesaria: Grafana accede a Prometheus por el nombre `prometheus`, y la red por defecto de Podman no resuelve nombres.

```bash
# Red
podman network create app-network

# Prometheus
cd prometheus && podman build -t poc-prometheus:latest . && cd ..
podman run -d --name prometheus --network app-network -p 9090:9090 --restart unless-stopped poc-prometheus:latest

# Grafana
cd grafana && podman build -t poc-grafana:latest . && cd ..
podman run -d --name grafana --network app-network -p 3000:3000 --restart unless-stopped poc-grafana:latest
```

Gestión de los contenedores:

```bash
podman ps -a                       # estado
podman logs -f prometheus          # logs en tiempo real
podman restart prometheus grafana  # reiniciar
podman stop prometheus grafana     # parar
podman rm -f prometheus grafana    # eliminar contenedores
podman rmi poc-prometheus poc-grafana
podman network rm app-network
podman stats                       # CPU y memoria
```

Notas: los datos de Prometheus no persisten al eliminar el contenedor (PoC); Grafana usa el volumen `grafana-data` si se arranca con `13-run-grafana.sh`. Los puertos 9090 y 3000 deben estar libres (`ss -ltnp | grep 9090`).


---

## Prometheus

- URL: http://localhost:9090
- Targets: http://localhost:9090/targets
- Query explorer: http://localhost:9090/graph

**Configuración de scraping:**
- Intervalo global: 15s
- Intervalo por app Java: **1s** (para capturar cambios rápidos bajo carga)

**Jobs configurados:**

| Job | Target | thread_type label |
|---|---|---|
| web-client-blocking | host.docker.internal:8080 | platform |
| web-server-blocking | host.docker.internal:8081 | — |
| web-client-reactive | host.docker.internal:8090 | reactive |
| web-server-reactive | host.docker.internal:8091 | — |
| web-client-vt | host.docker.internal:8095 | virtual |
| web-server-vt | host.docker.internal:8096 | — |

---

## Grafana

- URL: http://localhost:3000
- Usuario: `admin` / Contraseña: `admin`
- Datasource: Prometheus (preconfigurado)
- Refresco: 5s | Ventana por defecto: última hora

### Dashboards disponibles

| Dashboard | Archivo | Aplicaciones monitorizadas |
|---|---|---|
| Platform Threads | `grafana/dashboard-blocking.json` | web-server-blocking, web-client-blocking |
| Reactive | `grafana/dashboard-reactive.json` | web-server-reactive, web-client-reactive |
| Virtual Threads | `grafana/dashboard-vt.json` | web-server-vt, web-client-vt |

### Paneles en cada dashboard (16 paneles)

| # | Panel | Métrica clave |
|---|---|---|
| 1 | Memoria Heap Utilizada | `jvm_memory_used_bytes{area="heap"}` |
| 2 | Memoria Heap Máxima | `jvm_memory_max_bytes{area="heap"}` |
| 3 | % Uso de Heap (alerta >85%) | heap_used / heap_max * 100 |
| 4 | Memoria RSS del Proceso | `process_resident_memory_bytes` |
| 5 | Hilos Vivos | `jvm_threads_live_threads` |
| 6 | Hilos Daemon | `jvm_threads_daemon_threads` |
| 7 | Hilos por Estado | `jvm_threads_states_threads{state}` |
| 8 | CPU del Proceso | `process_cpu_usage` |
| 9 | CPU del Sistema | `system_cpu_usage` |
| 10 | Throughput HTTP (req/s) | `rate(http_server_requests_seconds_count[1m])` |
| 11 | Latencia Media HTTP | sum/count rate |
| 12 | Tasa de GC | `rate(jvm_gc_pause_seconds_sum[1m])` |
| 13 | Frecuencia de GC | `rate(jvm_gc_pause_seconds_count[1m])` |
| 14 | Latencia P95 | `histogram_quantile(0.95, rate(..._bucket[1m]))` |
| 15 | Latencia P99 | `histogram_quantile(0.99, rate(..._bucket[1m]))` |
| 16 | Tasa de Errores HTTP % | `rate(...{status=~"5.."}[1m]) / rate(...[1m]) * 100` |

---

## Métricas emitidas por las aplicaciones

### Custom (tag `thread_type`: platform / reactive / virtual)

| Métrica | Tipo | Descripción |
|---|---|---|
| `load.requests.total` | Counter | Total de peticiones lanzadas en la prueba de carga |
| `load.requests.success` | Counter | Peticiones completadas con éxito |
| `load.requests.failure` | Counter | Peticiones fallidas o rechazadas |
| `load.request.duration` | Timer | Duración individual de cada petición |

### JVM (automáticas via Spring Boot Actuator + Micrometer)

| Métrica | Descripción |
|---|---|
| `jvm_threads_live_threads` | Hilos OS vivos totales |
| `jvm_threads_states_threads{state}` | Hilos por estado: runnable / blocked / waiting / timed-waiting |
| `jvm_memory_used_bytes{area="heap"}` | Heap en uso |
| `jvm_memory_max_bytes{area="heap"}` | Heap máximo configurado |
| `process_resident_memory_bytes` | Memoria RSS del proceso (memoria real en OS) |
| `process_cpu_usage` | CPU consumida por el proceso |
| `jvm_gc_pause_seconds_sum/count` | Tiempo y frecuencia de paradas del GC |

### HTTP (automáticas via Spring MVC / WebFlux)

| Métrica | Descripción |
|---|---|
| `http_server_requests_seconds` | Latencia HTTP con SLO buckets: 50ms, 100ms, 200ms, 500ms, 1s, 2s, 5s |

---

## Qué observar bajo carga

| Métrica | Platform Threads | Reactive | Virtual Threads |
| --- | --- | --- | --- |
| Hilos bloqueados | Suben con la carga | ~0 | ~0 (se desmontan del carrier) |
| Latencia P99 | Se dispara al saturar el pool | Estable | Estable |
| Tasa de errores | Sube cuando el pool se agota | Muy baja | Muy baja |
| Throughput | Limitado por el tamaño del pool | Alto | Alto |
| GC pressure | Moderada | Alta (muchos objetos Mono/Flux) | Moderada |
| CPU (context switch) | Alta bajo saturación | Eficiente | Eficiente |

---

## Troubleshooting

### Prometheus no encuentra las apps Java
```bash
# Verifica que la app expone métricas
curl http://localhost:8080/actuator/prometheus

# Revisa los targets en Prometheus
open http://localhost:9090/targets
```

### Grafana no muestra datos
1. Verifica que Prometheus está UP: http://localhost:9090
2. Comprueba el datasource: Configuration → Data Sources
3. Confirma que hay datos: http://localhost:9090/graph

### Reconstruir Grafana tras cambiar dashboards
```bash
./scripts/05-build-grafana.sh && ./scripts/13-run-grafana.sh
```

### Reconstruir Prometheus tras cambiar prometheus.yml
```bash
./scripts/04-build-prometheus.sh && ./scripts/12-run-prometheus.sh
```
