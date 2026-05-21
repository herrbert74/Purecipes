# Android release preparation (local)

Changelog drafting is **local only** (your AI assistant + GitHub MCP). CI only **distributes** on tag push.

## Draft CHANGELOG with GitHub MCP

1. Identify `previous_tag` (e.g. `v0.1.0`) and new `version` (e.g. `0.2.0`).
2. Use the **GitHub MCP server** to list merged pull requests on `main` since that tag (e.g. `list_pull_requests`, `search_pull_requests`, or `list_commits` on the compare range).
3. Follow [`.github/prompts/android-release-changelog.md`](../../.github/prompts/android-release-changelog.md) and add a section to root [`CHANGELOG.md`](../../CHANGELOG.md):

   ```markdown
   ## [0.2.0] - YYYY-MM-DD

   ### Added
   - …
   ```

4. Review and edit the section on **`main`** (commit or stash other work first; the open-PR script checks out `main` and pulls).

Do not add OpenAI or other LLM API keys to GitHub Actions for changelog drafting.

## Open the release PR locally

After `CHANGELOG.md` on `main` has a non-empty `## [version]` section:

```bash
kotlin scripts/release/open_android_release_pr.main.kts 0.2.0 v0.1.0 true
```

`open_android_release_pr` bumps `versionName` / `versionCode`, commits `CHANGELOG.md` and `gradle/libs.versions.toml`, pushes branch `release/v<version>-changelog`, and runs `gh pr create`.

Requires a clean enough `main` checkout, `git`, and `gh` authenticated for the repo.

## After merge

Tag on `main` (e.g. `v0.2.0`) to run [`.github/workflows/distribute-android.yml`](../../.github/workflows/distribute-android.yml).
