#!/bin/bash

# Script para eliminar el volumen persistente de Grafana

VOLUME="grafana-data"

echo "⚠️  Esto eliminará todos los datos persistentes de Grafana ($VOLUME)"
read -p "¿Continuar? (s/N): " confirm

if [[ "$confirm" != "s" && "$confirm" != "S" ]]; then
  echo "Operación cancelada."
  exit 0
fi

# El volumen no se puede eliminar si el contenedor lo está usando
if podman ps --format '{{.Names}}' | grep -q '^grafana$'; then
  echo "Parando contenedor grafana..."
  podman stop grafana 2>/dev/null
  podman rm grafana 2>/dev/null
fi

echo "Eliminando volumen $VOLUME..."
podman volume rm $VOLUME

if [ $? -eq 0 ]; then
  echo "✅ Volumen $VOLUME eliminado correctamente"
else
  echo "❌ No se pudo eliminar el volumen (puede que no exista)"
  exit 1
fi
