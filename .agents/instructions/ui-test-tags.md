# Selective Android UI tests

Instrumented UI tests under `**/androidDeviceTest` are tagged with marker annotations in
`app.purecipes.shared.testfixtures.uitest` so local runs and CI can execute a subset.

## Annotations and PR labels

Use the same names in code and on GitHub PR labels:

| Tag | Meaning |
| --- | --- |
| `UiTestEssential` | Smoke suite (always included when any feature tag is selected) |
| `UiTestAuth` | Auth / sign-in |
| `UiTestCooking` | Step-by-step cooking |
| `UiTestFeatureRequests` | Feature requests |
| `UiTestHome` | Home feed |
| `UiTestLibrary` | Library / favorites / cookbooks |
| `UiTestMain` | Main shell / navigation integration |
| `UiTestNewRecipe` | Create recipe |
| `UiTestOnboarding` | Onboarding |
| `UiTestRecipeDetails` | Recipe details |
| `UiTestSearch` | Search / filters |
| `UiTestSettings` | Settings |
| `UiTestSubscription` | Subscription / paywall panels |
| `UiTestAll` | Full suite (no annotation filter; PR label or Gradle tag only) |

Tag each test class with its feature annotation. Mark a small smoke method (or class) with
`@UiTestEssential` in addition when it belongs in the default PR suite.

## Local runs

```bash
# Default PR-style smoke suite
./gradlew connectedAndroidTest -PuiTestTags=UiTestEssential

# Essential plus one feature
./gradlew connectedAndroidTest -PuiTestTags=UiTestSearch

# Multiple features (Essential is added automatically)
./gradlew connectedAndroidTest -PuiTestTags=UiTestSearch,UiTestLibrary

# Full suite
./gradlew connectedAndroidTest
# or
./gradlew connectedAndroidTest -PuiTestTags=UiTestAll
```

`-PuiTestTags` is resolved in `settings.gradle.kts` into
`android.testInstrumentationRunnerArguments.annotation`.

## CI behavior

Workflow: `.github/workflows/build-test-androidtest.yml`

- Push to `main`: `UiTestAll`
- Pull request with no `UiTest*` labels: `UiTestEssential`
- Pull request with `UiTest*` labels: those labels (Essential is unioned in unless `UiTestAll`)
- `workflow_dispatch` input `ui_test_tags`: defaults to `UiTestAll`

Create matching labels on the GitHub repo (`UiTestEssential`, `UiTestSearch`, …, `UiTestAll`).

## Verification

`.github/scripts/verify-connected-ui-tests.sh` uses `UI_TEST_TAGS` (default `UiTestAll`) and
`.github/scripts/count-ui-tests-for-tags.py` so filtered CI runs do not require every module’s full suite.
