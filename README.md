# BK Products API

API REST para gestión de productos desarrollada con Spring Boot 3.5.7.

## 📋 Descripción

Esta aplicación proporciona un sistema completo de gestión de productos con las siguientes características:

- **API REST** con endpoints para CRUD de productos
- **Documentación automática** con OpenAPI/Swagger
- **Base de datos en memoria** H2 para desarrollo
- **Manejo de excepciones** centralizado con respuestas estandarizadas
- **Pruebas unitarias** con JUnit 5 y Mockito
- **Cobertura de código** con JaCoCo
- **Validación de datos** con Jakarta Validation

## 🛠️ Tecnologías

- **Java 21**
- **Spring Boot 3.5.7**
  - Spring Web
  - Spring Data JPA
  - Spring Boot Actuator
  - Spring Boot Validation
- **H2 Database** (en memoria)
- **Lombok** (reducción de boilerplate)
- **OpenAPI/Swagger** (documentación)
- **JUnit 5** (pruebas unitarias)
- **Mockito** (mocking)
- **JaCoCo** (cobertura de código)

## 📦 Requisitos Previos

- **Java 21** o superior
- **Maven 3.6+** o usar el wrapper incluido (`mvnw`)
- Cualquier IDE compatible (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/JoanLem/bk-products-linktic.git
cd bk-products
```

### 2. Compilar el proyecto

```bash
# Usando Maven wrapper (recomendado)
./mvnw clean install

# O usando Maven instalado
mvn clean install
```

### 3. Ejecutar la aplicación

```bash
# Usando Maven wrapper
./mvnw spring-boot:run

# O usando Maven instalado
mvn spring-boot:run

# O ejecutando el JAR
java -jar target/bk-products-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: `http://localhost:8080`

## 📚 Endpoints de la API

### Base URL
```
http://localhost:8080/api/v1/products
```

### Endpoints disponibles

#### 1. Health Check
```http
GET /api/v1/products/health
```
**Respuesta:**
```
API products V1 is working
```

#### 2. Crear Producto
```http
POST /api/v1/products
Content-Type: application/json

{
  "name": "Producto Ejemplo",
  "price": 99.99,
  "description": "Descripción del producto",
  "status": true
}
```

**Respuesta (201 Created):**
```json
{
  "data": {
    "type": "product",
    "attributes": {
      "id": 1,
      "name": "Producto Ejemplo",
      "price": 99.99,
      "description": "Descripción del producto"
    }
  }
}
```

#### 3. Obtener Producto por ID
```http
GET /api/v1/products/{id}
```

**Respuesta (200 OK):**
```json
{
  "data": {
    "type": "product",
    "attributes": {
      "id": 1,
      "name": "Producto Ejemplo",
      "price": 99.99,
      "description": "Descripción del producto"
    }
  }
}
```

**Error (404 Not Found):**
```json
{
  "timestamp": "2025-01-08T20:18:23.753+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Producto no encontrado con ID: 999",
  "path": "/api/v1/products/999"
}
```

#### 4. Listar Todos los Productos
```http
GET /api/v1/products
```

**Respuesta (200 OK):**
```json
[
  {
    "data": {
      "type": "product",
      "attributes": {
        "id": 1,
        "name": "Producto 1",
        "price": 99.99,
        "description": "Descripción 1"
      }
    }
  },
  {
    "data": {
      "type": "product",
      "attributes": {
        "id": 2,
        "name": "Producto 2",
        "price": 149.99,
        "description": "Descripción 2"
      }
    }
  }
]
```

## 📖 Documentación API (Swagger)

Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva de la API:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

## 🧪 Pruebas

### Ejecutar todas las pruebas

```bash
./mvnw test
```

### Ejecutar pruebas con cobertura

```bash
./mvnw clean test jacoco:report
```

El reporte de cobertura estará disponible en:
```
target/site/jacoco/index.html
```

### Validar cobertura mínima (80%)

```bash
./mvnw clean test jacoco:check
```

### Estructura de pruebas

Las pruebas están organizadas por funcionalidad:
```
src/test/java/com/example/demo/
├── product/
│   ├── ProductServiceTest.java
│   └── ProductsControllerV1Test.java
└── BkProductsApplicationTests.java
```

## 📁 Estructura del Proyecto

```
bk-products/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/              # Configuraciones
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── exception/           # Manejo de excepciones
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── product/            # Funcionalidad de productos
│   │   │   │   ├── ProductModel.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── ProductService.java
│   │   │   │   └── ProductsControllerV1.java
│   │   │   ├── request/            # DTOs de entrada
│   │   │   │   └── ProductRequest.java
│   │   │   ├── response/           # DTOs de salida
│   │   │   │   └── ProductResponse.java
│   │   │   └── BkProductsApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/demo/
│           └── product/             # Pruebas unitarias
│               ├── ProductServiceTest.java
│               └── ProductsControllerV1Test.java
├── pom.xml
└── README.md
```

## ⚙️ Configuración

### Base de Datos

Por defecto, la aplicación usa H2 en memoria. La configuración está en `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:bk_products
spring.datasource.username=sa
spring.jpa.hibernate.ddl-auto=create-drop
```

**Nota:** Los datos se pierden al reiniciar la aplicación ya que es una base de datos en memoria.

### Consola H2 (Opcional)

Para acceder a la consola de H2 durante el desarrollo, agrega en `application.properties`:

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Luego accede a: http://localhost:8080/h2-console

## 🔧 Comandos Útiles

```bash
# Compilar sin ejecutar pruebas
./mvnw clean compile

# Ejecutar solo pruebas unitarias
./mvnw test

# Generar reporte de cobertura
./mvnw jacoco:report

# Validar cobertura mínima
./mvnw jacoco:check

# Empaquetar la aplicación
./mvnw clean package

# Ejecutar la aplicación
./mvnw spring-boot:run
```

## 📊 Cobertura de Código

El proyecto está configurado con JaCoCo para mantener un mínimo del **80% de cobertura** de código.

- **Cobertura mínima requerida:** 80%
- **Reporte generado en:** `target/site/jacoco/index.html`

## 🐛 Manejo de Errores

La API utiliza un manejador global de excepciones que devuelve respuestas estandarizadas:

```json
{
  "timestamp": "2025-01-08T20:18:23.753+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Mensaje de error específico",
  "path": "/api/v1/products/999"
}
```

## 📝 Validaciones

Los endpoints validan automáticamente los datos de entrada:

- **name:** Obligatorio, no puede estar vacío
- **price:** Obligatorio, debe ser mayor que cero
- **description:** Opcional
- **status:** Opcional

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia Apache 2.0.

## 👥 Autores

- **Linktic** - Desarrollo inicial

## 📞 Soporte

Para soporte, envía un email a support@linktic.com o abre un issue en el repositorio.

---

**Versión:** 0.0.1-SNAPSHOT  
**Última actualización:** 2025

