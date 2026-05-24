# Android release preparation (local)

Changelog drafting is **local only** (your AI assistant + GitHub MCP). CI only **distributes** on tag push.

## Draft CHANGELOG with GitHub MCP

1. Identify `previous_tag` (e.g. `0.1.0`) and new `version` (e.g. `0.2.0`).
2. Use the **GitHub MCP server** to list merged pull requests on `main` since that tag (e.g. `search_pull_requests`, `list_commits` on the compare range).
3. Follow [`.github/prompts/android-release-changelog.md`](../../.github/prompts/android-release-changelog.md) and draft a section in root [`CHANGELOG.md`](../../CHANGELOG.md):

   ```markdown
   ## [0.2.0] - YYYY-MM-DD

   ### Added
   - …
   ```

4. Review and edit until accurate.

Do not add OpenAI or other LLM API keys to GitHub Actions for changelog drafting.

## Bump Android version

Release builds read `versionName` and `versionCode` from [`gradle/libs.versions.toml`](../../gradle/libs.versions.toml) (`:app` `defaultConfig`). **Update both in the release PR** before tagging:

- Set `versionName` to the new semver (e.g. `0.2.0`).
- Increment `versionCode` by 1 (monotonic; required for Firebase App Distribution and Google Play).

Helper:

```bash
kotlin scripts/release/bump_android_version.main.kts 0.2.0 true
```

Pass `false` as the second argument to set `versionName` without bumping `versionCode`.

## Open the release PR with GitHub MCP

Never commit release prep directly to `main`. The release PR must include **`CHANGELOG.md`** (new `## [version]` section) and **`gradle/libs.versions.toml`** (updated `versionName` / `versionCode`).

1. Sync with `main` (stash or commit unrelated work first).
2. Bump versions (see above) if not already done.
3. Create branch `release/v<version>-changelog` from `main` (GitHub MCP `create_branch`, or local `git checkout -b`).
4. Push `CHANGELOG.md` and `gradle/libs.versions.toml` on that branch (GitHub MCP `push_files`, or local `git commit` + `git push`).
5. Open a PR to `main` with GitHub MCP `create_pull_request`:
   - **Title:** `Release <version> changelog`
   - **Body:** version, previous tag, and review checklist (changelog wording, `versionCode` / `versionName`, merge before tagging).

Optional: [`open_android_release_pr.main.kts`](../../scripts/release/open_android_release_pr.main.kts) can validate the changelog section, bump versions, and push the branch with local `git`; use GitHub MCP `create_pull_request` to open the PR.

## After merge

Tag on `main` (e.g. `v0.2.0`) to run [`.github/workflows/distribute-android.yml`](../../.github/workflows/distribute-android.yml).
