# Play Store screenshots

Local pipeline for framed Google Play listing screenshots (`app.purecipes`).

Agent workflow: [`.agents/skills/play-store-screenshots/SKILL.md`](../../.agents/skills/play-store-screenshots/SKILL.md).

| Step | Command |
|------|---------|
| Capture raw UI | `./gradlew :app:updateDebugScreenshotTest` |
| Frame store assets | `./gradlew generateStoreScreenshots` |
| Preview upload plan | `./gradlew uploadStoreScreenshots -PdryRun` |
| Upload to Play | `./gradlew uploadStoreScreenshots` |

Default listing language is **en-GB** (Play Console default). Override with `-Planguage=en-US` (or `-Ppurecipes.play.language=…`) on generate and upload.

Outputs land in `store-listing/` (`phone`, `tablet-7`, `tablet-10`, `feature-graphic`, locale folder matching the language).

## Credentials

`uploadStoreScreenshots` uses the Google Play Android Publisher API with the **Play Console** service account JSON (same as Cursor’s play-store MCP).

Resolution order:

1. `--credentials /path/to/play-sa.json` (main class directly)
2. Gradle `-Ppurecipes.play.serviceAccountJson=/path/to/play-sa.json`
3. Environment variable `GOOGLE_APPLICATION_CREDENTIALS`

```bash
./gradlew uploadStoreScreenshots \
  -Ppurecipes.play.serviceAccountJson=/path/to/play-sa.json
```

Never commit service account JSON or absolute machine paths into the repo.

### Play Console permissions

If upload returns `403 The caller does not have permission`:

1. Enable **Google Play Android Developer API** on the Cloud project that owns the key.
2. Link that project in Play Console → **Setup → API access**.
3. Invite the service account (`client_email` in the JSON) under **Users and permissions**.
4. Grant app access for **Purecipes** (`app.purecipes`) with at least **View app information** and **Manage store presence**.
5. **Save twice** — Play Console often requires a second save after changing app permissions before the invite sticks. Wait a minute, then retry.

## Slide copy and themes

Edit `src/main/kotlin/app/purecipes/store/screenshots/MarketingSlides.kt`. Raw captures are `@PreviewTest` composables in `app/src/screenshotTest/.../MarketingScreenshots.kt`.

Food photos for those captures live in `app/src/debug/res/drawable-nodpi/marketing_*.jpg` and are wired through `MarketingCoilPreview` (Coil preview handler). They ship only with debug builds, not release.
