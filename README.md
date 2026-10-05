# Tareas API

API REST de gestión de tareas desarrollada con Spring Boot 3 y Java 21, como parte de la asignatura de Prácticas Continuas del máster en Ingeniería de Software (Universidad de Murcia).

## Autores

- Manuel Chica ([@manuel-umu](https://github.com/manuel-umu))
- Emilio González ([@EmilioG999](https://github.com/EmilioG999))

## Tecnologías

- Java 21
- Spring Boot 3 (Web, Data JPA, Validation)
- PostgreSQL 16
- Docker y Docker Compose
- Maven
- JUnit 5

## Puesta en marcha

Requisitos: JDK 21 y Maven y una instancia de PostgreSQL accesible.

```bash
mvn clean package
java -jar target/tareas-api-0.0.1-SNAPSHOT.jar
```

La API arranca en `http://localhost:8080`.

## Puesta en marcha con Docker

Requisitos: Docker y Docker Compose.

1. Copia la plantilla de variables de entorno y ajusta la contraseña:

```bash
cp .env.example .env
```

2. Levanta la API junto a PostgreSQL:

```bash
docker compose up --build -d
```

3. Comprueba que ambos servicios están saludables:

```bash
docker compose ps
```

4. Prueba el endpoint de salud y la API:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/tasks
```

5. Para parar (conservando los datos) o reiniciar desde cero:

```bash
docker compose down       # conserva el volumen de datos
docker compose down -v    # borra también los datos
```

## Dev Container

Este proyecto incluye una configuración de [Dev Container](https://containers.dev/) en `.devcontainer/devcontainer.json`, que permite desarrollar con el mismo entorno (JDK, Maven, Docker) sin instalar nada en la máquina local.

1. Instala la extensión **Dev Containers** en VS Code.
2. Abre la carpeta del proyecto en VS Code.
3. `Ctrl+Shift+P` → **Dev Containers: Reopen in Container**.
4. Dentro del contenedor, levanta el stack igual que en local:

```bash
docker compose up --build
```

5. La API será accesible en `http://127.0.0.1:8080` desde el navegador de tu máquina.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/tasks` | Lista tareas paginadas (`page`, `size`), opcionalmente filtradas por `estado` |
| `GET` | `/api/tasks/search?titulo=` | Busca tareas por título, paginado |
| `GET` | `/api/tasks/{id}` | Obtiene una tarea por id |
| `POST` | `/api/tasks` | Crea una tarea |
| `PUT` | `/api/tasks/{id}` | Actualiza una tarea |
| `DELETE` | `/api/tasks/{id}` | Elimina una tarea |

## Tests

```bash
mvn test
```

## Formateo de código

El proyecto usa [Spotless](https://github.com/diffplug/spotless) (estilo Google) con un hook de pre-commit versionado en `.githooks/`. Actívalo con:

```bash
git config core.hooksPath .githooks
```
