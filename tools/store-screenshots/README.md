# Play Store screenshots

Local pipeline for framed Google Play listing screenshots (`app.purecipes`).

| Step | Command |
|------|---------|
| Capture raw UI | `./gradlew :app:updateDebugScreenshotTest` |
| Frame store assets | `./gradlew generateStoreScreenshots` |
| Preview upload plan | `./gradlew uploadStoreScreenshots -PdryRun` |
| Upload to Play | `./gradlew uploadStoreScreenshots` |

Outputs land in `store-listing/` (`phone`, `tablet-7`, `tablet-10`, `feature-graphic`, locale `en-US`).

## Credentials (same as Play MCP)

`uploadStoreScreenshots` uses the **Google Play** Android Publisher API. It needs the **Play Console service account** JSON — the same one Cursor’s **play-store** MCP uses (`GOOGLE_APPLICATION_CREDENTIALS` in that MCP’s env).

### Do not confuse with Firebase

| Credential | Used for |
|------------|----------|
| Play Console service account JSON | Play MCP, `uploadStoreScreenshots`, Play Console API |
| Firebase App Distribution service account | Uploading APKs to Firebase App Distribution only |

Both can be exposed as `GOOGLE_APPLICATION_CREDENTIALS` in different contexts. For screenshot upload, always use the **Play** key.

### How the Gradle task finds credentials

First match wins:

1. `--credentials /path/to/play-sa.json` (if you run the main class directly)
2. Gradle `-Ppurecipes.play.serviceAccountJson=/path/to/play-sa.json`
3. Environment variable `GOOGLE_APPLICATION_CREDENTIALS`

Easiest local setup (matches MCP):

```bash
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/play-sa.json
./gradlew uploadStoreScreenshots -PdryRun
./gradlew uploadStoreScreenshots
```

Or one-shot without exporting:

```bash
./gradlew uploadStoreScreenshots \
  -Ppurecipes.play.serviceAccountJson=/path/to/play-sa.json
```

The service account must be invited in Play Console (Users and permissions) with access to edit store listing / use the Google Play Android Developer API for `app.purecipes`.

## Slide copy and themes

Edit `src/main/kotlin/app/purecipes/store/screenshots/MarketingSlides.kt`. Raw captures are `@PreviewTest` composables in `app/src/screenshotTest/.../MarketingScreenshots.kt`.
