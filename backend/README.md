# PlaceNextAI — Backend

Complete Spring Boot backend. Includes JWT authentication and the endpoints the
React frontend calls: `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me`.

## How to import

1. Extract the ZIP. You get a complete `backend` folder (pom.xml at its root).
2. Open IntelliJ IDEA → **File → Open** → select the `backend` folder (or its `pom.xml`) → **Open as Project**.
3. Wait for Maven to download dependencies.
4. Set the Project SDK: **File → Project Structure → Project → SDK → JDK 25**
   (if your machine has JDK 21, change `<java.version>` in `pom.xml` to `21` — everything else is compatible).
5. Enable Lombok: **Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable annotation processing**.

## How to run

From a terminal inside the `backend` folder (requires Maven installed and on PATH):

```
mvn clean install
mvn spring-boot:run
```

Or in IntelliJ: run `PlaceNextAiApplication`.

- API base: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

Optional — generate the Maven wrapper (`mvnw` / `mvnw.cmd`) so teammates don't need Maven installed.
The wrapper contains a binary jar that must be produced by Maven itself, so run this once:

```
mvn -N wrapper:wrapper
```

After that, `mvnw.cmd spring-boot:run` works on any machine without a global Maven install.

## Auto-seeded accounts (created on first startup)

| Role      | Email                  | Password    |
|-----------|------------------------|-------------|
| Admin     | admin@placenextai.com  | Admin@123   |
| Student   | ananya@college.edu     | Student@123 |
| Recruiter | vikram@technova.com    | Recruit@123 |

Passwords are BCrypt-hashed by the app (see `config/DataSeeder.java`), which is why
users are seeded in code rather than in raw SQL.

## How to connect MySQL

1. Install and start **MySQL 8** (Windows service `MySQL80`).
2. Defaults in `src/main/resources/application.properties`:
   - URL: `jdbc:mysql://localhost:3306/placenextai_db` — the database is created
     automatically on first run (`createDatabaseIfNotExist=true`).
   - Username: `root` · Password: `root`
3. Different credentials? Either edit `application.properties` or set environment variables:

```
set DB_USERNAME=your_mysql_user
set DB_PASSWORD=your_mysql_password
mvn spring-boot:run
```

4. `database/schema.sql` documents the full schema; `database/sample_data.sql` adds extra
   sample jobs. The app creates/updates the schema itself (`ddl-auto=update`).

## Frontend pairing

The React frontend proxies `/api` → `http://localhost:8080` (see its `vite.config.js`),
attaches `Authorization: Bearer <JWT>` from `/api/auth/login`, and loads the profile from
`/api/auth/me`. CORS for `http://localhost:5173` is already configured in `CorsConfig.java`.

## Postman

Import `postman/PlaceNextAI.postman_collection.json`. The **Auth** folder covers the three
frontend endpoints; login/register requests auto-save the JWT into collection variables
used by every protected request.
