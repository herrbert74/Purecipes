# Android release scripts

Kotlin scripts for changelog, versioning, Firebase App Distribution release notes, and opening a release PR.

Full distribution setup: [`docs/releases/android-app-distribution.md`](../../docs/releases/android-app-distribution.md).

Agent workflow (GitHub MCP, local only): [`.agents/instructions/android-release.md`](../../.agents/instructions/android-release.md).

## Scripts

| Script | Role |
|--------|------|
| [`bump_android_version.main.kts`](bump_android_version.main.kts) | Set `versionName` / increment `versionCode` in `gradle/libs.versions.toml` |
| [`open_android_release_pr.main.kts`](open_android_release_pr.main.kts) | Validate changelog, bump versions, regenerate open source license definitions, commit, and push branch `release/v<version>-changelog` (open PR via GitHub MCP) |
| [`extract_release_notes.main.kts`](extract_release_notes.main.kts) | Extract a version section from `CHANGELOG.md` → `build/release-notes.txt` (CI on tag) |

## Prepare a release (local, no CI)

1. Ensure `main` CI is green.
2. With your AI assistant and the **GitHub MCP server**, gather merged PRs since the previous tag. Draft a new `## [version]` section in [`CHANGELOG.md`](../../CHANGELOG.md) using [`.agents/skills/android-release-changelog/SKILL.md`](../../.agents/skills/android-release-changelog/SKILL.md). Edit until accurate.
3. Bump `versionName` and increment `versionCode` in [`gradle/libs.versions.toml`](../../gradle/libs.versions.toml) (release builds read both from this file):

   ```bash
   kotlin scripts/release/bump_android_version.main.kts 0.2.0 true
   ```

4. Open PR `release/v<version>-changelog` with GitHub MCP (`create_branch`, `push_files`, `create_pull_request`), or push with local `git` after:

   ```bash
   kotlin scripts/release/open_android_release_pr.main.kts 0.2.0 0.1.0 true
   ```

   This step also runs `:app:exportLibraryDefinitions` and `:feature:settings:ui:exportLibraryDefinitions`, so the open source license definitions shipped in the About screen are refreshed and committed with the version bump. No `aboutlibraries` Android-specific Gradle plugin is required.

   Then use GitHub MCP `create_pull_request` (see suggested body in `build/release-pr-body.md`).

5. Review the PR on GitHub; merge when ready.
6. Tag on `main`: `git tag -a v0.2.0 -m "0.2.0"` and `git push origin v0.2.0`.

## CI

**Distribute Android** runs in GitHub Actions on tag `v*`: extract notes, build the release AAB (native debug symbols embedded), archive the AAB and R8 `mapping.txt` as workflow artifacts, upload the AAB to Firebase App Distribution (Play App Signing via Firebase ↔ Play link), and upload the same AAB plus R8 mapping to Google Play closed testing (`alpha`).

## Other local commands

```bash
kotlin scripts/release/bump_android_version.main.kts 0.2.0 true
kotlin scripts/release/extract_release_notes.main.kts 0.2.0
```
