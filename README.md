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

## 👀 Others

TBC
