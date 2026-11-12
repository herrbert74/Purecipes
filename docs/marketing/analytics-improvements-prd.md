# PRD: Analytics Improvements

## Overview

Purecipes has a solid multiplatform analytics foundation (GA4 + Mixpanel behind a common abstraction, Crashlytics via CrashKiOS, Usercentrics consent gating), but coverage is thin: only five events exist, screen views rely on Firebase's automatic Activity tracking (useless in a single-Activity Compose app), there are no global/super properties, and the entire Crashlytics breadcrumb/handled-exception API is wired up yet never called from production code. This PRD captures the current state and defines the improvements.

## Current state (audit findings)

### Architecture

- Layering follows the repo convention: `AnalyticsDataSource` (expect/actual per platform) → `AnalyticsAccessor`/`CrashAccessor`/`ConsentAccessor` → repositories in `feature/analytics/domain` → use cases → view models.
- `AnalyticsAccessor` fans events out to `Ga4AnalyticsDataSource` and `MixpanelAnalyticsDataSource`, gated by `ConsentRepository.currentConsentState().allowsAnalytics()`.
- Events are typed: `AnalyticsEvent` sealed interface with `eventName` + `properties: Map<String, AnalyticsValue>`.
- Existing events: `SearchPerformed`, `RecipeViewed`, `CookingStarted`, `FavoriteChanged`, `RecipeSaved`.
- Tracked from view models: `RecipeSearchViewModel`, `RecipeDetailsViewModel`, `StepByStepCookingViewModel`, `CreateRecipeViewModel`.
- Crash stack exists end to end (`CrashRepository`, `CrashAccessor`, `CrashlyticsDataSource`, use cases `LogBreadcrumbUseCase`, `SendHandledExceptionUseCase`, `SetCrashCustomValueUseCase`, `SetCrashUserIdUseCase`) but **no production call sites exist** for any of them.
- `MainViewModel` sets the analytics user id from the auth session key but never sets the Crashlytics user id.

### Q1: Is debug/release separated?

**No, not meaningfully.**

- `app/src/debug/google-services.json` and `app/src/release/google-services.json` both point to the same Firebase project (`purecipes-50e5c`) and both register `app.purecipes` and `app.purecipes.debug`. Debug builds get a separate GA4 data stream (different application id) but pollute the same Firebase/GA4 property and the same Crashlytics dashboards.
- A single `PURECIPES_MIXPANEL_PROJECT_TOKEN` Gradle property feeds all build types on all platforms — debug, staging, and release traffic land in the same Mixpanel project.
- Nothing gates analytics or Crashlytics on `PurecipesBuildType`; the only gate is consent. `PurecipesConfig.buildType()` exists and is already injected where needed, so the hook point is available.

### Q2: Is Mixpanel and Google Analytics abstracted properly?

**Mostly yes.** Feature code depends only on `TrackEventUseCase` and typed `AnalyticsEvent`s; no vendor types leak above the data sources; each platform (android/ios/jvm/wasmJs) has its own actuals; iOS Mixpanel goes through `IosAnalyticsNativeBridge` to Swift.

Gaps:

- `AnalyticsDataModule` constructs `Ga4AnalyticsDataSource`/`MixpanelAnalyticsDataSource` inline in a hardcoded list; adding/removing a vendor means editing the accessor wiring rather than contributing a binding (Metro multibinding would fit).
- `AnalyticsDataSource` has no concept of screen views or global/super properties, so those cannot be implemented per-vendor idiomatically (GA4 `screen_view` event + default parameters vs Mixpanel `registerSuperProperties`).
- `AnalyticsAccessor.trackEvent` calls `setTrackingEnabled` on every event as a side effect; consent changes should drive enablement reactively (observe `ConsentRepository`) instead.
- No enforcement of GA4 event/param naming constraints (snake_case, length limits) — currently upheld only by convention in `AnalyticsEvent`.

### Q3: Screen views

**Confirmed problem.** There is no `screen_view`/screen tracking anywhere in the codebase. Firebase's automatic screen tracking reports the single Activity, so GA4 sees one screen forever; Mixpanel sees no screens at all. Navigation is Navigation 3 (`NavKey` destinations, `NavDisplay` keyed by tab stack in `MainScreen`), which gives a single choke point to observe.

Screen inventory (NavKey destinations + tab roots):

| Screen name (proposed)   | Source                                            |
|--------------------------|---------------------------------------------------|
| `search`                 | `SearchDestination` (tab root)                    |
| `recipe_details`         | `RecipeDetailsDestination(recipeId)`              |
| `cooking`                | `RecipeCookingDestination(recipeId)`              |
| `favorites`              | `FavoritesDestination`                            |
| `create_recipe`          | `CreateDestination`                               |
| `account`                | `AccountDestination`                              |
| `email_sign_in`          | `EmailSignInDestination`                          |
| `email_registration`     | `EmailRegistrationDestination`                    |
| `settings`               | settings tab root                                 |
| `account_settings`       | `AccountSettingsDestination`                      |
| `about`                  | `AboutDestination`                                |
| `licenses`               | `LicensesDestination`                             |
| `consent_preferences`    | `ConsentPreferencesScreen`                        |

