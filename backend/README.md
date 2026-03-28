# Purecipes Backend

## Run

Set environment variables (defaults shown):

- `PURECIPES_BACKEND_PORT` (default: `8080`)
- `PURECIPES_GOOGLE_WEB_CLIENT_ID` (required for Google sign-in verification)
- `PURECIPES_DB_URL` (default: `jdbc:postgresql://localhost:5432/purecipes`)
- `PURECIPES_DB_USER` (default: `postgres`)
- `PURECIPES_DB_PASSWORD` (default: `postgres`)
- `PURECIPES_DB_POOL_SIZE` (default: `5`)

Start the server:

```bash
./gradlew :backend:run
```

When running the mobile and Wasm app against the local backend, start it on port `9090` so it matches the current debug client configuration:

```bash
PURECIPES_BACKEND_PORT=9090 ./gradlew :backend:run
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
