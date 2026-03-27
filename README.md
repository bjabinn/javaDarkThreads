# Docker Compose - Stack para PoC Threads vs Virtual Threads

Stack completo con PostgreSQL, Kafka (KRaft), Prometheus y Grafana para pruebas de concepto.

## 📋 Servicios Incluidos

| Servicio   | Puerto | Credenciales        | Descripción                          |
|------------|--------|---------------------|--------------------------------------|
| PostgreSQL | 5432   | postgres/postgres   | Base de datos relacional             |
| Kafka      | 9092   | N/A                 | Message broker (modo KRaft)          |
| Prometheus | 9090   | N/A                 | Sistema de monitorización y alertas  |
| Grafana    | 3000   | admin/admin         | Visualización de métricas            |

## 🚀 Inicio Rápido

### Levantar todos los servicios:
```bash
docker-compose up -d
```

### Ver logs de todos los servicios:
```bash
docker-compose logs -f
```

### Ver logs de un servicio específico:
```bash
docker-compose logs -f prometheus
```

### Parar todos los servicios:
```bash
docker-compose down
```

### Parar y eliminar volúmenes (reset completo):
```bash
docker-compose down -v
```

## 🔍 Acceso a los Servicios

### Grafana
- URL: http://localhost:3000
- Usuario: `admin`
- Contraseña: `admin`
- Prometheus ya viene preconfigurado como datasource

### Prometheus
- URL: http://localhost:9090
- Targets: http://localhost:9090/targets
- Query: http://localhost:9090/graph

### PostgreSQL
```bash
# Conectar desde el host
psql -h localhost -U postgres -d testdb

# Conectar desde Docker
docker exec -it postgres psql -U postgres -d testdb
```

### Kafka
```bash
# Crear un topic de prueba
docker exec -it kafka kafka-topics.sh --create --topic test-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# Listar topics
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# Producir mensajes
docker exec -it kafka kafka-console-producer.sh --topic test-topic --bootstrap-server localhost:9092

# Consumir mensajes
docker exec -it kafka kafka-console-consumer.sh --topic test-topic --from-beginning --bootstrap-server localhost:9092
```

## 📊 Configuración para Aplicaciones Java

### Spring Boot con Micrometer

1. Añade las dependencias necesarias en tu `pom.xml`:
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

2. Configura `application.properties`:
```properties
management.endpoints.web.exposure.include=prometheus,health,info,metrics
management.metrics.export.prometheus.enabled=true
```

3. Actualiza `prometheus.yml` para incluir tus aplicaciones:
```yaml
- job_name: 'java-apps'
  static_configs:
    - targets: ['host.docker.internal:8080', 'host.docker.internal:8081']
  metrics_path: '/actuator/prometheus'
```

4. Reinicia Prometheus:
```bash
docker-compose restart prometheus
```

## 📈 Métricas Útiles para Threads vs Virtual Threads

### Métricas a monitorear en Prometheus/Grafana:

- **JVM Threads**: `jvm_threads_live_threads`
- **CPU Usage**: `process_cpu_usage`
- **Memory**: `jvm_memory_used_bytes`
- **GC**: `jvm_gc_pause_seconds_sum`
- **HTTP Requests**: `http_server_requests_seconds_count`
- **Latency**: `http_server_requests_seconds_sum`

### Queries de ejemplo en Prometheus:

```promql
# Threads activos
jvm_threads_live_threads

# CPU usage
rate(process_cpu_seconds_total[1m])

# Request rate
rate(http_server_requests_seconds_count[1m])

# Latencia media
rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m])
```

## 🛠️ Verificar Estado de los Servicios

```bash
# Ver estado de todos los contenedores
docker-compose ps

# Ver salud de los servicios
docker inspect --format='{{.State.Health.Status}}' prometheus
docker inspect --format='{{.State.Health.Status}}' grafana
docker inspect --format='{{.State.Health.Status}}' postgres
docker inspect --format='{{.State.Health.Status}}' kafka
```

## 🔧 Troubleshooting

### Kafka no arranca
Si Kafka tiene problemas al iniciar, intenta:
```bash
docker-compose down
docker-compose up -d kafka
docker-compose logs -f kafka
```

### Prometheus no encuentra las métricas
1. Verifica que tus aplicaciones expongan métricas: `curl http://localhost:8080/actuator/prometheus`
2. Revisa los targets en Prometheus: http://localhost:9090/targets
3. Verifica la configuración en `prometheus.yml`

### Grafana no muestra datos
1. Verifica que Prometheus esté corriendo: http://localhost:9090
2. Comprueba el datasource en Grafana: Configuration → Data Sources
3. Asegúrate de que hay datos en Prometheus: http://localhost:9090/graph

## 📝 Notas

- **Sin persistencia**: Los datos no persisten entre reinicios (configuración para PoC)
- **Kafka KRaft**: Usa el modo KRaft moderno sin Zookeeper
- **Red Docker**: Todos los servicios en la red `app-network` para comunicación interna
- **Health checks**: Todos los servicios tienen health checks configurados

## 🎯 Para tu PoC de Threads vs Virtual Threads

1. Ejecuta tus aplicaciones exponiendo métricas en diferentes puertos
2. Configura Prometheus para scraping de ambas aplicaciones
3. Crea dashboards en Grafana comparando:
   - Número de threads
   - Uso de CPU
   - Uso de memoria
   - Throughput (requests/segundo)
   - Latencia (p50, p95, p99)
   - Tiempo de respuesta

¡Buena suerte con tu PoC! 🚀