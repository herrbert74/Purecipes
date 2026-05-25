# Purecipes Backend

## Run

Set environment variables (defaults shown):

- `PURECIPES_BACKEND_PORT` (default: `8080`)
- `PURECIPES_GOOGLE_WEB_CLIENT_ID` (required for Google sign-in verification)
- `PURECIPES_FIREBASE_PROJECT_ID` (default: `purecipes-50e5c`, required for Firebase email sign-in token verification). Use the Firebase **project ID** from Project settings (for example `purecipes-50e5c`), not the Google web client ID and not an authorized domain.
- `PURECIPES_DB_URL` (default: `jdbc:postgresql://localhost:5432/purecipes`)
- `PURECIPES_DB_USER` (default: `postgres`)
- `PURECIPES_DB_PASSWORD` (default: `postgres`)
- `PURECIPES_DB_POOL_SIZE` (default: `5`)

Start the server:

```bash
./gradlew :backend:run
```

The Google web client ID can also come from Gradle properties, using the same lookup order as the app modules:

- `purecipes.googleWebClientId`
- `PURECIPES_GOOGLE_WEB_CLIENT_ID`
- environment variable `PURECIPES_GOOGLE_WEB_CLIENT_ID`

The Firebase project ID can also come from Gradle properties, using the same lookup order:

- `purecipes.firebaseProjectId`
- `PURECIPES_FIREBASE_PROJECT_ID`
- environment variable `PURECIPES_FIREBASE_PROJECT_ID`

When you launch the packaged backend via the shadow jar, Gradle properties are embedded into the jar at build time through a generated resource. If you change the client ID property, rebuild the jar before restarting the backend.

When running the mobile and Wasm app against the local backend, start it on port `9090` so it matches the current debug client configuration:

```bash
PURECIPES_BACKEND_PORT=9090 ./gradlew :backend:run
```

The backend listens on all interfaces by default (`PURECIPES_BACKEND_HOST` defaults to `0.0.0.0`). Clients still need a concrete host (not `0.0.0.0`).

Debug client hosts:

- Android emulator: `10.0.2.2` (automatic)
- Android physical device over USB: `127.0.0.1` after `adb reverse tcp:9090 tcp:9090` (automatic)
- Android physical device or iOS device on Wi‑Fi: set your machine's LAN IP in `local.properties`:

```properties
purecipes.debugBackendHost=192.168.1.42
```

Rebuild the debug app after changing that value. iOS Simulator and Wasm use `localhost` when the property is unset.

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
