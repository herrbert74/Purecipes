# Purecipes

Purecipes is a Kotlin Multiplatform project with Android, iOS and WasmJS targets, and a Postgres/ktor backend. The
primary goal of the app to make it easier for users to follow recipe instructions, and find new recipes.

## 🧩 Setup

### ☁️ Backend

Build it:

```
./gradlew :backend:shadowJar --no-daemon
```

Run it locally:

```
PURECIPES_BACKEND_PORT=9090 java -jar backend/build/libs/backend.jar
```

For a physical Android device over USB, also run `adb reverse tcp:9090 tcp:9090`. For Wi‑Fi devices (or a fixed LAN IP), add `purecipes.debugBackendHost=<your-machine-ip>` to `local.properties` and rebuild the debug app. See [backend/README.md](backend/README.md).

Deep linking and share URLs: [docs/deep-linking-website-setup.md](docs/deep-linking-website-setup.md) (website `/.well-known` files and local `adb` testing).

### 🌐 Wasm

Wasm builds use a system-installed Node.js and Yarn Classic instead of letting the Kotlin Gradle plugin download them.
This avoids repository conflicts with `RepositoriesMode.FAIL_ON_PROJECT_REPOS`.

Install Node.js on macOS with Homebrew:

```
brew install node
node --version
npm --version
```

Enable Yarn Classic with Corepack:

```
corepack enable
corepack prepare yarn@1.22.22 --activate
yarn --version
```

The project is currently verified with Node 24 and Yarn 1.22.22.

Run the application

```
./gradlew :umbrella:wasmJsBrowserDevelopmentRun
```

## 📚 Tech stack

- UI developed in [Compose Multiplatform](https://kotlinlang.org/compose-multiplatform/)
- Following the [Material 3](https://m3.material.io/) guidelines
- Dependency injection with [Metro](https://zacsweers.github.io/metro/latest/)
- Database using [androidx-room](https://developer.android.com/training/data-storage/room/)
- Some modules contain shared [Gradle test fixtures modules](https://docs.gradle.org/current/userguide/java_testing.html#sec:java_test_fixtures)
- Using [Compose Preview Screenshot Testing](https://developer.android.com/studio/preview/compose-screenshot-testing) to verify UI

## 🏛 Architecture

FlickSlate architecture is Clean(ish) Architecture as [recommended by Google](https://developer.android.com/topic/architecture).

Let's take a look in each major part of the application:

* **app** - Contains the Android entry point to the application.
* **iosApp** - Contains the iOS entry point to the application.
* **umbrella** - Works as an umbrella library for the iosApp, contains the Wasm entry point, plus Jetpack
  **navigation**.
* **feature** modules (and submodules for **domain**, **data**, and **ui**):
  * **search**
  * **To be determined**
* **Submodules** within above features.
  * **domain** - Contains the **shared** **domain model**, the **api interface**, and optionally **use cases**. Domain depends only on itself and all interaction it does is via _dependency
    inversion_.
  * **data** - Contains the (**db**, **network**, etc) modules.
  * **ui** - Presentation layer
* **shared** - **Shared** domain, UI and data modules specific to this app.
* **base** - Kotlin and Android base classes, reusable in any apps, but not extracted to separate library yet.

## 📈 Analytics and consent

The app reads analytics configuration from the platform-specific build config layer:

* Android uses `BuildConfig` fields from `app/build.gradle.kts`.
* iOS and Wasm use `BuildKonfig` values from `umbrella/build.gradle.kts`.
* The same config keys are used across targets:
  * `PURECIPES_GA_MEASUREMENT_ID` / `purecipesGaMeasurementId`
  * `PURECIPES_MIXPANEL_PROJECT_TOKEN` / `purecipesMixpanelProjectToken`
  * `PURECIPES_USERCENTRICS_SETTINGS_ID` / `purecipesUsercentricsSettingsId`

These values are loaded from either Gradle properties or environment variables.

## 👀 Others

TBC
