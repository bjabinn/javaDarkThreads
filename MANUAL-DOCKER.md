# Manual: Ejecutar Servicios sin Docker Compose

Este documento explica cómo construir y ejecutar Prometheus y Grafana manualmente, sin `docker-compose`. Con Podman, el comando `docker` funciona igual mediante el paquete `podman-docker`.

## Uso rápido con scripts

```bash
chmod +x scripts/*.sh

./scripts/build-containers.sh   # red + imágenes de Prometheus y Grafana
./scripts/start-containers.sh   # arranca Prometheus y Grafana
./scripts/status.sh             # estado
./scripts/stop-containers.sh    # para y elimina los contenedores
```

Scripts individuales: `01-create-network.sh`, `04-build-prometheus.sh`, `05-build-grafana.sh`, `12-run-prometheus.sh` y `13-run-grafana.sh`.

## Comandos manuales

### 1. Red

```bash
docker network create app-network
```

### 2. Prometheus

```bash
cd prometheus && docker build -t poc-prometheus:latest . && cd ..
docker run -d --name prometheus --network app-network -p 9090:9090 --restart unless-stopped poc-prometheus:latest
```

Verificar: `docker logs prometheus` y http://localhost:9090/targets

### 3. Grafana

```bash
cd grafana && docker build -t poc-grafana:latest . && cd ..
docker run -d --name grafana --network app-network -p 3000:3000 --restart unless-stopped poc-grafana:latest
```

Verificar: `docker logs grafana` y http://localhost:3000 (admin / admin)

## Gestión de servicios

```bash
docker ps -a                       # estado
docker logs -f prometheus          # logs en tiempo real
docker restart prometheus grafana  # reiniciar
docker stop prometheus grafana     # parar
docker rm -f prometheus grafana    # eliminar contenedores
docker rmi poc-prometheus poc-grafana
docker network rm app-network
docker stats                       # CPU y memoria
```

## Troubleshooting

- **El contenedor no arranca:** `docker logs <nombre>` y `docker inspect <nombre>`.
- **Puerto en uso:** `ss -ltnp | grep 9090` (o 3000) y detener el proceso.
- **La red no existe:** `docker network create app-network`.

## Notas

1. Los contenedores comparten la red `app-network` y se resuelven por nombre.
2. Los datos de Prometheus no persisten al eliminar el contenedor (PoC); Grafana usa el volumen `grafana-data` si se arranca con `13-run-grafana.sh`.
3. Puertos necesarios libres: 9090 y 3000.
