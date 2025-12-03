# Visitly Assessment

## Overview
Java \- Spring Boot authentication service with JWT, PostgreSQL persistence and optional RabbitMQ event publishing (used in the `docker` profile). Provides user registration, login, profile and role assignment endpoints.

## Requirements
1. Java 17\+
2. Maven 3.6\+
3. Docker & Docker Compose (for containerized DB and RabbitMQ)

## Quick start (Docker Compose)
1. Build and start all services: \
**docker-compose up --build** 
  - This starts:
    - Spring Boot app on port `8081`
    - PostgreSQL on port `5432`
    - RabbitMQ on ports `5672` (AMQP) and `15672` (management UI)

   If you use `docker-compose.yml`, environment variables and `application-docker.properties` should align

2. You can access the api at `http://localhost:8081/visitly/`
  
   ### Database Initialization (Docker Compose)
    - What happens during DB startup
    - init.sql is present in ./docker/init.sql
    - Postgres automatically executes any .sql file inside docker-entrypoint-initdb.d/.
    During initialization, the following happens:
    
    1. users table is created
    2. roles table is created
    3. user_roles mapping table is created
    
    ### **A default admin user is inserted** 
    email: harshil@test.com  
    password: secret123 (plain before hashing)
    
    
    Roles are inserted
    ADMIN
    USER
    
    The default user is assigned the ADMIN role 
    with the credentials to generate an admin JWT token for testing.
    
    email: harshil@test.com  
    password: secret123 

    This gives you a pre-configured admin account immediately after the DB container starts.


## StandAlone Spring Boot app (without Docker):
   - Ensure PostgreSQL is running locally on `localhost:5432` with a database created.
   - Configure `application.properties` with your DB credentials.
   - Run the app: mvn spring-boot:run

## Configuration / Environment variables for running locally and using docker
Set up these env in `application.properties` in `src/main/resources` 
All properties :
- `spring.datasource.url` (e.g. `jdbc:postgresql://localhost:5432/yourdb`)
- `spring.datasource.username`
- `spring.datasource.password`
- `jwt.secret` (or your project-specific JWT property)
- `jwt.jwt.expirationMs` 

Set up these env in `application-docker.properties`  in `src/main/resources`
All properties :
- `spring.datasource.url` (e.g. `jdbc:postgresql://localhost:5432/yourdb`)
- `spring.datasource.username`
- `spring.datasource.password`
- `jwt.secret` (or your project-specific JWT property)
- `jwt.jwt.expirationMs`
- `spring.rabbitmq.host` (defaults to `localhost` or Docker service name)
- `spring.rabbitmq.port`
- `spring.rabbitmq.username`
- `spring.rabbitmq.password`


## Database Schema (Roles & Users)

This section describes the database entities used for authentication and
authorization in the application.\
Two main tables exist: **roles** and **users**, along with a join table
**user_roles** to support a many‑to‑many relationship.

------------------------------------------------------------------------

###  Role Entity (`roles` table)

Represents a security role in the system, such as:

-   `ROLE_USER`
-   `ROLE_ADMIN`

### Table Structure

Column   Type               Description
  -------- ------------------ ---------------------------------------
`id`     BIGSERIAL (PK)     Auto‑generated unique ID
`name`   VARCHAR (unique)   Name of the role (e.g., `ROLE_ADMIN`)

### Notes

-   The `name` field is unique.
-   This table is used to assign role‑based access permissions.

------------------------------------------------------------------------

## User Entity (`users` table)

Represents application users.

### Table Structure

Column         Type               Description
  -------------- ------------------ ------------------------
`id`           BIGSERIAL (PK)     Auto‑generated user ID
`username`     VARCHAR            Optional username
`email`        VARCHAR (unique)   User login email
`password`     VARCHAR            Encrypted password
`last_login`   TIMESTAMP          Last login timestamp

### Relations

-   A user can have multiple roles.
-   Relationship is stored in the **user_roles** join table.

------------------------------------------------------------------------

### User Roles Join Table (`user_roles`)

This table connects users with roles.

Column      Type                     Description
  ----------- ------------------------ ----------------
`user_id`   BIGINT (FK → users.id)   User reference
`role_id`   BIGINT (FK → roles.id)   Role reference

### Notes

-   This enables assigning any number of roles to a user.
-   Cascade is not forced---deleting a user or role requires care.

------------------------------------------------------------------------

### Entity Diagram (Conceptual)

    +---------+        +-----------------+       +--------+
    |  Role   |        |   user_roles    |       |  User  |
    +---------+        +-----------------+       +--------+
    | id      | 1    ∞ | user_id         | ∞   1 | id     |
    | name    |--------| role_id         |--------| email  |
    +---------+        +-----------------+       +--------+

------------------------------------------------------------------------
### Database assumption without docker setup
 create a user with role ROLE_ADMIN in database explicitly to be able to get admin token.

### ✔ Summary

-   **roles** table stores all available role types.
-   **users** table stores user information.
-   **user_roles** handles the many-to-many mapping.
-   Suitable for Spring Security or JWT‑based authentication systems.


## API Endpoints (examples)
- Register: `POST /api/users/register`  
  Body JSON:
  \```json
  { "username": "alice", "email": "alice@example.com", "password": "secret" }
  \```

- Login: `POST /api/users/login`  
  Body JSON:
  \```json
  { "email": "alice@example.com", "password": "secret" }
  \```  
  Response contains JWT. Use it in `Authorization: Bearer <token>`.

- Get current user: `GET /api/users/me` (requires Bearer token)

- Assign roles: `POST /api/users/{userId}/roles` (requires `ADMIN`)  
  Body JSON:
  \```json
  { "roles": ["ADMIN","USER"] }
  \```
- Add roles: `POST /api/roles` (requires `ADMIN`)  
  Body JSON:
  \```json
  { "name": "USER" }
  \```

cURL login example:
\```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"secret"}'
\```

Protected request example:
\```bash
curl -H "Authorization: Bearer <JWT>" http://localhost:8080/api/users/me
\```

## Building
\```bash
mvn clean package
\```

## Notes
- Prefer using the Spring Boot parent `pom` to avoid version mismatches. Example parent in `pom.xml`:
  \```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.1.3</version>
  <relativePath/>
</parent>
\```

- The application publishes registration/login events only when the `docker` profile is active.