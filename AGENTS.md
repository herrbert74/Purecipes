# AI Agent Development Guidelines

Purecipes is a Kotlin Multiplatform project with Android, iOS and WasmJS targets, and a Postgres/ktor backend. The 
primary goal of the app to make it easier for users to follow recipe instructions, and find new recipes.

## Core Principles

- Use **tabs** instead of spaces for tabs.

---

## Project structure

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

## Rule: Naming

Add suffixes to designate types and prefixes for designate the features.

### Data classes

* Ktor and Ktorfit interfaces have the suffix Service.
* Room interfaces have the suffix Dao, databases the suffix DataBase.
* DataSources using the above have the suffixes RemoteDataSource and LocalDataSource.
* The DataSource interfaces have the suffix DataSource, and nested within them are the interfaces Local and Remote.
* The Repository interfaces using the DataSources have the suffix Repository, while their implementations have the suffix Accessor.

### Presentation classes

Top level composables that represent a screen have the Screen suffix.

## Rule: Use kotlin-result library

Do not use try/catch structure for network calls or similar. Use the Outcome class instead, which is a type alias 
for the Result class from the kotlin-result library. We use this library because the Result class in the standard library is not a full implementation.

## Rule: Use custom pagination

The Google Pagination library is overcomplicated and inflexible. We use a few custom classes instead.
When using pagination, refer to classes in package 'com.purecipes.shared.ui.component.paging'.

## Rule: DTOs and domain classes

Do not change the names of DTO parameters to follow Kotlin naming patterns. Instead, keep the API names and 
suppress detekt with @Suppress("PropertyName", "ConstructorParameterNaming"), but only if needed, typically 
when the API uses underscores.
Use the correct pattern only in the domain model classes.

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
