# AI Agent Development Guidelines

Purecipes is a Kotlin Multiplatform project with Android, iOS and WasmJS targets, and a Postgres/ktor backend. The 
primary goal of the app to make it easier for users to follow recipe instructions, and find new recipes.

## Core Principles

- **NEVER use `@SuppressLint` or `@Suppress` to bypass errors/warnings unless explicitly instructed.**
- **NEVER rename tests, modify test logic to bypass failures, or introduce hacky workarounds without asking first.**
- Use **tabs** instead of spaces for tabs.
- Run **`./gradlew detektAll`** before finishing any Kotlin change set, unless the user scopes validation differently.
- Formatting adherence is a correctness requirement, not a cleanup task. Before every edit, match the file's existing formatting exactly and preserve the alignment style already in use.
- **Compose UI file layout:** keep `@Preview` functions in the **same** `.kt` file as the `@Composable` they preview (usually private, at the bottom). Put **other** top-level types and `@Composable` helpers in **their own** `.kt` files (for example `SignInScreenContent.kt`, preview stubs). Do **not** stack multiple production composables in one screen file, and do **not** move previews into separate preview-only files. Avoid thin call-through composables that only forward args—inline into the screen or extract to a dedicated file when the split helps previews, tests, or reuse. Composables that touch uninitialized platform SDKs in Android Studio (Google/Apple/Facebook auth, etc.) must branch on `LocalInspectionMode.current` and render preview-safe placeholders; `@Preview` call sites should still prefer stubs when they do not need the real widget.

## Additional Agent Guidance

`AGENTS.md` is the entry point for agent instructions in this repository.

Keep detailed, tool-agnostic guidance under:
- `.agents/instructions/` for durable repository rules.
- `.agents/skills/` for task-specific guidance that extends these rules.

Agents should treat those files as extensions of this document when the task matches them.

For scraping, PostgreSQL maintenance, and deleting imported recipes by site, also follow [`scripts/scraping/AGENTS.md`](scripts/scraping/AGENTS.md) (including local `psql` via `sudo -u postgres`).

---

## Project structure

* **app** - Contains the Android entry point to the application.
* **iosApp** - Contains the iOS entry point to the application.
* **umbrella** - Works as an umbrella library for the iosApp, contains the Wasm entry point, plus Jetpack 
  **navigation**.
* **feature** modules (and submodules for **domain**, **data**, and **ui**):
  * **search**
  * **recipe details**
  * **step-by-step cooking**
  * **new recipe**
  * **measurement** (preferences and unit conversion; consumed by search, recipe details, cooking, and settings UI)
  * **auth**, **favorites**, **settings**, **sharing**, and **analytics** also live under `feature/`
* **Submodules** within above features.
  * **domain** - Contains the feature **api interfaces** and optionally **use cases**. Domain depends only on itself and all interaction it does is via _dependency
      inversion_.
  * **data** - Contains the (**db**, **network**, etc) modules.
  * **ui** - Presentation layer
* **shared** - **Shared** domain, UI and data modules specific to this app.
  * **shared/domain** - Contains domain classes shared between backend and apps.
* **base** - Kotlin and Android base classes, reusable in any apps, but not extracted to separate library yet. 

## iOS integration (Gradle + Xcode)

`feature:analytics:data` imports **Firebase Analytics** via Kotlin **SwiftPM import** (`swiftPMDependencies {}` in its `build.gradle.kts`). **Usercentrics**, **Mixpanel**, and **Google Sign-In** are wired from Swift only (`IosAnalyticsNativeBridge` + Swift packages on the iOS app target); Kotlin no longer uses the CocoaPods Gradle plugin for analytics.

The umbrella module uses **direct integration**: Xcode runs `:umbrella:embedAndSignAppleFrameworkForXcode` and links the generated local package `KotlinMultiplatformLinkedPackage` (from `integrateLinkagePackage`). Commit `.swiftpm-locks/` and `iosApp/PurecipesIOSApp/KotlinMultiplatformLinkedPackage/` when SwiftPM dependencies change.

