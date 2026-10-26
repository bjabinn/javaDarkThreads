# Guía de preración de entorno de desarrollo – JavaDarkThreads

26 oct 2026

## 1. Objeto y alcance

Este documento establece el software que debe instalar cada asistente para trabajar en el garage del proyecto **javaDarkThreads** (comparativa Platform Threads vs Reactive vs Virtual Threads, repositorio de ViewNext). El entorno estándar es **VS Code conectado a WSL2, con Podman como motor de contenedores**.

Nosotros lo hemos implementado con este software: 

| Campo | Valor |
| --- | --- |
| Distribución Linux | Fedora Linux 44 sobre WSL2 |
| Requisito previo | WSL2 |

## 2. Descripción técnica del proyecto

El repositorio contiene seis aplicaciones Spring Boot (un servidor y un cliente generador de carga por cada modelo de concurrencia) y dos servicios de observabilidad en contenedor: Prometheus y Grafana.

| Componente | Tecnología | Puerto |
| --- | --- | --- |
| web-client-blocking / web-server-blocking | Spring MVC, hilos de plataforma | 8080 / 8081 |
| web-client-reactive / web-server-reactive | Spring WebFlux, Project Reactor | 8090 / 8091 |
| web-client-vt / web-server-vt | Spring MVC, hilos virtuales (Java 21) | 8095 / 8096 |
| Prometheus | Contenedor, scraping cada 1 s | 9090 |
| Grafana | Contenedor, 3 dashboards preconfigurados | 3000 |

Todas las aplicaciones declaran **Java 21** como versión objetivo (se compilan y ejecutan con el JDK 25 instalado) y Maven Wrapper (`mvnw`), de modo que no hace falta instalar Maven. 

## 3. Software requerido

Cada asistente necesita seis elementos. Los de la primera parte se instalan en Windows; los de la segunda, dentro de Fedora (WSL2).

| Ámbito | Software | Versión | Finalidad |
| --- | --- | --- | --- |
| Windows | WSL2 + Fedora Linux | 44 | Entorno Linux donde se ejecuta todo |
| Windows | Visual Studio Code | ≥ 1.90 | Editor |
| Windows | Extensión WSL (`ms-vscode-remote.remote-wsl`) | última | Apunta VS Code a WSL2 |
| WSL (Fedora) | OpenJDK | **25** (el código declara Java 21 y compila con un JDK superior) | Compilar y ejecutar las apps |
| WSL (Fedora) | Podman | ≥ 5.0 (incluido en Fedora 44) | Motor de contenedores sin demonio |
| WSL (Fedora) | Git, curl, `dos2unix` | últimas | Clonado, pruebas y conversión de saltos de línea |

Extensiones de VS Code (se instalan **en WSL**, no en Windows):

- Extension Pack for Java (`vscjava.vscode-java-pack`)
- Spring Boot Extension Pack (`vmware.vscode-boot-dev-pack`)
- Container Tools (`ms-azuretools.vscode-containers`), configurada con Podman como cliente
- YAML (`redhat.vscode-yaml`), útil para `prometheus.yml` y los dashboards de Grafana

**No instalar:** Docker Desktop, Maven (se usa `./mvnw`). 

## 4. Procedimiento de instalación

**Paso 1 – WSL2 y Fedora (PowerShell como administrador, reinicio posterior).**

# 1. Comprobar que la virtualizacion esta activa en firmware (debe indicar Si/Yes)
```Powershell
systeminfo | Select-String "Virtualiz"
```

# 2. Habilitar los componentes de Windows necesarios para WSL2
```Powershell
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
```

# 3. Reiniciar el equipo
Restart-Computer

# 4. Tras el reinicio: actualizar WSL, fijar WSL2 e instalar Fedora
```Powershell
wsl --update
wsl --set-default-version 2
wsl --install -d FedoraLinux-44
```

# 5. Verificar
```Powershell
wsl -l -v
```

La última orden debe mostrar `FedoraLinux-44` con `VERSION 2`. Al primer arranque se crea el usuario Linux.

**Paso 2 – VS Code y extensión WSL (Windows).** Instalar VS Code desde el catálogo corporativo o con `winget install Microsoft.VisualStudioCode`, y añadir la extensión `ms-vscode-remote.remote-wsl`.

**Paso 3 – Paquetes base, JDK 25 y Podman (terminal de Fedora).**

```bash
sudo dnf install -y git curl dos2unix java-25-openjdk-devel podman
```

**Paso 4 – Clonar el repositorio dentro de WSL.** 

