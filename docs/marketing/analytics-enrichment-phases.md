# Analytics Enrichment Phases

Plans for making Purecipes analytics usable for product and marketing decisions.
Phases are ordered by analytical ROI and should be implemented one at a time under the repo serial-review rule.

Related docs: [`analytics-strategy.md`](analytics-strategy.md), [`../../analytics-improvements-prd.md`](../../analytics-improvements-prd.md).

## Shared conventions

- Property keys: `snake_case`, GA4-safe lengths.
- Recipe identity pair on every recipe-scoped event: `recipe_id` (number) + `recipe_name` (string title).
- Prefer enums / catalogue labels over free-text user content. Search query is the only free-text exception already in production.
- No PII (email, display name, auth tokens).
- Keep a shared helper (for example `recipeAnalyticsProperties`) so all call sites emit the same keys.
- Every new/changed `AnalyticsEvent` subtype needs coverage in `AnalyticsEventTest` plus call-site view-model tests.
- After each phase: update Mixpanel Lexicon descriptions and refresh / extend dashboards.

---

## Phase 1 — Recipe identity everywhere

### Goal

Make recipe details, favorites, cooking, and sharing readable in Mixpanel/GA4 without joining an external recipe catalogue.

### Problem today

- `recipe_viewed`, `favorite_changed`, cooking events, `recipe_shared`, `recipe_saved`, and `recipe_load_failed` carry `recipe_id` but not `recipe_name`.
- Screen views for `recipe_details` / `cooking` carry neither id nor name.
- Analysts cannot answer “which recipes matter?” from the dashboard alone.

### Event / property changes

| Event / surface | Add |
|---|---|
| `recipe_viewed` | `recipe_name`, optional content dims below |
| `favorite_changed` | `recipe_name`, same content dims when available |
| `cooking_started` | `recipe_name` |
| `cooking_step_viewed` | `recipe_name` |
| `cooking_completed` | `recipe_name` |
| `recipe_shared` | `recipe_name` |
| `recipe_saved` | `recipe_name` (saved title) |
| `recipe_load_failed` | `recipe_name` when already loaded; omit if unknown |
| Screen view `recipe_details` | `recipe_id` (from destination); `recipe_name` after load via content event or delayed param strategy |
| Screen view `cooking` | same as details |

**Recommended content dimensions** (from `RecipeDetails` when loaded):

- `cuisine`
- `total_time_minutes`
- `difficulty_level`
- `meal_type`
- `cooking_method`
- `is_favorite` (state at event time)
- `has_nutrition` (boolean) and/or `calorie_range`

Minimum viable Phase 1: **`recipe_id` + `recipe_name` only**. Content dims can ship in the same PR if cheap, or as a Phase 1b follow-up.

### Call sites

- `RecipeDetailsViewModel` — has `RecipeDetails` at track time for view / favorite / share / load-failed.
- `StepByStepCookingViewModel` — has details after load for cooking events.
- `CreateRecipeViewModel` — has saved title for `recipe_saved`.
- Screen-view choke point (`TrackActiveScreenViews` / destination mapping) — pass `recipeId` from `RecipeDetailsDestination` / `RecipeCookingDestination`; name may require a secondary content event if unavailable at navigation time.

### Validation

- Unit: `AnalyticsEventTest` property maps.
- Unit: view-model tests assert name is forwarded.
- Manual: Mixpanel Live View shows `recipe_name` on view/favorite/cook.
- Dashboard: top recipes by views / favorites / cooks (by name).

### Acceptance

- Any recipe-scoped event in Mixpanel is human-readable without an ID lookup.
- Top-N recipe charts work on `recipe_name`.

### Out of scope

- New favorites-tab / cookbook / premium events (Phases 3–4).
- Search filter payload (Phase 2).

---

## Phase 2 — Search: why results happen

### Goal

Explain empty searches, filter value, pantry usage, and near-miss engagement.

### Problem today

`search_performed` only sends `query`, `query_length`, `result_count`, `is_empty_result`. Filters, pantry, and key ingredients are invisible even though they drive search quality and premium gating.

### Event / property changes

**Enrich `search_performed`:**

| Property | Type | Notes |
|---|---|---|
| `has_query` | bool | derived |
| `has_filters` | bool | any recipe filter selected |
| `filter_count` | number | count of selected filter values |
| `cuisines` | string / list | compact joined labels or list |
| `meal_types` | string / list | |
| `cooking_time_ranges` | string / list | |
| `difficulty_levels` | string / list | |
| `cooking_methods` | string / list | |
| `dietary_preferences` | string / list | |
| `calorie_ranges` | string / list | premium |
| `nutrition_filters` | string / list | premium |
| `pantry_count` | number | |
| `excluded_count` | number | |
| `key_ingredient_count` | number | premium |
| `near_miss_count` | number | first page |
| `premium_filters_applied` | bool | |
| `is_premium_user` | bool | snapshot at search time |