The iOS app uses **Swift Package Manager** for native SDKs: `GoogleSignIn`, `Mixpanel`, `Usercentrics`/`UsercentricsUI`, and `facebook-ios-sdk` (products **FacebookCore** and **FacebookLogin**). Usercentrics ships as local SPM wrappers under `iosApp/PurecipesIOSApp/LocalPackages/` (Bitbucket binary downloads are fetched by `scripts/ios/fetch-usercentrics-xcframeworks.sh` during the Xcode build). Open **`PurecipesIOSApp.xcodeproj`** directly (no CocoaPods workspace).

* **Linux CI and Android-only Gradle runs** skip macOS-only iOS SwiftPM fetch/linkage work when no iOS/Xcode tasks are requested.
* **macOS** runs SwiftPM fetch/linkage and `embedAndSign` when Gradle tasks look like iOS, Xcode, `embedAndSign`, or a full tree build, or during Android Studio/IntelliJ **Gradle sync** (`idea.sync.active`).

## Rule: Naming

Add suffixes to designate types and prefixes for designate the features.

### Data classes

* Ktor and Ktorfit interfaces have the suffix Service.
* Room interfaces have the suffix Dao, databases the suffix DataBase.
* DataSources using the above have the suffixes RemoteDataSource and LocalDataSource.
* The DataSource interfaces have the suffix DataSource, and nested within them are the interfaces Local and Remote.
* The Repository interfaces using the DataSources have the suffix Repository, while their implementations have the suffix Accessor.
* Use cases have the suffix UseCase.
* Put each use case in its own `.kt` file named after the class (for example `GetMeasurementPreferencesUseCase.kt`). Do not group multiple use case classes in one file (for example `MeasurementPreferencesUseCases.kt` or `NotificationPreferencesUseCases.kt`).

### Presentation classes

Top level composables that represent a screen have the Screen suffix.

## Rule: Compose UI file layout

