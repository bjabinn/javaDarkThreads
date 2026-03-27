FROM postgres:latest

# Etiquetas
LABEL maintainer="PoC Threads vs Virtual Threads"
LABEL description="PostgreSQL customizado para PoC"

# Variables de entorno por defecto
ENV POSTGRES_USER=postgres \
    POSTGRES_PASSWORD=postgres \
    POSTGRES_DB=testdb

# Crear directorio para scripts de inicialización (opcional)
RUN mkdir -p /docker-entrypoint-initdb.d

# Copiar script de inicialización si existe
COPY init-db.sql /docker-entrypoint-initdb.d/ 2>/dev/null || true

# Exponer puerto
EXPOSE 5432

# Healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=5 \
  CMD pg_isready -U postgres || exit 1