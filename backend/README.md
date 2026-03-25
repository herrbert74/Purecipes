# Purecipes Backend

## Run

Set environment variables (defaults shown):

- `PURECIPES_BACKEND_PORT` (default: `8080`)
- `PURECIPES_DB_URL` (default: `jdbc:postgresql://localhost:5432/purecipes`)
- `PURECIPES_DB_USER` (default: `postgres`)
- `PURECIPES_DB_PASSWORD` (default: `postgres`)
- `PURECIPES_DB_POOL_SIZE` (default: `5`)

Start the server:

```bash
./gradlew :backend:run
```

Health check:

```bash
curl http://localhost:8080/health
```

Recipe search:

```bash
curl "http://localhost:8080/recipes/search?query=italian"
curl "http://localhost:8080/recipes/search?query=chicken&limit=20"
```

Favorites:

```bash
curl http://localhost:8080/favorites
curl -X POST http://localhost:8080/favorites/42
curl -X DELETE http://localhost:8080/favorites/42
```