```bash
git clone <URL-del-repositorio> javaDarkThreads
cd javaDarkThreads
git config core.autocrlf input
```

**Paso 5 – Normalizar saltos de línea.** Los `.sh` del repositorio están guardados con CRLF (formato Windows) y fallan en Linux con `bad interpreter`. Se convierten una vez:

```bash
find . -name "*.sh" -exec dos2unix {} +
chmod +x scripts/*.sh */run.sh */mvnw
```

## 5. Configuración de VS Code con WSL2 y Podman

1. Desde la terminal de Fedora, dentro de la carpeta del repositorio, ejecutar `code .`. VS Code abre una ventana remota; la esquina inferior izquierda debe mostrar **WSL: FedoraLinux-44**.
2. En esa ventana remota, instalar las extensiones de la sección 3 (botón *Install in WSL: FedoraLinux-44*).
3. En Container Tools, indicar Podman como cliente. Añadir a los ajustes de usuario **remotos** (`Preferences: Open Remote Settings (WSL)`):

```json
{
  "containers.containerClient": "com.microsoft.visualstudio.containers.podman",
  "java.configuration.runtimes": [
    { "name": "JavaSE-25", "path": "/usr/lib/jvm/java-25-openjdk", "default": true }
  ]
}
```

4. Activar el socket de usuario de Podman para que la extensión liste contenedores e imágenes:

```bash
systemctl --user enable --now podman.socket
```

5. **Ajuste necesario en Prometheus.**

Prometheus, dentro de su contenedor, consulta las apps Java mediante `host.docker.internal` (fichero `prometheus/prometheus.yml`). Con Podman rootless ese nombre puede no resolverse. Si en `http://localhost:9090/targets` aparecen los jobs en estado DOWN, se debe añadir `--add-host=host.docker.internal:host-gateway` al `podman run` de `scripts/12-run-prometheus.sh`, o sustituir `host.docker.internal` por `host.containers.internal` en `prometheus.yml` y reconstruir la imagen. Este cambio debe acordarse e incorporarse al repositorio una sola vez por el equipo organizador, no por cada asistente.

## 6. Verificación y arranque del proyecto

Comprobación del entorno, desde la terminal de Fedora:

```bash
java -version          # debe indicar 25.x
podman --version       # >= 5.0
podman run --rm hello-world
```

Arranque de la infraestructura (Prometheus y Grafana):

```bash
./scripts/build-containers.sh
./scripts/start-containers.sh
./scripts/status.sh
```

Arranque de las aplicaciones Java, una terminal por aplicación y siempre servidor antes que cliente. Ejemplo con hilos virtuales:

```bash
cd web-server-vt && ./run.sh      # puerto 8096
cd web-client-vt && ./run.sh      # puerto 8095
```

Resultado esperado, accesible desde el navegador de Windows:

| Servicio | URL | Credenciales |
| --- | --- | --- |
| Prometheus (targets) | http://localhost:9090/targets | — |
| Grafana | http://localhost:3000 | admin / admin |
| Swagger servidor VT | http://localhost:8096/swagger-ui.html | — |
| Métricas servidor VT | http://localhost:8096/actuator/prometheus | — |

Para parar la infraestructura: `./scripts/stop-containers.sh`.

## 7. Incidencias frecuentes

| Síntoma | Causa | Solución |
| --- | --- | --- |
| `bad interpreter: /bin/bash^M` al ejecutar un `.sh` | Saltos de línea CRLF | `dos2unix` (sección 4, paso 5) |
| `Permission denied` al lanzar `./run.sh` o `./mvnw` | Falta bit de ejecución | `chmod +x` (sección 4, paso 5) |
| Maven muy lento o ficheros bloqueados | Repositorio en `/mnt/c` u OneDrive | Clonar en `~/dev` dentro de WSL |
| Jobs DOWN en Prometheus | `host.docker.internal` no resuelve en Podman | Ajuste de la sección 5 |
| Container Tools no lista contenedores | Socket de Podman inactivo | `systemctl --user enable --now podman.socket` |
| `systemctl --user` falla | systemd desactivado en la distro | Añadir `[boot]` con `systemd=true` en `/etc/wsl.conf` y ejecutar `wsl --shutdown` |
| Puerto en uso (8080, 9090, 3000…) | Otro proceso local | `ss -ltnp \| grep <puerto>` y detenerlo |
| `Hyper-V`/`WSL` no arranca | Virtualización desactivada | Activar VT-x/SVM en BIOS; consultar a Soporte IT |
| WSL consume demasiada RAM | Sin límite configurado | `.wslconfig` (sección 5, punto 5) |
