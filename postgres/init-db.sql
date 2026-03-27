-- Script de inicialización de PostgreSQL
-- Este script se ejecuta automáticamente al crear el contenedor

-- Crear extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Crear schema para PoC
CREATE SCHEMA IF NOT EXISTS poc;

-- Ejemplo: Tabla para almacenar resultados de pruebas
CREATE TABLE IF NOT EXISTS poc.test_results (
    id SERIAL PRIMARY KEY,
    test_type VARCHAR(50) NOT NULL,
    thread_type VARCHAR(50) NOT NULL, -- 'platform' o 'virtual'
    threads_count INTEGER,
    duration_ms BIGINT,
    throughput DECIMAL(10,2),
    cpu_usage DECIMAL(5,2),
    memory_mb DECIMAL(10,2),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para consultas comunes
CREATE INDEX IF NOT EXISTS idx_test_results_type ON poc.test_results(test_type);
CREATE INDEX IF NOT EXISTS idx_test_results_thread_type ON poc.test_results(thread_type);
CREATE INDEX IF NOT EXISTS idx_test_results_timestamp ON poc.test_results(timestamp);

-- Tabla para métricas en tiempo real
CREATE TABLE IF NOT EXISTS poc.metrics (
    id SERIAL PRIMARY KEY,
    app_name VARCHAR(100) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(15,2),
    tags JSONB,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_metrics_app_name ON poc.metrics(app_name);
CREATE INDEX IF NOT EXISTS idx_metrics_timestamp ON poc.metrics(timestamp);

-- Mensajes de confirmación
DO $$
BEGIN
    RAISE NOTICE 'Base de datos inicializada correctamente para PoC Threads vs Virtual Threads';
END $$;