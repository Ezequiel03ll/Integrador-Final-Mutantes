# Integrador-Final-Mutantes




# 🧬 Mutant Detector API – Proyecto Backend con Spring Boot

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg)]()
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue.svg)]()
[![Status](https://img.shields.io/badge/Estado-En%20desarrollo-success.svg)]()

> API REST que determina si una secuencia de ADN pertenece a un mutante o a un humano, inspirada en el challenge técnico de MercadoLibre para Backend.

---

## 📑 Índice

1. [Descripción General](#-descripción-general)
2. [Stack Tecnológico](#-stack-tecnológico)
3. [Requisitos Previos](#-requisitos-previos)
4. [Puesta en Marcha](#-puesta-en-marcha)
5. [Estructura del Proyecto](#-estructura-del-proyecto)
6. [Arquitectura y Capas](#-arquitectura-y-capas)
7. [Algoritmo de Detección](#-algoritmo-de-detección)
8. [Modelo de Datos y Base de Datos](#-modelo-de-datos-y-base-de-datos)
9. [Endpoints Disponibles](#-endpoints-disponibles)
10. [Ejecución de Tests](#-ejecución-de-tests)
11. [Empaquetado con Gradle (JAR)](#-empaquetado-con-gradle-jar)
12. [Uso con Docker](#-uso-con-docker)
13. [Conceptos que se Practican](#-conceptos-que-se-practican)
14. [Ideas para Extender el Proyecto](#-ideas-para-extender-el-proyecto)

---

## 🧾 Descripción General

Este proyecto implementa una API REST que recibe una matriz de ADN y determina si corresponde a un mutante o no.
El ADN se representa como un arreglo de strings (`String[] dna`) que forman una matriz **NxN** con las letras:

* **A** (Adenina)
* **T** (Timina)
* **C** (Citosina)
* **G** (Guanina)

Un individuo se considera **mutante** si se detectan **al menos dos secuencias** de **4 letras iguales** de forma consecutiva en alguna de estas direcciones:

* Horizontal (→)
* Vertical (↓)
* Diagonal descendente (↘)
* Diagonal ascendente (↗)

Además de resolver el challenge, el proyecto está armado para servir como **material de estudio**, mostrando:

* Buenas prácticas con Spring Boot
* Diseño en capas
* Uso de JPA/H2
* Testing con JUnit 5 y Mockito
* Documentación con Swagger

---

## 🛠 Stack Tecnológico

* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3.x
* **Build Tool:** Gradle (Wrapper incluido)
* **Persistencia:** Spring Data JPA + H2 (en memoria)
* **Testing:** JUnit 5, Mockito, Spring Test (MockMvc)
* **Documentación:** Springdoc OpenAPI / Swagger UI
* **Lombok:** Para reducir boilerplate (getters/setters, etc.)

---

## 📦 Requisitos Previos

Asegurate de tener instalado:

| Herramienta | Mínimo                       | Uso                        |
| ----------- | ---------------------------- | -------------------------- |
| Java JDK    | 17                           | Ejecutar y compilar la app |
| Git         | Cualquiera                   | Clonar el repo             |
| IDE Java    | IntelliJ / VS Code / Eclipse | Desarrollo                 |
| Docker      | (Opcional)                   | Ejecutar con contenedores  |

Verificación rápida:

```bash
java -version
git --version
```

---

## 🚀 Puesta en Marcha

### 1️⃣ Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd Mutantes   # o el nombre que tenga el proyecto
```

### 2️⃣ Compilar y correr tests

```bash
# Windows
gradlew.bat test

# Linux/Mac
./gradlew test
```

### 3️⃣ Levantar la aplicación

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

Cuando arranque, la API quedará escuchando en:

* `http://localhost:8080`

### 4️⃣ Interfaces útiles

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **Consola H2:** `http://localhost:8080/h2-console`

    * JDBC URL: `jdbc:h2:mem:testdb`
    * User: `sa`
    * Pass: *(vacío)*

---

## 📁 Estructura del Proyecto

```text
src/
├── main/
│   ├── java/org/example/mutant_detector/
│   │   ├── config/
│   │   │   └── SwaggerConfig.java
│   │   ├── controller/
│   │   │   └── MutantController.java
│   │   ├── dto/
│   │   │   ├── DnaRequest.java
│   │   │   ├── StatsResponse.java
│   │   │   └── ErrorResponse.java
│   │   ├── entity/
│   │   │   └── DnaRecord.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── DnaHashCalculationException.java
│   │   ├── repository/
│   │   │   └── DnaRecordRepository.java
│   │   ├── service/
│   │   │   ├── MutantDetector.java
│   │   │   ├── MutantService.java
│   │   │   └── StatsService.java
│   │   ├── validation/
│   │   │   ├── ValidDnaSequence.java
│   │   │   └── ValidDnaSequenceValidator.java
│   │   └── MutantDetectorApplication.java
│   │
│   └── resources/
│       └── application.properties
│
└── test/java/org/example/mutant_detector/
    ├── controller/
    │   └── MutantControllerTest.java
    └── service/
        ├── MutantDetectorTest.java
        ├── MutantServiceTest.java
        └── StatsServiceTest.java
```

---

## 🧱 Arquitectura y Capas

La aplicación sigue un diseño clásico por capas:

* **Controller:** maneja HTTP, código de estado y mapeo de endpoints.
* **DTOs:** modelos para entrada/salida de la API.
* **Services:** lógica de negocio (detección, cacheo, estadísticas).
* **Repository:** acceso a base de datos con Spring Data JPA.
* **Entity:** representación de la tabla `dna_records`.
* **Validation & Exception:** validaciones custom y manejo global de errores.

Flujo típico para `POST /mutant`:

1. Llega el JSON al `MutantController`.
2. Spring convierte JSON → `DnaRequest` y ejecuta validaciones (`@ValidDnaSequence`).
3. `MutantService` calcula un hash del ADN y consulta en `DnaRecordRepository`.
4. Si ya está en BD → devuelve resultado cacheado.
5. Si no está → llama a `MutantDetector` para analizar la matriz.
6. Se persiste en BD con `DnaRecord`.
7. El controller responde:

    * `200 OK` si es mutante.
    * `403 Forbidden` si es humano.
    * `400 Bad Request` si el ADN es inválido.

---

## 🧠 Algoritmo de Detección

La lógica está centralizada en `MutantDetector`.

### Reglas

* La matriz debe ser **cuadrada** (NxN) y de tamaño mínimo `4x4`.
* Solo se permiten caracteres: `A`, `T`, `C`, `G`.
* Se cuentan secuencias de longitud **4** en línea.
* Si se encuentran **más de una secuencia**, se considera **mutante**.

### Idea General

1. Validar la matriz (no nula, NxN, tamaños correctos).

2. Convertir `String[]` a `char[][]` para acceso rápido.

3. Recorrer todas las posiciones de la matriz.

4. Desde cada posición, revisar:

    * Horizontal (→)
    * Vertical (↓)
    * Diagonal descendente (↘)
    * Diagonal ascendente (↗)

5. Llevar un contador de secuencias encontradas.

6. Aplicar **early termination**: si el contador pasa de 1, retornar `true` de inmediato.

Complejidad:

* Peor caso: O(N²)
* Con early termination, promedio bastante menor para casos mutantes.

---

## 💽 Modelo de Datos y Base de Datos

Se utiliza H2 en memoria con una sola entidad:

```java
@Entity
@Table(
  name = "dna_records",
  indexes = {
      @Index(name = "idx_dna_hash", columnList = "dnaHash"),
      @Index(name = "idx_is_mutant", columnList = "isMutant")
  }
)
public class DnaRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String dnaHash;

    @Column(nullable = false)
    private boolean isMutant;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

¿Por qué guardar un **hash** del ADN?

* Se evita almacenar cadenas muy grandes.
* Permite búsquedas rápidas con índice sobre `dnaHash`.
* Facilita implementar cacheo: si ya se analizó ese ADN, no se recalcula.

---

## 🌐 Endpoints Disponibles

### 🔹 `POST /mutant`

Analiza una secuencia de ADN.

**Request:**

```http
POST /mutant
Content-Type: application/json

{
  "dna": [
    "ATGCGA",
    "CAGTGC",
    "TTATGT",
    "AGAAGG",
    "CCCCTA",
    "TCACTG"
  ]
}
```

**Respuestas posibles:**

* `200 OK` → Es mutante.
* `403 Forbidden` → No es mutante.
* `400 Bad Request` → ADN inválido (no NxN, caracteres inválidos, etc.).

**Ejemplo con cURL:**

```bash
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}'
```

---

### 🔹 `GET /stats`

Retorna estadísticas de ejecuciones previas.

**Request:**

```http
GET /stats
```

**Response ejemplo:**

```json
{
  "count_mutant_dna": 10,
  "count_human_dna": 5,
  "ratio": 2.0
}
```

* `count_mutant_dna`: cantidad de ADN mutantes analizados.
* `count_human_dna`: cantidad de ADN humanos analizados.
* `ratio`: `mutantes / humanos` (controlando división por cero).

---

## 🧪 Ejecución de Tests

El proyecto incluye:

* Tests unitarios del algoritmo (`MutantDetectorTest`).
* Tests del servicio (`MutantServiceTest`, `StatsServiceTest`).
* Tests de integración con MockMvc (`MutantControllerTest`).

### Comandos

```bash
# Todos los tests
./gradlew test

# Test de una clase
./gradlew test --tests org.example.mutant_detector.service.MutantDetectorTest

# Un test puntual
./gradlew test --tests "*MutantDetectorTest.testEarlyTermination"
```

---

## 📦 Empaquetado con Gradle (JAR)

Para generar el JAR ejecutable:

```bash
# JAR con tests
./gradlew bootJar

# Saltándose tests
./gradlew bootJar -x test
```

El artefacto quedará en:

```text
build/libs/<nombre-del-jar>.jar
```

Para ejecutarlo:

```bash
java -jar build/libs/<nombre-del-jar>.jar
# o con puerto custom:
java -jar -Dserver.port=9090 build/libs/<nombre-del-jar>.jar
```

---

## 🐳 Uso con Docker

El proyecto incluye un `Dockerfile` con **multi-stage build**:

1. Primera etapa: compila el proyecto y genera el JAR usando Gradle.
2. Segunda etapa: copia solo el JAR a una imagen ligera con OpenJDK 17.

### Build de la imagen

```bash
docker build -t mutant-detector-api .
```

### Ejecutar el contenedor

```bash
docker run -p 8080:8080 --name mutant-detector-container mutant-detector-api
```

Luego podés acceder a:

* `http://localhost:8080/swagger-ui.html`
* `http://localhost:8080/stats`

---

## 📚 Conceptos que se Practican

* Diseño de **API REST** con Spring Boot.
* Patrón **Service/Repository**.
* Uso de **Spring Data JPA** con índices.
* **Bean Validation** y validaciones custom.
* Manejo centralizado de errores con `@ControllerAdvice`.
* Tests unitarios + de integración con **JUnit 5** y **MockMvc**.
* Empaquetado con **Gradle bootJar**.
* Despliegue en contenedor con **Docker**.

---

## ✍️ Datos del Estudiante

**Nombre:** Pablo Ezequiel Llampa

**Legajo:** 50974

**Curso:** 3k10

.

