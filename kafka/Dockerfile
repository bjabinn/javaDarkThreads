FROM apache/kafka:latest

# Etiquetas
LABEL maintainer="PoC Threads vs Virtual Threads"
LABEL description="Apache Kafka en modo KRaft (sin Zookeeper)"

# Usuario kafka
USER root

# Variables de entorno para KRaft mode
ENV KAFKA_NODE_ID=1 \
    KAFKA_PROCESS_ROLES=broker,controller \
    KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093 \
    KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
    KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
    KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
    KAFKA_CONTROLLER_QUORUM_VOTERS=1@kafka:9093 \
    KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
    KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
    KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
    KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS=0 \
    KAFKA_NUM_PARTITIONS=3 \
    CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk

# Crear directorio para logs
RUN mkdir -p /var/log/kafka && \
    chown -R kafka:kafka /var/log/kafka

# Volver al usuario kafka
USER kafka

# Exponer puertos
EXPOSE 9092 9093

# Healthcheck
HEALTHCHECK --interval=10s --timeout=10s --retries=5 \
  CMD kafka-broker-api-versions.sh --bootstrap-server localhost:9092 || exit 1

# Script de entrada
CMD ["sh", "-c", "/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/kraft/server.properties"]