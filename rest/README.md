# Java EE 7 - REST Example

Example REST API built with **Java EE 7**, using **JAX-RS**, **EJB** and **JSON-P**.

This module demonstrates the basic structure of a Java EE REST application, including:

- REST endpoints with JAX-RS
- Separation between resource and service layers
- Stateless EJB services
- JSON request and response processing
- HTTP status handling
- Maven WAR packaging
- Deployment on GlassFish
- OpenAPI 3 documentation
- Swagger UI

---

## Technologies

- Java 8
- Java EE 7
- JAX-RS
- EJB
- JSON-P
- Maven
- GlassFish 4.1.2
- OpenAPI 3.0
- Swagger UI

---

## Requirements

The following software is required to build and run this module:

- JDK 8
- Maven 3.x
- GlassFish 4.1.2

The module uses the Java EE 7 API:

```text
javax:javaee-api:7.0
```

with Maven scope:

```text
provided
```

The Java EE API is required during compilation, while the runtime implementation is provided by GlassFish.

---

## Project Structure

```text
rest/
├── README.md
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── pt/
        │       └── brunoribeiro/
        │           └── examples/
        │               └── rest/
        │                   ├── config/
        │                   │   └── RestApplication.java
        │                   │
        │                   ├── model/
        │                   │   └── User.java
        │                   │
        │                   ├── resource/
        │                   │   ├── HelloResource.java
        │                   │   └── UserResource.java
        │                   │
        │                   └── service/
        │                       └── UserService.java
        │
        └── webapp/
            ├── docs/
            │   └── openapi.yaml
            │
            └── swagger/
                └── index.html
```

---

# Architecture

The application follows a simple layered structure:

```text
HTTP Request
     |
     v
JAX-RS Resource
     |
     v
EJB Service
     |
     v
In-memory storage
```

For the Users API:

```text
HTTP Request
     |
     v
UserResource
     |
     | @EJB
     v
UserService
     |
     v
ConcurrentHashMap<Long, User>
```

The responsibilities are separated as follows.

### `config`

Contains the JAX-RS application configuration.

Example:

```java
@ApplicationPath("/api")
public class RestApplication extends Application {
}
```

This defines:

```text
/api
```

as the base path for all REST resources.

---

### `resource`

Contains the REST endpoints.

Responsibilities include:

- HTTP methods
- URL paths
- Path parameters
- Reading request bodies
- Producing JSON responses
- Returning appropriate HTTP status codes

Examples:

```java
@GET
```

```java
@POST
```

```java
@PUT
```

```java
@DELETE
```

---

### `service`

Contains application and business logic.

`UserService` is implemented as a stateless EJB:

```java
@Stateless
public class UserService {
}
```

It is injected into the REST resource using:

```java
@EJB
private UserService userService;
```

The service is responsible for:

- Listing users
- Finding users
- Creating users
- Updating users
- Deleting users
- Generating user identifiers
- Managing the current in-memory storage

---

### `model`

Contains domain objects.

The current example contains:

```text
User
├── id
├── name
└── email
```

---

# Request Flow

A typical request follows this path:

```text
GET /rest/api/users/1
          |
          v
     UserResource
          |
          v
 userService.findById(1)
          |
          v
     UserService
          |
          v
 ConcurrentHashMap
          |
          v
        User
          |
          v
     HTTP 200 JSON
```

The REST layer does not directly manage the underlying data storage.

This means the current in-memory implementation can later be replaced by:

```text
UserResource
     |
     v
UserService
     |
     v
Repository
     |
     v
JPA / EntityManager
     |
     v
Database
```

without significantly changing the REST API.

---

# Base URL

After deployment on GlassFish:

```text
http://localhost:8080/rest/api
```

The complete URL is composed of:

```text
http://localhost:8080
        +
/rest
        +
/api
```

where:

```text
/rest
```

is the application context generated from:

```text
rest.war
```

and:

```text
/api
```

is configured through:

```java
@ApplicationPath("/api")
```

---

# REST Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/hello` | Simple REST example |
| GET | `/hello/{name}` | Greeting using a path parameter |
| GET | `/users` | List all users |
| GET | `/users/{id}` | Get a user by ID |
| POST | `/users` | Create a user |
| PUT | `/users/{id}` | Update a user |
| DELETE | `/users/{id}` | Delete a user |

---

# Hello API

## GET `/hello`

Returns a simple text response.

### Request

```http
GET /rest/api/hello
```

### Response

```text
Hello Java EE 7 REST!
```

### Status

```text
200 OK
```

---

## GET `/hello/{name}`

Returns a greeting using a path parameter.

### Example Request

```http
GET /rest/api/hello/Bruno
```

### Response

```text
Hello Bruno!
```

### Status

```text
200 OK
```

This endpoint demonstrates:

```java
@Path("/{name}")
```

and:

```java
@PathParam("name")
```

---

# Users API

## User Object

A user is represented as:

```json
{
  "id": 1,
  "name": "Bruno",
  "email": "bruno@example.com"
}
```

Properties:

| Property | Type | Description |
|---|---|---|
| `id` | Long | User identifier |
| `name` | String | User name |
| `email` | String | User email address |

---

# List Users

## GET `/users`

Returns all available users.

### Request

```http
GET /rest/api/users
```

### Response

```json
[
  {
    "id": 1,
    "name": "Bruno",
    "email": "bruno@example.com"
  },
  {
    "id": 2,
    "name": "Maria",
    "email": "maria@example.com"
  }
]
```

### Status

```text
200 OK
```

---

# Get User

## GET `/users/{id}`

Returns a user by ID.

### Example Request

```http
GET /rest/api/users/1
```

### Response

```json
{
  "id": 1,
  "name": "Bruno",
  "email": "bruno@example.com"
}
```

### Status

```text
200 OK
```

If the user does not exist:

```json
{
  "error": "User not found"
}
```

Status:

```text
404 Not Found
```

---

# Create User

## POST `/users`

Creates a new user.

### Request

```http
POST /rest/api/users
Content-Type: application/json
```

### Request Body

```json
{
  "name": "Ana",
  "email": "ana@example.com"
}
```

### Response

```json
{
  "id": 3,
  "name": "Ana",
  "email": "ana@example.com"
}
```

### Status

```text
201 Created
```

The response also contains a `Location` header pointing to the created resource.

Example:

```text
Location: http://localhost:8080/rest/api/users/3
```

If required fields are missing:

```json
{
  "error": "Name and email are required"
}
```

Status:

```text
400 Bad Request
```

---

# Update User

## PUT `/users/{id}`

Updates an existing user.

### Example Request

```http
PUT /rest/api/users/1
Content-Type: application/json
```

### Request Body

```json
{
  "name": "Bruno Ribeiro",
  "email": "bruno.ribeiro@example.com"
}
```

### Response

```json
{
  "id": 1,
  "name": "Bruno Ribeiro",
  "email": "bruno.ribeiro@example.com"
}
```

### Status

```text
200 OK
```

Possible errors:

```text
400 Bad Request
```

```text
404 Not Found
```

---

# Delete User

## DELETE `/users/{id}`

Deletes an existing user.

### Example Request

```http
DELETE /rest/api/users/1
```

### Successful Response

```text
204 No Content
```

If the user does not exist:

```json
{
  "error": "User not found"
}
```

Status:

```text
404 Not Found
```

---

# HTTP Status Codes

| Status | Description |
|---|---|
| `200 OK` | Request completed successfully |
| `201 Created` | Resource created successfully |
| `204 No Content` | Resource deleted successfully |
| `400 Bad Request` | Invalid request |
| `404 Not Found` | Resource not found |

---

# JSON Processing

This project uses the Java EE 7 JSON Processing API:

```text
JSON-P
```

Package:

```java
javax.json
```

Example:

```java
Json.createObjectBuilder()
        .add("id", user.getId())
        .add("name", user.getName())
        .add("email", user.getEmail())
        .build();
```

Request bodies are parsed using:

```java
JsonReader
```

Example:

```java
try (JsonReader reader =
             Json.createReader(new StringReader(requestBody))) {

    JsonObject json = reader.readObject();
}
```

---

# EJB Service

The `UserService` is a stateless EJB:

```java
@Stateless
public class UserService {
}
```

It is injected into the JAX-RS resource:

```java
@EJB
private UserService userService;
```

This demonstrates container-managed dependency injection between JAX-RS and EJB components.

The service currently provides:

```text
findAll()
findById()
create()
update()
delete()
```

---

# Data Storage

User data is currently stored in memory using:

```java
ConcurrentHashMap<Long, User>
```

Identifiers are generated using:

```java
AtomicLong
```

This implementation is intentionally simple and exists only to demonstrate REST and EJB concepts.

Data is lost when the application or server is restarted.

A future version can replace this storage with:

```text
JPA
+
EntityManager
+
Relational Database
```

---

# Build

The REST module is part of the parent Maven multi-module project.

From the root project:

```bash
mvn clean package
```

This builds all configured Maven modules.

To build only the REST module:

```bash
mvn -pl rest clean package
```

After a successful build:

```text
BUILD SUCCESS
```

the WAR is generated at:

```text
rest/target/rest.war
```

---

# Maven Packaging

The module uses:

```xml
<packaging>war</packaging>
```

The generated artifact is:

```text
rest.war
```

The Java EE API is declared through Maven as:

```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
</dependency>
```

The parent Maven project manages:

```text
javax:javaee-api:7.0
```

with:

```text
scope = provided
```

GlassFish provides the Java EE implementation at runtime.

---

# GlassFish

This example uses:

```text
GlassFish 4.1.2
```

with:

```text
JDK 8
```

GlassFish 4.x is based on Java EE 7 and uses the:

```text
javax.*
```

namespace.

---

# Start GlassFish

On Windows:

```bat
C:\glassfish4\glassfish\bin\asadmin.bat start-domain
```

Expected result:

```text
Successfully started the domain : domain1
```

GlassFish is normally available at:

```text
http://localhost:8080
```

The Administration Console is available at:

```text
http://localhost:4848
```

---

# Deploy Application

Build first:

```bash
mvn clean package
```

Then deploy:

```bat
C:\glassfish4\glassfish\bin\asadmin.bat deploy rest\target\rest.war
```

For subsequent deployments:

```bat
C:\glassfish4\glassfish\bin\asadmin.bat deploy --force=true rest\target\rest.war
```

After deployment:

```text
http://localhost:8080/rest/api
```

---

# cURL Examples

## List Users

```bash
curl http://localhost:8080/rest/api/users
```

---

## Get User

```bash
curl http://localhost:8080/rest/api/users/1
```

---

## Create User

```bash
curl -X POST -H "Content-Type: application/json" -d "{\"name\":\"Ana\",\"email\":\"ana@example.com\"}" http://localhost:8080/rest/api/users
```

---

## Update User

```bash
curl -X PUT -H "Content-Type: application/json" -d "{\"name\":\"Ana Silva\",\"email\":\"ana.silva@example.com\"}" http://localhost:8080/rest/api/users/3
```

---

## Delete User

```bash
curl -X DELETE http://localhost:8080/rest/api/users/3
```

---

# OpenAPI

The REST API is documented using:

```text
OpenAPI 3.0
```

The API contract is stored at:

```text
src/main/webapp/docs/openapi.yaml
```

After deployment, the OpenAPI document is available at:

```text
http://localhost:8080/rest/docs/openapi.yaml
```

The OpenAPI document describes:

- API paths
- HTTP methods
- Parameters
- Request bodies
- Responses
- HTTP status codes
- User schema
- Error schema

---

# OpenAPI Schemas

