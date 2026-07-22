# Android releases (Firebase App Distribution)

Release builds use the **release** variant (`app.purecipes`), [Firebase App Distribution](https://firebase.google.com/docs/app-distribution), and a reviewed root [CHANGELOG.md](../../CHANGELOG.md). The same pipeline applies to any version you ship to testers (including production builds while the product is in an alpha stage). Release builds use the RevenueCat Google Play key, are not debuggable, and do not show monetisation debug overrides.

Release scripts: [`scripts/release/README.md`](../../scripts/release/README.md).

Prepare releases locally with the GitHub MCP server: [`.agents/instructions/android-release.md`](../../.agents/instructions/android-release.md).

## One-time Firebase Console setup

1. [Firebase Console](https://console.firebase.google.com/) → project **purecipes-50e5c** → **App Distribution** → Get started.
2. Confirm Android app **app.purecipes** is listed (see `app/src/release/google-services.json`).
3. Create tester group **alpha-testers** and add emails for people who test pre-production builds (core team and close associates during the alpha stage; the group can stay in use for later pre-release builds).
4. Create a CI service account (Google Cloud → IAM):
   - Role: **Firebase App Distribution Admin** (or **Firebase Admin**).
   - Download JSON key → GitHub secret `FIREBASE_SERVICE_ACCOUNT_JSON`.

## GitHub secrets (distribute workflow only)

| Secret | Purpose |
|--------|---------|
| `PURECIPES_SIGNING_STORE_FILE_BASE64` | Release keystore |
| `PURECIPES_SIGNING_STORE_PASSWORD` | Keystore password |
| `PURECIPES_SIGNING_KEY_ALIAS` | Key alias |
| `PURECIPES_SIGNING_KEY_PASSWORD` | Key password |
| `PURECIPES_GOOGLE_WEB_CLIENT_ID` | Google Sign-In web client ID (Gradle `BuildConfig`; same value as `client_type` 3 in release `google-services.json`, not a secret but stored here for CI). Non-debug fallback only; debug uses `purecipes.googleWebClientId.debug` / built-in debug default. |
| `PURECIPES_FIREBASE_PROJECT_ID` | Optional for distribute; backend release packaging uses this as the non-debug Firebase project fallback (default `purecipes-50e5c`). Local dual-build uses `purecipes.firebaseProjectId.debug` / `.release` instead. |
| `PURECIPES_MIXPANEL_PROJECT_TOKEN` | Mixpanel project token for release builds (Gradle `BuildConfig` via env fallback; use the release Mixpanel project) |
| `PURECIPES_REVENUECAT_API_KEY` | RevenueCat Google Play SDK key (`goog_…`; passed as `purecipes.revenueCatApiKey.release`) |
| `PURECIPES_ADMOB_APP_ID` | Production AdMob app ID (`ca-app-pub-…~…`; Gradle `BuildConfig` + AndroidManifest placeholder) |
| `PURECIPES_ADMOB_BANNER_AD_UNIT_ID` | Production AdMob banner ad unit ID (`ca-app-pub-…/…`) |
| `PURECIPES_ADMOB_INTERSTITIAL_AD_UNIT_ID` | Production AdMob interstitial ad unit ID (`ca-app-pub-…/…`) |
| `FIREBASE_SERVICE_ACCOUNT_JSON` | App Distribution upload |
| `GRADLE_ENCRYPTION_KEY` | Optional Gradle build scan cache |

Optional: GitHub Environment **android-release** with required reviewers on the distribute workflow.

## Release order

Never tag before the changelog PR is merged.

1. **Prepare the release PR locally.** Follow [`scripts/release/README.md`](../../scripts/release/README.md) and [`.agents/instructions/android-release.md`](../../.agents/instructions/android-release.md): draft the `## [version]` section in [CHANGELOG.md](../../CHANGELOG.md), bump `versionName` / `versionCode`, and regenerate the open source license definitions. The PR on branch `release/v<version>-changelog` includes `CHANGELOG.md`, `gradle/libs.versions.toml`, and the regenerated `aboutlibraries.json` files (`app/src/main/res/raw/` and `feature/settings/ui/src/commonMain/composeResources/files/`).
2. Review and merge the PR.
3. Tag on `main`: `git tag -a v0.2.0 -m "0.2.0"` then `git push origin v0.2.0` (optional suffix, e.g. `v0.2.0-rc.1`).
4. **Distribute Android** workflow runs on the tag: extracts [CHANGELOG.md](../../CHANGELOG.md), builds, uploads to Firebase.
5. **alpha-testers** install via [Firebase App Tester](https://firebase.google.com/docs/app-distribution/android/set-up-for-testing).

## Tag naming

- Tags: `v*` (e.g. `v0.2.0`, `v1.0.0-rc.1`).
- `CHANGELOG.md` section header must match semver in the tag: `v0.2.0-rc.1` → section `## [0.2.0]`.

## Local commands

Release-prep commands (changelog, version bump, license export, release PR) live in [`scripts/release/README.md`](../../scripts/release/README.md). To build and upload a release APK manually:

```bash
./gradlew :app:assembleRelease :app:appDistributionUploadRelease \
  -PfirebaseAppDistribution.serviceCredentialsFile=/path/to/sa.json
```

## Google Play internal testing

- After adopting Firebase App Distribution, do not upload new pre-release builds to Play **Internal testing** unless you intentionally use both channels.
- Keep the Play listing for later closed/open beta and production.
- **alpha-testers** installing from Firebase App Tester should upgrade in place over an older Play internal build when package id and signing match; otherwise uninstall the Play build once, then install from Firebase.