### Q4: Global data points

None exist today. Every event carries only its own ad-hoc properties.

### Q5: Event coverage

Five events for the whole app; auth, favorites-tab usage, sharing, settings/measurement changes, cooking completion, and consent changes are all untracked.

### Q6: Crashlytics breadcrumbs

`enableCrashlytics()` runs at startup on Android/iOS, but `logBreadcrumb`, `sendHandledException`, `setCustomValue`, and `setUserId` are never invoked, so crash reports have no context beyond the stack trace.

## Goals

1. Keep debug/staging traffic out of production analytics and crash dashboards.
2. Track Compose screens as first-class screen views in both GA4 and Mixpanel.
3. Attach consistent global context (build type, platform, user state, screen, origin) to all events.
4. Broaden event coverage across auth, favorites, sharing, cooking, and settings; enrich existing events.
5. Make crash reports diagnosable with breadcrumbs, custom keys, and a user id.

Non-goals: adding new analytics vendors, server-side analytics, A/B testing (see `docs/features/024_feature-flag-ab-testing.md`), replacing Usercentrics consent flow.

## Requirements

### R1: Debug/release separation

- Support per-build-type Mixpanel tokens (`purecipes.mixpanelProjectToken.debug` / `.staging` / `.release` Gradle properties with fallback to the current single property) on all platforms (BuildConfig, BuildKonfig, wasm).
- Use a separate Firebase project for non-release builds with distinct `google-services.json` per build type source set (done; debug no longer duplicates release).
- Add an `environment` global property (`debug`/`staging`/`release`, from `PurecipesConfig.buildType()`) to every event so misrouted traffic remains filterable.
- Keep Crashlytics collection enabled in debug (debug traffic goes to the separate Firebase project).

### R2: Abstraction hardening

- Extend `AnalyticsDataSource` with `trackScreenView(screenName, properties)` and `setGlobalProperties(properties)` so each vendor maps them idiomatically (GA4: `screen_view` event with `screen_name` param + default event parameters/user properties; Mixpanel: named event + `registerSuperProperties`).
- Contribute data sources via Metro multibinding instead of the hardcoded list in `AnalyticsDataModule`.
- Move consent enablement out of the per-event hot path: `AnalyticsAccessor` observes `ConsentRepository` and pushes `setTrackingEnabled` on changes.
- Keep vendor SDK types strictly inside `feature/analytics/data` platform source sets (already true — preserve in review).

### R3: Compose screen view tracking

- Add `AnalyticsEvent.ScreenViewed(screenName, origin)` (or a dedicated `TrackScreenViewUseCase` flowing to `trackScreenView`).
- Introduce a `screenName: String` mapping for every `NavKey` destination and tab root (table in Q3 above).
- Observe back-stack/tab changes at the `MainScreen`/`MainViewModel` level (single choke point; includes tab switches, pushes, and pops resurfacing a screen) and emit exactly one screen view per surface change. Auth/consent screens presented outside the nav stacks are instrumented at their route composables.
- Disable Firebase automatic Activity screen reporting (`google_analytics_automatic_screen_reporting_enabled=false`) so the fake Activity screen stops masking real data.
- Update Crashlytics current-screen custom key on each screen change (see R6).

### R4: Global data points

Global properties attached to every event and screen view (registered once, updated on change):

| Property         | Values / source                                       |
|------------------|-------------------------------------------------------|
| `environment`    | `debug` / `staging` / `release` (`PurecipesConfig`)   |
| `platform`       | `android` / `ios` / `web` / `desktop`                 |
| `app_version`    | `PurecipesConfig.versionName()`                       |
| `user_state`     | `logged_in` / `anonymous` (from `ObserveAuthenticationStateUseCase`; updated in `MainViewModel` alongside `setAnalyticsUserId`) |
| `active_tab`     | current `MainTab`                                     |
| `current_screen` | screen name from R3                                   |

Per-event contextual data point:

- `origin`: where an action was initiated (`search`, `favorites`, `deep_link`, `recipe_details`, `share`, …). Added as an explicit constructor parameter (enum) to navigation-triggered events — `RecipeViewed`, `CookingStarted`, `FavoriteChanged`, `ScreenViewed`.

### R5: New events and richer payloads

Enrich existing events:

- `SearchPerformed`: add applied filters/sort when advanced search lands, `is_empty_result: Boolean`.
- `RecipeViewed`: add `origin`.
- `CookingStarted`: add `origin`, `step_count`.
- `FavoriteChanged`: add `origin`.
- `RecipeSaved`: add `has_photo`, `ingredient_count`, `step_count`.