Prefer compact comma-joined strings if GA4 list params are awkward; keep Mixpanel-friendly list values if the data source supports them cleanly.

**Optional companion events (if volume/clarity needs them):**

| Event | Properties | Trigger |
|---|---|---|
| `filter_sheet_opened` | `origin` | open filters |
| `filters_applied` | same filter summary as search | dismiss sheet with changes |
| `near_miss_recipe_clicked` | `recipe_id`, `recipe_name`, `missing_ingredient` | tap near-miss card |

### Call sites

- `RecipeSearchViewModel` search path (already fires `search_performed` on first page).
- Filter sheet open/dismiss handlers if companion events are included.
- Near-miss click handler in search results UI / VM.

### Validation

- Empty-result rate segmented by `has_filters` / `pantry_count` / `has_query`.
- Mixpanel breakdowns for cuisine / time / dietary.
- Unit tests for property serialization of multi-value filters.

### Acceptance

- Product can answer: “Do filters reduce empty results?” and “Is pantry search used?”
- Premium filter usage is measurable before paywall instrumentation (Phase 4).

### Out of scope

- Cookbook / favorites-tab events.
- Paywall events.

---

## Phase 3 — Favorites and cookbooks

### Goal

Illuminate the favorites tab and cookbook organize → share → import loop (currently dark).

### Problem today

- `favorite_changed` only fires from recipe details.
- `FavoritesViewModel` has no analytics dependency.
- Cookbook create / open / delete / share / import have no events.

### New events

| Event | Properties | Trigger |
|---|---|---|
| `favorites_tab_selected` | `tab` (`saved_recipes` / `cookbooks` / `my_recipes`) | tab switch in favorites |
| `cookbook_created` | `cookbook_id`, `cookbook_name` | create cookbook |
| `cookbook_opened` | `cookbook_id`, `cookbook_name`, `recipe_count` (if known) | open cookbook detail |
| `cookbook_deleted` | `cookbook_id` | delete |
| `recipe_added_to_cookbook` | `recipe_id`, `recipe_name`, `cookbook_id`, `cookbook_name?`, `origin` | add from details picker or favorites |
| `cookbook_shared` | `cookbook_id`, `cookbook_name?`, `origin` | share cookbook |
| `cookbook_import_completed` | `imported_recipe_count`, `cookbook_id?` | successful import |
| `cookbook_import_failed` | `error_kind` | failed import |

### Enrich existing

- Ensure `favorite_changed` includes Phase 1 identity fields and can use `origin=favorites` if unfavorite/add is ever done from the favorites surface.
- `recipe_shared` vs cookbook share: add `share_type` (`recipe` / `cookbook`) when both exist.

### Call sites

- `FavoritesViewModel` (tabs, cookbook CRUD, open, import).
- `RecipeDetailsViewModel` cookbook picker (`addRecipeToCookbookId`, `createCookbookAndAdd`).
- Sharing use cases / UI for cookbook share.

### Validation

- Funnel: `recipe_viewed` → `favorite_changed(is_favorite=true)` → `favorites` screen → `cookbook_created` / `recipe_added_to_cookbook`.
- Import funnel: `cookbook_shared` → `cookbook_import_completed`.
- View-model unit tests for each new event.

### Acceptance

- Favorites tab engagement is visible.
- Cookbook virality (share → import) is measurable.

### Out of scope

- Premium / paywall (Phase 4).
- Cooking abandonment (Phase 5).

---

## Phase 4 — Monetization funnel

### Goal

Measure free → paywall → purchase and which gated features drive upgrades.

### Problem today

- No `premium_status` global property.
- Paywall UI (`PaywallViewModel` / `PaywallScreen`) has no analytics events.
- Strategy doc lists `premium_upgrade_*` / `premium_feature_used` but none are implemented.

### Global property

| Property | Values | Source |
|---|---|---|
| `premium_status` | `free` / `premium` | subscription entitlements observer (same place as other globals in `MainViewModel`) |

### New events

| Event | Properties | Trigger |
|---|---|---|
| `paywall_viewed` | `feature`, `origin` | paywall shown |
| `premium_upgrade_started` | `plan?`, `feature`, `origin` | user starts purchase |
| `premium_upgrade_completed` | `plan?`, `feature?` | purchase success |
| `premium_upgrade_failed` | `error_kind`, `feature?` | purchase failure |
| `restore_purchases_completed` | `result` (`restored` / `nothing_to_restore` / `failed`) | restore |
| `premium_feature_blocked` | `feature`, `origin` | free user hits gated control (filters, etc.) |

