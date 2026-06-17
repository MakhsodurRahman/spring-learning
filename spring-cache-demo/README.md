Spring Cache demo (simple in-memory)

This small project demonstrates Spring Cache using Spring's simple ConcurrentMapCache (no Caffeine/Redis).

Requirements

- Java 21
- Maven

Run

```bash
mvn spring-boot:run
```

Endpoints

- GET /books/{isbn}  -- returns book (first call slower, subsequent cached)
- POST /books/{isbn}/evict  -- evict cache for isbn

Notes

- This uses Spring's `ConcurrentMapCacheManager` (in-memory, not distributed).

Swagger

- Swagger UI: `http://localhost:8080/swagger-ui.html` or `/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
