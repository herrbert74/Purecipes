# Android releases (Firebase App Distribution + Google Play)

Release builds use the **release** variant (`app.purecipes`), [Firebase App Distribution](https://firebase.google.com/docs/app-distribution) (APK for testers), and a Google Play **closed testing** upload (AAB, API track `alpha`). Notes come from a reviewed root [CHANGELOG.md](../../CHANGELOG.md). Release builds use the RevenueCat Google Play key, are not debuggable, and do not show monetisation debug overrides.

Release scripts: [`scripts/release/README.md`](../../scripts/release/README.md).

Prepare releases locally with the GitHub MCP server: [`.agents/instructions/android-release.md`](../../.agents/instructions/android-release.md).

Play AAB upload tool: [`tools/play-publish/README.md`](../../tools/play-publish/README.md).

## One-time Firebase Console setup

1. [Firebase Console](https://console.firebase.google.com/) → project **purecipes-50e5c** → **App Distribution** → Get started.
2. Confirm Android app **app.purecipes** is listed (see `app/src/release/google-services.json`).
3. Create tester group **alpha-testers** and add emails for people who test pre-production builds (core team and close associates during the alpha stage; the group can stay in use for later pre-release builds).
4. Create a CI service account (Google Cloud → IAM):
   - Role: **Firebase App Distribution Admin** (or **Firebase Admin**).
   - Download JSON key → GitHub secret `FIREBASE_SERVICE_ACCOUNT_JSON`.

## One-time Google Play API setup

1. Use the same Play Console service account as screenshot uploads / the play-store MCP (JSON → GitHub secret `PLAY_SERVICE_ACCOUNT_JSON`).
2. Enable **Google Play Android Developer API** and link the Cloud project in Play Console → **Setup → API access**.
3. Grant the service account app access for **Purecipes** with at least **View app information** and **Release to testing tracks**.
4. In Play Console, create a **closed testing** track and add tester emails (or a Google Group). Testers must accept the Play invite separately from Firebase App Distribution.

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
| `PLAY_SERVICE_ACCOUNT_JSON` | Google Play AAB upload (Publisher API) |
| `GRADLE_ENCRYPTION_KEY` | Optional Gradle build scan cache |

Optional: GitHub Environment **android-release** with required reviewers on the distribute workflow.

## Release order

Never tag before the changelog PR is merged.

1. **Prepare the release PR locally.** Follow [`scripts/release/README.md`](../../scripts/release/README.md) and [`.agents/instructions/android-release.md`](../../.agents/instructions/android-release.md): draft the `## [version]` section in [CHANGELOG.md](../../CHANGELOG.md), bump `versionName` / `versionCode`, and regenerate the open source license definitions. The PR on branch `release/v<version>-changelog` includes `CHANGELOG.md`, `gradle/libs.versions.toml`, and the regenerated `aboutlibraries.json` files (`app/src/main/res/raw/` and `feature/settings/ui/src/commonMain/composeResources/files/`).
2. Review and merge the PR.
3. Tag on `main`: `git tag -a v0.2.0 -m "0.2.0"` then `git push origin v0.2.0` (optional suffix, e.g. `v0.2.0-rc.1`).
4. **Distribute Android** workflow runs on the tag: extracts [CHANGELOG.md](../../CHANGELOG.md), builds APK + AAB, uploads APK to Firebase, uploads AAB to Play closed testing.
5. **alpha-testers** install via [Firebase App Tester](https://firebase.google.com/docs/app-distribution/android/set-up-for-testing). Closed testers install from the Play Store after accepting the Play invite.

## Tag naming

- Tags: `v*` (e.g. `v0.2.0`, `v1.0.0-rc.1`).
- `CHANGELOG.md` section header must match semver in the tag: `v0.2.0-rc.1` → section `## [0.2.0]`.

## Local commands

Release-prep commands (changelog, version bump, license export, release PR) live in [`scripts/release/README.md`](../../scripts/release/README.md).

Firebase App Distribution (APK):

```bash
./gradlew :app:assembleRelease :app:appDistributionUploadRelease \
  -PfirebaseAppDistribution.serviceCredentialsFile=/path/to/firebase-sa.json
```

Google Play closed testing (AAB):

```bash
./gradlew :app:bundleRelease :tools:play-publish:uploadPlayRelease \
  -Ppurecipes.play.serviceAccountJson=/path/to/play-sa.json \
  -Ppurecipes.play.releaseName=0.2.0
```

## Tester channels

- **Firebase App Distribution** and **Play closed testing** are separate invites and install sources. Prefer one channel per tester group; do not ping-pong the same people between them for the same release.
- Firebase still uploads an **APK** until Play review / published track status allows switching App Distribution to **AAB** (Firebase ↔ Play link).
- Play closed installs use Google Play App Signing; Firebase installs use your upload key. Switching channels may require uninstalling once.
