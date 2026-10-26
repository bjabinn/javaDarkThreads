FROM grafana/grafana:latest

# Etiquetas
LABEL maintainer="PoC Threads vs Virtual Threads"
LABEL description="Grafana customizado con dashboards preconfigurados"

# Cambiar a root para configuración
USER root

# Crear directorios necesarios
RUN mkdir -p /etc/grafana/provisioning/datasources && \
    mkdir -p /etc/grafana/provisioning/dashboards && \
    mkdir -p /var/lib/grafana/dashboards

# Copiar configuraciones
COPY datasources.yml /etc/grafana/provisioning/datasources/
COPY dashboards.yml /etc/grafana/provisioning/dashboards/
COPY dashboard-blocking.json /var/lib/grafana/dashboards/
COPY dashboard-reactive.json /var/lib/grafana/dashboards/
COPY dashboard-vt.json /var/lib/grafana/dashboards/

# Variables de entorno
ENV GF_SECURITY_ADMIN_USER=admin \
    GF_SECURITY_ADMIN_PASSWORD=admin \
    GF_USERS_ALLOW_SIGN_UP=false \
    GF_SERVER_ROOT_URL=http://localhost:3000 \
    GF_ANALYTICS_REPORTING_ENABLED=false

# Volver al usuario grafana
USER grafana

# Exponer puerto
EXPOSE 3000

# Healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:3000/api/health || exit 1

# Comando por defecto
CMD ["/run.sh"]