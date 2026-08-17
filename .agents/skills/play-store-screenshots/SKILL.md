# Skill: Play Store screenshots

Use this skill when generating, updating, framing, or uploading Google Play listing screenshots for Purecipes (`app.purecipes`).

Human-facing command/credential notes: [`tools/store-screenshots/README.md`](../../../tools/store-screenshots/README.md).

## Goal

Produce store-ready framed PNGs from real Compose UI captures, then optionally upload them with the Android Publisher API. Do not use paid screenshot SaaS or third-party agent skills for this.

## Pipeline

Run in order:

1. **Capture raw UI** — `./gradlew :app:updateDebugScreenshotTest`
2. **Frame assets** — `./gradlew generateStoreScreenshots`
3. **Dry-run upload** — `./gradlew uploadStoreScreenshots -PdryRun`
4. **Upload** — `./gradlew uploadStoreScreenshots` (only when the user asks to publish)

Default language is **en-GB**. Override with `-Planguage=en-US` (or `-Ppurecipes.play.language=…`) on generate and upload.

Outputs: `store-listing/{phone|tablet-7|tablet-10|feature-graphic}/en-GB/*.png` (or the overridden language).

Sizes: phone 1080×1920, 7" tablet 1200×1920, 10" tablet 1600×2560, feature graphic 1024×500 (feature graphic is generated from the first slide only).

## Key paths

| Role | Path |
|------|------|
| Raw `@PreviewTest` captures | `app/src/screenshotTest/kotlin/app/purecipes/marketing/MarketingScreenshots.kt` |
| Raw reference PNGs | `app/src/screenshotTestDebug/reference/app/purecipes/marketing/MarketingScreenshotsKt/` |
| Slide copy + themes | `tools/store-screenshots/.../MarketingSlides.kt` |
| Frame layout | `tools/store-screenshots/.../MarketingFrame.kt` |
| Frame generator | `./gradlew generateStoreScreenshots` |
| Play upload | `./gradlew uploadStoreScreenshots` |

## Adding or changing a slide

1. Prefer a public `*ScreenContent` composable with fake data and no ViewModel (see `RecipeSearchScreenContent`, `RecipeDetailsScreenContent`, `StepByStepCookingScreenContent`). Put previews in the same file as the composable they show; put Content helpers in their own `.kt` file.
2. Add a `@PreviewTest` + `@Preview(device = MARKETING_DEVICE, …)` in `MarketingScreenshots.kt`. Device spec: `spec:width=1080px,height=2340px,dpi=440`.
3. Register the slide in `MarketingSlides.kt` with:
   - `fileName` (`01.png`, `02.png`, …)
   - title / optional subtitle (one outcome per slide; pass the one-second thumbnail test)
   - `rawScreenshotNamePrefix` matching the preview function + preview name (e.g. `SearchMarketingScreenshot_search`) — do not hardcode content-hash suffixes
   - `theme` (`ROSE`, `GOLD`, or `DEEP`) — vary adjacent slides
4. Run capture, then generate. Open the framed PNGs and iterate on copy/layout if needed.
5. Run `./gradlew detektAll` after Kotlin changes. If UI/Content code changed, also run the relevant `connectedAndroidTest` / full suite per AGENTS.md.

## Design rules

- Screenshots are ads, not documentation.
- One clear user outcome per slide.
- Headlines must read in a store thumbnail.
- Feature graphic uses slide 01 only; phone/tablet get the full set.
- Raw captures may use local debug drawables via Coil `LocalAsyncImagePreviewHandler` (`MarketingCoilPreview` + `app/src/debug/res/drawable-nodpi/marketing_*.jpg`). Do not rely on network URLs in screenshot tests.

## Credentials

Use the **Play Console** service account (same JSON as Cursor’s play-store MCP).

Resolution order for upload: `--credentials`, then `-Ppurecipes.play.serviceAccountJson`, then `GOOGLE_APPLICATION_CREDENTIALS`.

Never commit service account JSON or absolute machine paths into the repo or skill docs.

If upload returns `The caller does not have permission`, invite the service account in Play Console with **Manage store presence** on `app.purecipes`, enable **Google Play Android Developer API**, and **save the invite twice** if permissions were just changed. Details: [`tools/store-screenshots/README.md`](../../../tools/store-screenshots/README.md).

## Checklist

- raw `@PreviewTest` updated and `updateDebugScreenshotTest` succeeded
- `MarketingSlides` prefixes match generated reference filenames
- `generateStoreScreenshots` wrote expected files under `store-listing/`
- agent reviewed framed PNGs (read the image files)
- `detektAll` clean for Kotlin edits
- dry-run upload lists the expected assets before a real upload
- real upload only when explicitly requested
