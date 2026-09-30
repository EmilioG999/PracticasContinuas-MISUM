# Tareas API

API REST de gestión de tareas desarrollada con Spring Boot 3 y Java 21, como parte de la asignatura de Prácticas Continuas del máster en Ingeniería de Software (Universidad de Murcia).

## Autores

- Manuel Chica ([@manuel-umu](https://github.com/manuel-umu))
- Emilio González ([@EmilioG999](https://github.com/EmilioG999))

## Tecnologías

- Java 21
- Spring Boot 3 (Web, Data JPA, Validation)
- H2 (base de datos en memoria)
- Maven
- JUnit 5

## Puesta en marcha

Requisitos: JDK 21 y Maven.

```bash
mvn clean package
java -jar target/tareas-api-0.0.1-SNAPSHOT.jar
```

La API arranca en `http://localhost:8080`.

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
