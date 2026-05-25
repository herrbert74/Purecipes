# Android releases (Firebase App Distribution)

Release builds use the **release** variant (`app.purecipes`), [Firebase App Distribution](https://firebase.google.com/docs/app-distribution), and a reviewed root [CHANGELOG.md](../../CHANGELOG.md). The same pipeline applies to any version you ship to testers (including production builds while the product is in an alpha stage).

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
| `PURECIPES_GOOGLE_WEB_CLIENT_ID` | Google Sign-In web client ID (Gradle `BuildConfig`; same value as `client_type` 3 in `google-services.json`, not a secret but stored here for CI) |
| `FIREBASE_SERVICE_ACCOUNT_JSON` | App Distribution upload |
| `GRADLE_ENCRYPTION_KEY` | Optional Gradle build scan cache |

Optional: GitHub Environment **android-release** with required reviewers on the distribute workflow.

## Release order

Never tag before the changelog PR is merged.

1. Ensure `main` CI is green.
2. **Locally (AI assistant + GitHub MCP):** list merged PRs since the previous tag; draft and review `## [version]` in [CHANGELOG.md](../../CHANGELOG.md) (see [prompt](../../.github/prompts/android-release-changelog.md)).
3. **Locally:** bump `versionName` and increment `versionCode` in [gradle/libs.versions.toml](../../gradle/libs.versions.toml) (release APKs use these values):

   ```bash
   kotlin scripts/release/bump_android_version.main.kts 0.2.0 true
   ```

4. **GitHub MCP:** push `CHANGELOG.md` and `gradle/libs.versions.toml` on branch `release/v<version>-changelog` and open a PR to `main` (`create_branch`, `push_files`, `create_pull_request`). Optional: [`open_android_release_pr.main.kts`](../../scripts/release/open_android_release_pr.main.kts) for local `git` push, then `create_pull_request`.
5. Review and merge the PR.
6. Tag on `main`: `git tag -a v0.2.0 -m "0.2.0"` then `git push origin v0.2.0` (optional suffix, e.g. `v0.2.0-rc.1`).
7. **Distribute Android** workflow runs on the tag: extracts [CHANGELOG.md](../../CHANGELOG.md), builds, uploads to Firebase.
8. **alpha-testers** install via [Firebase App Tester](https://firebase.google.com/docs/app-distribution/android/set-up-for-testing).

## Tag naming

- Tags: `v*` (e.g. `v0.2.0`, `v1.0.0-rc.1`).
- `CHANGELOG.md` section header must match semver in the tag: `v0.2.0-rc.1` → section `## [0.2.0]`.

## Local commands

```bash
kotlin scripts/release/bump_android_version.main.kts 0.2.0 true
kotlin scripts/release/open_android_release_pr.main.kts 0.2.0 0.1.0 true
kotlin scripts/release/extract_release_notes.main.kts 0.2.0
```

```bash
./gradlew :app:assembleRelease :app:appDistributionUploadRelease \
  -PfirebaseAppDistribution.serviceCredentialsFile=/path/to/sa.json
```

## Google Play internal testing

- After adopting Firebase App Distribution, do not upload new pre-release builds to Play **Internal testing** unless you intentionally use both channels.
- Keep the Play listing for later closed/open beta and production.
- **alpha-testers** installing from Firebase App Tester should upgrade in place over an older Play internal build when package id and signing match; otherwise uninstall the Play build once, then install from Firebase.