The OpenAPI specification defines reusable schemas.

Examples:

```text
User
UserInput
Error
```

The `User` schema represents responses:

```yaml
User:
  type: object
  properties:
    id:
      type: integer
      format: int64

    name:
      type: string

    email:
      type: string
```

The `UserInput` schema represents POST and PUT requests.

---

# Swagger UI

Swagger UI is included as a simple static frontend for the OpenAPI contract.

Location:

```text
src/main/webapp/swagger/index.html
```

After deployment:

```text
http://localhost:8080/rest/swagger/
```

Swagger UI loads the OpenAPI specification from:

```text
../docs/openapi.yaml
```

This allows the API documentation to remain relative to the application context.

---

# Testing the API with Swagger UI

Open:

```text
http://localhost:8080/rest/swagger/
```

Select an endpoint.

For example:

```text
GET /users
```

Click:

```text
Try it out
```

and then:

```text
Execute
```

Swagger UI displays:

- Request URL
- Request parameters
- Request body
- Response status
- Response headers
- Response body

This allows the REST API to be tested directly from the browser.

---

# OpenAPI and Swagger

The two concepts have different responsibilities:

```text
OpenAPI
   |
   | API contract
   v
openapi.yaml
   |
   v
Swagger UI
   |
   | visualization / testing
   v
Browser
```

OpenAPI defines the API contract.

Swagger UI presents that contract as interactive documentation.

---

# Current Application Flow

```text
                         GlassFish
                            |
                            v
                 Java EE REST Application
                            |
             +--------------+--------------+
             |                             |
             v                             v
         JAX-RS                        OpenAPI
             |                             |
        UserResource                  openapi.yaml
             |                             |
             v                             v
        UserService                    Swagger UI
             |
             v
    ConcurrentHashMap
```

---

# Java EE Concepts Demonstrated

This module demonstrates the following Java EE concepts:

### JAX-RS

- `@ApplicationPath`
- `@Path`
- `@GET`
- `@POST`
- `@PUT`
- `@DELETE`
- `@PathParam`
- `@Consumes`
- `@Produces`
- `@Context`
- `UriInfo`
- `Response`

### HTTP

- GET
- POST
- PUT
- DELETE
- `200 OK`
- `201 Created`
- `204 No Content`
- `400 Bad Request`
- `404 Not Found`
- `Location` header

### JSON-P

- `Json`
- `JsonObject`
- `JsonReader`
- `JsonArrayBuilder`

### EJB

- `@Stateless`
- `@EJB`
- Container-managed component lifecycle
- Service-layer separation

### Maven

- Parent POM
- Multi-module build
- WAR packaging
- Java EE API with `provided` scope

### Deployment

- GlassFish 4.1.2
- WAR deployment
- Application context

### API Documentation

- OpenAPI 3
- Reusable schemas
- Swagger UI
- Interactive API testing

---

# Current Limitations

This project is intentionally simple.

Current limitations include:

- No database persistence
- No JPA
- No authentication
- No authorization
- Basic validation only
- JSON conversion is manually implemented
- No global exception handling
- No integration tests
- OpenAPI contract is maintained manually

---

# Planned Improvements

Future versions may introduce:

```text
REST
 |
 v
EJB
 |
 v
JPA
 |
 v
Database
```

Possible improvements:

- JPA entities
- `EntityManager`
- Database persistence
- Repository layer
- Bean Validation
- `@Valid`
- Custom exceptions
- `ExceptionMapper`
- CDI
- Authentication
- Role-based authorization
- Integration tests
- Automated OpenAPI generation
- Swagger annotations

---

# Learning Goals

The main goal of this module is to demonstrate how a Java EE 7 REST application is structured and how the main platform components work together.

The example progressively introduces:

```text
JAX-RS
   |
   v
REST API
   |
   v
EJB Service
   |
   v
Data Access
   |
   v
JPA / Database
```

This provides a foundation for the other Java EE examples contained in the parent project.