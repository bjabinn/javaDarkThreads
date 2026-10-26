FROM prom/prometheus:latest

# Etiquetas
LABEL maintainer="PoC Threads vs Virtual Threads"
LABEL description="Prometheus customizado para monitorización de aplicaciones Java"

# Copiar configuración personalizada
COPY prometheus.yml /etc/prometheus/prometheus.yml
COPY alerts.yml /etc/prometheus/alerts.yml

# Crear directorios necesarios
USER root
RUN mkdir -p /prometheus && \
    chown -R nobody:nobody /prometheus && \
    chown -R nobody:nobody /etc/prometheus

USER nobody

# Exponer puerto
EXPOSE 9090

# Healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:9090/-/healthy || exit 1

# Comando por defecto con configuración
CMD [ "--config.file=/etc/prometheus/prometheus.yml", \
      "--storage.tsdb.path=/prometheus", \
      "--web.console.libraries=/usr/share/prometheus/console_libraries", \
      "--web.console.templates=/usr/share/prometheus/consoles", \
      "--web.enable-lifecycle" ]