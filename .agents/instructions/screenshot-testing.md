# Compose preview screenshot testing

Purecipes uses AGP Compose Preview Screenshot Testing configured in `app/build.gradle.kts` under
`android.testOptions.screenshotTests`.

## Suites

| Suite | Source set | Reference directory | Purpose |
| --- | --- | --- | --- |
| `componentScreenshot` | `app/src/componentScreenshot/kotlin/` | `app/src/componentScreenshotDefaultDebug/reference/` | UI regression tests for design-system components |
| `storeScreenshot` | `app/src/storeScreenshot/kotlin/` | `app/src/storeScreenshotDefaultDebug/reference/` | Play Store marketing captures |

## Commands

```bash
# Update component screenshot baselines
./gradlew :app:updateDebugComponentScreenshotDefaultTestSuite

# Validate component screenshots against baselines
./gradlew :app:testDebugComponentScreenshotDefaultTestSuite

# Update Play Store marketing captures
./gradlew :app:updateDebugStoreScreenshotDefaultTestSuite
```

## Writing component screenshot tests

- Annotate test composables with `@PreviewTest` (`com.android.tools.screenshot.PreviewTest`) and
  `@Preview` (`androidx.compose.ui.tooling.preview.Preview`).
- Wrap any component that loads `AsyncImage` with `ScreenshotCoilPreview`
  (`app/src/componentScreenshot/kotlin/app/purecipes/screenshot/ScreenshotCoilPreview.kt`) so
  Coil 3 renders deterministic placeholder bitmaps without network calls.
- Commit updated reference images under `app/src/componentScreenshotDefaultDebug/reference/`
  whenever intentional UI changes alter component rendering.
