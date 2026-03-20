# AI Agent Development Guidelines

Purecipes is a Kotlin Multiplatform project with Android, iOS and WasmJS targets, and a Postgres/ktor backend. The 
primary goal of the app to make it easier for users to follow recipe instructions, and find new recipes.

## Core Principles

- Use **tabs** instead of spaces for tabs.

## Additional Agent Guidance

`AGENTS.md` is the entry point for agent instructions in this repository.

Keep detailed, tool-agnostic guidance under:
- `.agents/instructions/` for durable repository rules.
- `.agents/skills/` for task-specific guidance that extends these rules.

Agents should treat those files as extensions of this document when the task matches them.

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
* **Submodules** within above features.
  * **domain** - Contains the feature **api interfaces** and optionally **use cases**. Domain depends only on itself and all interaction it does is via _dependency
      inversion_.
  * **data** - Contains the (**db**, **network**, etc) modules.
  * **ui** - Presentation layer
* **shared** - **Shared** domain, UI and data modules specific to this app.
  * **shared/domain** - Contains domain classes shared between backend and apps.
* **base** - Kotlin and Android base classes, reusable in any apps, but not extracted to separate library yet. 

## Rule: Naming

Add suffixes to designate types and prefixes for designate the features.

### Data classes

* Ktor and Ktorfit interfaces have the suffix Service.
* Room interfaces have the suffix Dao, databases the suffix DataBase.
* DataSources using the above have the suffixes RemoteDataSource and LocalDataSource.
* The DataSource interfaces have the suffix DataSource, and nested within them are the interfaces Local and Remote.
* The Repository interfaces using the DataSources have the suffix Repository, while their implementations have the suffix Accessor.
* Use cases have the suffix UseCase.

### Presentation classes

Top level composables that represent a screen have the Screen suffix.

## Rule: Use kotlin-result library

Do not use try/catch structure for network calls or similar. Use the Outcome class instead, which is a type alias 
for the Result class from the kotlin-result library. We use this library because the Result class in the standard library is not a full implementation.

## Rule: Use custom pagination

The Google Pagination library is overcomplicated and inflexible. We use a few custom classes instead.
When using pagination, refer to classes in package 'com.purecipes.shared.ui.component.paging'.

## Rule: Change validation

Validate every change, but match the scope to the task.

For focused maintenance tasks such as fixing Detekt issues, small bug fixes, or adding/changing unit tests, a focused validation is acceptable: run Detekt only, or run the affected old/new tests, or run the smallest relevant build target.

For new features and larger refactors, run complete validation:
- `./gradlew detektAll`
- build the Android app
- build the iOS app
- run the whole test suite

New features should also add new automated tests. Prefer both unit tests and UI tests when UI behavior changes. The repo currently has too little test coverage, and new work should improve that.

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

Reformat code after changes as per the project rules in .idea/codeStyles/Project.xml.
Also format the code according to Detekt rules in config/detekt.
Run detekt with ./gradlew detektAll.
Do not use suppress violations. Try to fix them, or if it cannot be done, 
ask for confirmation if a suppression can be added or a rule can be changed.
Be careful when adding imports or moving classes between packages. Imports not only needed to be updated, 
but also arranged in alphabetical order.
The libraries in the version catalog are also ordered alphabetically by their ids.
Follow Kotlin naming conventions and Detekt naming rules directly in code.
In particular, `const val` names must use `UPPER_SNAKE_CASE`, including private constants in platform-specific source sets.
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
