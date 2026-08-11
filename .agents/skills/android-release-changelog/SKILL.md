# Skill: Android release changelog

Use this skill when drafting tester-facing release notes for an Android build, and when preparing the Android release PR.

You write user-facing Android release notes for the Purecipes recipe app (testers in the alpha stage and later pre-release builds).

Use the GitHub MCP server to list merged pull requests on `main` since the previous release tag. Do not call OpenAI or other paid APIs from CI for this task.

## Changelog draft

Rules:
- Write a new `## [version] - YYYY-MM-DD` section at the top of root `CHANGELOG.md` (after the Keep a Changelog intro). Do not only print notes in chat.
- Under that header, use Keep a Changelog sections: Added, Changed, Fixed, Removed (omit empty sections).
- Use bullet lists under each section heading (`### Added`, etc.).
- Write for testers, not developers: no PR numbers, no internal codenames, no "misc fixes".
- Merge related pull requests into concise bullets.
- Maximum about 4000 characters.
- Do not invent features not supported by the pull request summaries.

## Versioning

Purecipes uses **semver** (`MAJOR.MINOR.PATCH`) for `versionName`.

- Choose a **minor** bump for the usual release (new or improved user-facing behaviour since the previous tag).
- Choose a **patch** bump only when the release is a **bug-fix-only** set of changes.
- Do not invent a major bump unless the user explicitly asks for one.

Also update **both** values in `gradle/libs.versions.toml`:

- Set `versionName` to the new semver (e.g. `0.8.0`).
- Increment `versionCode` by 1 (monotonic; required for Firebase App Distribution and Google Play).

Helper:

```bash
kotlin scripts/release/bump_android_version.main.kts 0.8.0 true
```

Pass `false` as the second argument to set `versionName` without bumping `versionCode`.

## Prepare release branch, commit, and PR

After `CHANGELOG.md` and versions are ready, prepare the release on a branch (never commit release prep directly to `main`). Prefer:

```bash
kotlin scripts/release/open_android_release_pr.main.kts 0.8.0 v0.7.1 true
```

That script validates the changelog section, bumps versions if needed, regenerates `aboutlibraries.json`, creates branch `release/v<version>-changelog`, and commits with:

```
Prepare Android release <version>
```

Example: `Prepare Android release 0.8.0`.

Then open the PR with GitHub MCP `create_pull_request` (not `gh`):

- **Title:** `Release <version> changelog`
- **Base:** `main`
- **Head:** `release/v<version>-changelog`
- **Body:** use `build/release-pr-body.md` from the script (version, previous tag, review checklist for changelog wording and `versionCode` / `versionName`).

Do not tag until the PR is merged.