Suggested `feature` values: `calorie_filter`, `nutrition_filter`, `key_ingredients`, `settings_paywall`, `ads_removal` (extend as gates appear).

### Call sites

- `PaywallViewModel` / RevenueCat purchase flow.
- Search filter premium gates.
- Settings monetization entry points.
- `MainViewModel` (or equivalent) for `premium_status` global updates.

### Validation

- Funnel: `premium_feature_blocked` → `paywall_viewed` → `premium_upgrade_started` → `premium_upgrade_completed`.
- Breakdown of paywall views by `feature`.
- Segment core engagement by `premium_status`.

### Acceptance

- Conversion rate free → premium is reportable.
- Highest-intent gated features are identifiable.

### Out of scope

- Ads impression economics beyond a simple blocked/paywall path (optional Phase 5).
- Server-side revenue reconciliation (RevenueCat / Play Console remain source of truth for money).

---

## Phase 5 — Funnel glue and quality

### Goal

Close remaining gaps for completion quality, acquisition, and optional commercial signals.

### Enrichments

| Event | Add |
|---|---|
| `cooking_completed` | `step_count`, `origin` (if available), keep `duration_seconds` + Phase 1 name |
| `recipe_shared` | `share_type`, Phase 1 name |
| Auth events | optional `is_new_user` when detectable |

### New events (optional but useful)

| Event | Properties | Trigger |
|---|---|---|
| `cooking_abandoned` | `recipe_id`, `recipe_name`, `last_step_index`, `step_count`, `duration_seconds` | leave cooking without completing (debounce / screen dispose) |
| `deep_link_opened` | `link_type` (`recipe` / `cookbook`), `recipe_id?`, `token_present?` | `MainViewModel.onDeepLink` |
| `ad_impression` | `placement` | banner/interstitial shown |
| `ad_clicked` | `placement` | ad click if SDK exposes it |

### Quality / Lexicon hygiene

- Verify events in Mixpanel Lexicon; hide noisy automatic screen-name duplicates if they collide with custom events.
- Document high-cardinality warnings: `recipe_name` and `query` are intentional; avoid adding ingredient free-text lists.
- Revisit `cooking_step_viewed` volume (sample or first/last only if cost spikes).

### Validation

- Completion rate: `cooking_started` → `cooking_completed`.
- Abandonment distribution by `last_step_index / step_count`.
- Deep-link → `recipe_viewed` with `origin=deep_link`.

### Acceptance

- Core discovery → cook → complete funnel is fully instrumented with readable recipe identity.
- Acquisition via shares/deep links is attributable.
- Dashboards from Phases 1–4 remain accurate after enrichments.

---

## Suggested implementation order and commit slices

1. **Phase 1** — shared recipe property helper + enrich existing events + tests.
2. **Phase 2** — `search_performed` enrichment (+ optional companions).
3. **Phase 3** — favorites/cookbook events.
4. **Phase 4** — `premium_status` + paywall funnel.
5. **Phase 5** — abandonment, deep-link, ads, Lexicon cleanup.

After each phase: run scoped module tests, `./gradlew detektAll` for Kotlin changes, and update Mixpanel boards to use new properties.

## Mixpanel boards

Created in the **Purecipes** production project (`4009723`, EU). Until Phase 1 ships `recipe_name`, top-recipe charts break down by `recipe_id`.

| Board | URL | Charts now | Lights up after |
|---|---|---|---|
| Purecipes Product Overview | https://eu.mixpanel.com/project/4009723/app/boards#id=11441256 | WAU, core event volume, screen views, users by platform | Phases 2–4 for richer segments |
| Purecipes Recipe Engagement | https://eu.mixpanel.com/project/4009723/app/boards#id=11441271 | Engagement trend, top viewed (`recipe_id`), views by origin, favorite add/remove | Phase 1 (`recipe_name`), Phase 3 (cookbooks) |
| Purecipes Search and Funnels | https://eu.mixpanel.com/project/4009723/app/boards#id=11441295 | Empty search rate, discover→cook funnel, auth state, cooking completion funnel | Phase 2 (filters/pantry), Phase 5 (abandonment) |
| Purecipes Favorites and Cooking | https://eu.mixpanel.com/project/4009723/app/boards#id=11441296 | Top favorited (`recipe_id`), avg cook duration, cook completion funnel, shares/load failures | Phase 1 (`recipe_name`), Phase 3 (cookbooks), Phase 5 |
| Monetization (planned) | — | — | Phase 4 |
