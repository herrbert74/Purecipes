# Android release scripts

Kotlin scripts for changelog, versioning, Firebase App Distribution release notes, and opening a release PR.

Full distribution setup: [`docs/releases/android-app-distribution.md`](../../docs/releases/android-app-distribution.md).

Agent workflow (GitHub MCP, local only): [`.agents/instructions/android-release.md`](../../.agents/instructions/android-release.md).

## Scripts

| Script | Role |
|--------|------|
| [`open_android_release_pr.main.kts`](open_android_release_pr.main.kts) | After you draft `CHANGELOG.md`, bump versions and open a review PR via `gh` |
| [`bump_android_version.main.kts`](bump_android_version.main.kts) | Set `versionName` / increment `versionCode` in `gradle/libs.versions.toml` |
| [`extract_release_notes.main.kts`](extract_release_notes.main.kts) | Extract a version section from `CHANGELOG.md` → `build/release-notes.txt` (CI on tag) |

## Prepare a release (local, no CI)

1. Ensure `main` CI is green.
2. With your AI assistant and the **GitHub MCP server**, gather merged PRs since the previous tag. Draft a new `## [version]` section in [`CHANGELOG.md`](../../CHANGELOG.md) using [`.github/prompts/android-release-changelog.md`](../../.github/prompts/android-release-changelog.md). Edit until accurate.
3. Open the PR:

```bash
kotlin scripts/release/open_android_release_pr.main.kts 0.2.0 v0.1.0 true
```

4. Review the PR on GitHub; merge when ready.
5. Tag on `main`: `git tag -a v0.2.0 -m "0.2.0"` and `git push origin v0.2.0`.

## CI

Only **Distribute Android** runs in GitHub Actions (on tag `v*`): extract notes, build release APK, upload to Firebase App Distribution.

## Other local commands

```bash
kotlin scripts/release/bump_android_version.main.kts 0.2.0 true
kotlin scripts/release/extract_release_notes.main.kts 0.2.0
```