New events (all snake_case, GA4-safe names):

| Event                  | Properties                                    | Trigger point                          |
|------------------------|-----------------------------------------------|----------------------------------------|
| `sign_in_completed`    | `method` (google/apple/facebook/email)        | auth view models                       |
| `sign_up_completed`    | `method`                                      | registration flow                      |
| `sign_out`             | —                                             | account settings                       |
| `cooking_step_viewed`  | `recipe_id`, `step_index`, `step_count`       | `StepByStepCookingViewModel`           |
| `cooking_completed`    | `recipe_id`, `duration_seconds`               | last-step completion                   |
| `recipe_shared`        | `recipe_id`, `origin`                         | sharing feature                        |
| `measurement_changed`  | `system` (metric/imperial/original)           | measurement preferences                |
| `consent_changed`      | `state` (`ConsentState`)                      | `ConsentViewModel` / consent refresh   |
| `recipe_load_failed`   | `recipe_id`, `error_kind`                     | details/search error paths             |

Rules: no PII in properties (no emails, no free-text user content beyond the existing search query), ids are numeric recipe ids, every new event gets a sealed `AnalyticsEvent` subtype plus unit test coverage in `AnalyticsEventTest`.

### R6: Crashlytics breadcrumbs and context

- Log a breadcrumb on every screen change (`screen: <name>`) from the R3 choke point via `LogBreadcrumbUseCase`.
- Log breadcrumbs for key user actions: search performed, recipe opened, cooking started/step advanced, recipe save attempted, sign-in attempted.
- Call `SetCrashUserIdUseCase` alongside `setAnalyticsUserId` in `MainViewModel` (session key, not email).
- Set custom keys: `current_screen`, `active_tab`, `environment`, `user_state` via `SetCrashCustomValueUseCase`.
- Report caught `Outcome` failures that are swallowed today (network/db errors surfaced as UI error states) through `SendHandledExceptionUseCase` so non-fatal issues become visible.
- Breadcrumbs must never contain query text or user content — screen names, ids, and enum values only.

## Implementation plan (serial steps, one commit each)

1. **Build-type separation** — per-build-type Mixpanel tokens (Android BuildConfig, iOS/wasm BuildKonfig, umbrella Gradle), distinct `google-services.json` for debug, `environment` derivation from `PurecipesConfig` (Crashlytics stays enabled in debug against the debug Firebase project). Validation: `detektAll`, Android debug+release assemble.
2. **Abstraction hardening** — extend `AnalyticsDataSource` (screen views, global properties), Metro multibinding for data sources, reactive consent enablement in `AnalyticsAccessor`. Validation: `feature:analytics` unit tests (`jvmTest`), `detektAll`.
3. **Screen view tracking** — destination→screen-name mapping, back-stack observation in `MainViewModel`/`MainScreen`, `ScreenViewed` event, disable automatic Activity reporting, unit tests for mapping and dedup behavior. Validation: main module tests + `connectedAndroidTest`.
4. **Global properties** — global property provider, `user_state` + `active_tab` + `current_screen` updates, `origin` parameter on navigation-triggered events, update existing call sites and tests.
5. **New events** — add sealed subtypes + call sites per R5, extend `AnalyticsEventTest`, per-feature view model tests.
6. **Crashlytics context** — breadcrumbs, custom keys, crash user id, handled-exception reporting in error paths, tests with fake `CrashRepository`.

Each step follows the repo's serial-review rule: implement, validate, stop for review before the next step.

## Acceptance criteria

- Debug builds send no data to the production Mixpanel project or production Firebase project; every event carries `environment`.
- Navigating Search → Recipe Details → Cooking produces three distinct screen views in both GA4 (as `screen_view`) and Mixpanel, with correct `origin`.
- All events carry `user_state`, `platform`, `app_version`, `environment`; toggling sign-in flips `user_state` on subsequent events.
- Crash reports show the screen trail as breadcrumbs, `current_screen`/`environment` custom keys, and a user id for signed-in sessions.
- No vendor SDK type appears outside `feature/analytics/data`; consent denial still suppresses all tracking (existing `AnalyticsAccessorTest` behavior preserved).
- New/changed events covered by unit tests; screen tracking covered by UI test.

## Risks and open questions

- **Firebase project split**: debug Firebase project and Android `google-services.json` are in place; iOS `GoogleService-Info.plist` for debug may still need provisioning if iOS debug should leave the production project.
- **Mixpanel free-tier limits**: separate debug project may be preferable to filtering by `environment` to protect quota.
- **Event volume**: `cooking_step_viewed` can be high-frequency; consider sampling or only tracking first/last steps if volume becomes a cost issue.
- **Wasm/desktop parity**: jvm data sources are no-ops today; decide whether desktop should remain untracked.
- **Screen-view semantics on back navigation**: emit on every resurface (proposed) vs. only on forward navigation — needs product decision before step 3.
