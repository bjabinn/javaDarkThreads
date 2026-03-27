# Manual: Ejecutar Servicios sin Docker Compose

Este documento explica cómo construir y ejecutar cada servicio Docker manualmente, sin usar `docker-compose`.

## 📋 Índice

1. [Uso Rápido con Scripts](#uso-rápido-con-scripts)
2. [Comandos Manuales Detallados](#comandos-manuales-detallados)
3. [Gestión de Servicios](#gestión-de-servicios)

---

## 🚀 Uso Rápido con Scripts

### Opción 1: Ejecutar todo automáticamente

```bash
# 1. Dar permisos de ejecución a los scripts
chmod +x scripts/*.sh

# 2. Construir todas las imágenes
./scripts/build-all.sh

# 3. Iniciar todos los servicios
./scripts/start-all.sh

# 4. Ver estado
./scripts/status.sh

# 5. Parar todo
./scripts/stop-all.sh
```

### Opción 2: Ejecutar servicios individuales

```bash
# Dar permisos
chmod +x scripts/*.sh

# Crear red (solo una vez)
./scripts/01-create-network.sh

# Construir imágenes individuales
./scripts/02-build-postgres.sh
./scripts/03-build-kafka.sh
./scripts/04-build-prometheus.sh
./scripts/05-build-grafana.sh

# Ejecutar servicios individuales
./scripts/10-run-postgres.sh
./scripts/11-run-kafka.sh
./scripts/12-run-prometheus.sh
./scripts/13-run-grafana.sh
```

---

## 🔧 Comandos Manuales Detallados

Si prefieres ejecutar todo manualmente sin scripts:

### 1. Crear Red Docker

```bash
docker network create app-network
```

### 2. PostgreSQL

**Construir imagen:**
```bash
cd postgres
docker build -t poc-postgres:latest .
cd ..
```

**Ejecutar contenedor:**
```bash
docker run -d \
  --name postgres \
  --network app-network \
  -p 5432:5432 \
  --restart unless-stopped \
  poc-postgres:latest
```

**Verificar:**
```bash
docker logs postgres
psql -h localhost -U postgres -d testdb
```

### 3. Kafka (modo KRaft)

**Construir imagen:**
```bash
cd kafka
docker build -t poc-kafka:latest .
cd ..
```

**Ejecutar contenedor:**
```bash
docker run -d \
  --name kafka \
  --network app-network \
  -p 9092:9092 \
  -p 9093:9093 \
  --restart unless-stopped \
  poc-kafka:latest
```

**Verificar:**
```bash
docker logs kafka

# Crear un topic de prueba
docker exec -it kafka kafka-topics.sh \
  --create \
  --topic test-topic \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Listar topics
docker exec -it kafka kafka-topics.sh \
  --list \
  --bootstrap-server localhost:9092
```

### 4. Prometheus

**Construir imagen:**
```bash
cd prometheus
docker build -t poc-prometheus:latest .
cd ..
```

**Ejecutar contenedor:**
```bash
docker run -d \
  --name prometheus \
  --network app-network \
  -p 9090:9090 \
  --restart unless-stopped \
  poc-prometheus:latest
```

**Verificar:**
```bash
docker logs prometheus

# Abrir en navegador
open http://localhost:9090
# O en Linux: xdg-open http://localhost:9090

# Ver targets
open http://localhost:9090/targets
```

### 5. Grafana

**Construir imagen:**
```bash
cd grafana
docker build -t poc-grafana:latest .
cd ..
```

**Ejecutar contenedor:**
```bash
docker run -d \
  --name grafana \
  --network app-network \
  -p 3000:3000 \
  --restart unless-stopped \
  poc-grafana:latest
```

**Verificar:**
```bash
docker logs grafana

# Abrir en navegador
open http://localhost:3000
# Login: admin/admin
```

---

## 📊 Gestión de Servicios

### Ver estado de todos los contenedores

```bash
docker ps -a
```

### Ver logs de un servicio específico

```bash
# Ver logs en tiempo real
docker logs -f postgres
docker logs -f kafka
docker logs -f prometheus
docker logs -f grafana

# Ver últimas 100 líneas
docker logs --tail 100 postgres
```

### Reiniciar un servicio

```bash
docker restart postgres
docker restart kafka
docker restart prometheus
docker restart grafana
```

### Parar un servicio

```bash
docker stop postgres
docker stop kafka
docker stop prometheus
docker stop grafana
```

### Eliminar un contenedor

```bash
docker rm -f postgres
docker rm -f kafka
docker rm -f prometheus
docker rm -f grafana
```

### Reconstruir y reiniciar un servicio

```bash
# Ejemplo con PostgreSQL
docker stop postgres
docker rm postgres
cd postgres
docker build -t poc-postgres:latest .
cd ..
docker run -d \
  --name postgres \
  --network app-network \
  -p 5432:5432 \
  --restart unless-stopped \
  poc-postgres:latest
```

### Limpiar todo

```bash
# Parar y eliminar contenedores
docker stop postgres kafka prometheus grafana
docker rm postgres kafka prometheus grafana

# Eliminar imágenes
docker rmi poc-postgres poc-kafka poc-prometheus poc-grafana

# Eliminar red
docker network rm app-network

# Eliminar todo lo no usado (¡CUIDADO!)
docker system prune -a
```

---

## 🔍 Troubleshooting

### El contenedor no arranca

```bash
# Ver logs detallados
docker logs nombre-contenedor

# Inspeccionar configuración
docker inspect nombre-contenedor
```

### Puerto ya en uso

```bash
# Ver qué está usando el puerto
lsof -i :5432    # PostgreSQL
lsof -i :9092    # Kafka
lsof -i :9090    # Prometheus
lsof -i :3000    # Grafana

# Matar proceso
kill -9 PID
```

### La red no existe

```bash
# Verificar redes
docker network ls

# Crear red
docker network create app-network

# Conectar contenedor existente a la red
docker network connect app-network nombre-contenedor
```

### Ver uso de recursos

```bash
# Ver uso de CPU y memoria
docker stats

# Ver uso de disco
docker system df
```

---

## 📝 Notas Importantes

1. **Orden de inicio**: No hay dependencias críticas, pero se recomienda:
   - Primero: PostgreSQL y Kafka
   - Luego: Prometheus
   - Finalmente: Grafana

2. **Red Docker**: Todos los servicios deben estar en la misma red (`app-network`) para comunicarse entre sí.

3. **Nombres de contenedor**: Los nombres (`postgres`, `kafka`, etc.) se usan como DNS dentro de la red Docker.

4. **Persistencia**: Los datos NO persisten al eliminar los contenedores (según configuración PoC).

5. **Puertos**: Asegúrate de que los puertos 5432, 9092, 9093, 9090 y 3000 estén libres.

---

## 🎯 Resumen de Comandos Rápidos

```bash
# SETUP INICIAL
docker network create app-network

# CONSTRUIR TODO
cd postgres && docker build -t poc-postgres:latest . && cd ..
cd kafka && docker build -t poc-kafka:latest . && cd ..
cd prometheus && docker build -t poc-prometheus:latest . && cd ..
cd grafana && docker build -t poc-grafana:latest . && cd ..

# INICIAR TODO
docker run -d --name postgres --network app-network -p 5432:5432 --restart unless-stopped poc-postgres:latest
docker run -d --name kafka --network app-network -p 9092:9092 -p 9093:9093 --restart unless-stopped poc-kafka:latest
docker run -d --name prometheus --network app-network -p 9090:9090 --restart unless-stopped poc-prometheus:latest
docker run -d --name grafana --network app-network -p 3000:3000 --restart unless-stopped poc-grafana:latest

# VER ESTADO
docker ps

# PARAR TODO
docker stop postgres kafka prometheus grafana

# ELIMINAR TODO
docker rm postgres kafka prometheus grafana
docker rmi poc-postgres poc-kafka poc-prometheus poc-grafana
docker network rm app-network