See the **Compose UI file layout** bullet under [Core Principles](#core-principles). In short: previews co-located with the composable they show; every other type or composable gets its own file when it is not the single screen entry composable for that file; platform auth/SDK UI must use `LocalInspectionMode` placeholders so Android Studio preview can render.

## Rule: Use IDE Index plugin (android-studio-index MCP server) whenever possible

If this MCP server is not available, stop all work and notify the user.
If possible find a way to restart in the same request, for example by using ask question tool.

## Rule: GitHub operations (user-github MCP server)

For issues, pull requests, checks, releases, and other GitHub tasks, use the **user-github** MCP server tools. Do **not** use the GitHub CLI (`gh`).
If this MCP server is not available, stop and notify the user instead of falling back to `gh`.

## Rule: Use kotlin-result library

Do not use try/catch structure for network calls or similar. Use the Outcome class instead, which is a type alias 
for the Result class from the kotlin-result library. We use this library because the Result class in the standard library is not a full implementation.

## Rule: Use custom pagination

The Google Pagination library is overcomplicated and inflexible. We use a few custom classes instead.
When using pagination, refer to classes in package 'app.purecipes.shared.ui.component.paging'.

## Rule: Change validation

Validate every change, but match the scope to the task.

For focused maintenance tasks such as fixing Detekt issues, small bug fixes, or adding/changing unit tests, a focused validation is acceptable: run Detekt only, or run the affected old/new tests, or run the smallest relevant build target.

When changes touch test code, test fixtures, fake implementations, or test-only Gradle dependencies, always run the affected module test tasks automatically before finishing the task. Do not skip test execution in those cases.

When changes touch UI code, Compose screens/routes/view models, `androidDeviceTest` sources, or shared test fixtures/fakes used by Android UI tests, also run the Android UI test suite with `./gradlew connectedAndroidTest` before finishing the task. If the environment cannot run instrumented Android tests, explicitly report that as a blocker instead of silently skipping them.

For Kotlin Multiplatform modules, run concrete target test tasks (for example `jvmTest`) instead of non-existent aggregate task names like `commonTest`.

For new features and larger refactors, run complete validation:
- `./gradlew detektAll`
- build the Android app
- build the iOS app
- run the whole test suite

New features should also add new automated tests. Prefer both unit tests and UI tests when UI behavior changes. The repo currently has too little test coverage, and new work should improve that.

## Rule: Multi-step plans (serial review)

When a task is broken into an **ordered multi-step plan** (including a suggested commit split), implement **one step at a time** in order. After each step, **stop**: briefly summarize what changed and where, run validation scoped to that step, and **do not start the next step** until the user has reviewed locally and explicitly asked to continue (so they can commit or adjust before the next slice). Do not implement the entire plan in one uninterrupted pass unless the user explicitly opts out of this workflow for that task.

## Rule: Domain classes

Because we control both the backend and the apps, shared business entities should live in a common domain layer instead of separate DTO layers.

Use `shared/domain` for domain classes that are used by both backend and app code. Do not create duplicate DTOs for the same concept in backend or client modules unless there is a real transport-specific need.

Feature domain modules should contain feature APIs: repository interfaces, use cases, and other feature-facing abstractions. Shared entities returned by those APIs should come from `shared/domain`.

## Rule: Feature layering

Use the feature layers consistently as `data source -> repository -> use case -> presentation`.

Data sources are the place for DTO or DBO to domain mapping, `Outcome` wrapping, threading or execution-context concerns not already handled lower in the stack, and other data-oriented transformations.

Use cases sit between repositories and presentation. They can merge several repository or use-case calls and apply presentation-facing transformations. For now, add a use case for each repository flow even when it is only a pass-through.

## Rule: Comments

Do not add any comments in Kotlin files, unless you are explicitly asked.
If you think it's absolutely necessary, ask for permission.

## Rule: Formatting and Detekt

Reformat code after changes as per the project rules in .idea/codeStyles/Project.xml with the IDE Index plugin (android-studio-index MCP server).
Also format the code according to Detekt rules in config/detekt.
Run detekt with ./gradlew detektAll.
Do not use suppress violations. Try to fix them, or if it cannot be done, 
ask for confirmation if a suppression can be added or a rule can be changed.
Be careful when adding imports or moving classes between packages. Imports not only needed to be updated, 
but also arranged in alphabetical order.
The libraries in the version catalog are also ordered alphabetically by their ids.
Follow Kotlin naming conventions and Detekt naming rules directly in code.
In particular, `const val` names must use `UPPER_SNAKE_CASE`, including private constants in platform-specific source sets.
Ensure every text file you create or edit ends with exactly one trailing newline so Detekt does not report `NewLineAtEndOfFile`.
Formatting must be treated as a hard constraint during editing, especially in `.kt` and `.kts` files.
In Kotlin and Gradle Kotlin DSL files, add exactly one indentation level per block level and keep new sibling entries aligned with existing siblings. Do not add extra indentation for visual grouping or continuation unless the file already does so in that exact location.
Examples:
```kotlin
dependencies {
  api(project(":feature:analytics:data"))
  api(project(":feature:measurement:data"))
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlinx.coroutines.core)
    }
  }
}
```
Keep flat lists flat in `.kts` files, including `export(...)`, `api(...)`, `implementation(...)`, and similar sibling entries.
Examples:
```kotlin
listOf(
  "a",
  "b",
  "c",
)

export(project(":feature:auth:domain"))
export(project(":feature:measurement:domain"))
```
Different file formats keep their own formatting rules. YAML, JSON, Markdown, and other space-indented formats must preserve their native spacing and alignment instead of using Kotlin or Gradle tab rules.
If a Detekt or Kotlin-style issue recurs, add a short repo-specific rule here so future agents do not repeat it.
Reference docs:
https://kotlinlang.org/docs/coding-conventions.html
https://detekt.dev/docs/intro/

## Rule: Centralize compiler opt-ins

Do not scatter repeated `@OptIn(...)` annotations through source files when the whole module needs the same experimental API.
Prefer centralizing those opt-ins in the module build file via compiler arguments so the source stays cleaner and the policy is consistent.

## Rule: Wasm Tooling

Wasm builds use a system-installed Node.js and Yarn Classic.
Do not re-enable Kotlin's automatic Node/Yarn downloads, because this project uses `RepositoriesMode.FAIL_ON_PROJECT_REPOS` and the downloader adds project repositories.
Use Corepack to provision Yarn Classic: `corepack enable` and `corepack prepare yarn@1.22.22 --activate`.
Do not switch the Wasm tooling to Yarn 4+, because the current Kotlin Gradle npm install flow still uses Yarn Classic flags such as `--ignore-scripts`.
If Wasm tasks fail on a machine, first verify `node --version`, `npm --version`, and `yarn --version` are available on `PATH`.
