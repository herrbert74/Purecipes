# Release scripts — agent guidelines

Guidelines for Android release automation under [`scripts/release/`](.).

## Rule: Android release scripts use Kotlin

Release automation lives in `scripts/release/*.main.kts`. Prefer Kotlin scripts over bash. CI installs the Kotlin compiler via [`.github/scripts/install-kotlin-compiler.sh`](../../.github/scripts/install-kotlin-compiler.sh) only for the distribute workflow (`extract_release_notes`).

## Rule: Prepare releases locally with GitHub MCP

- Draft `CHANGELOG.md` locally using the GitHub MCP server and [`.agents/skills/android-release-changelog/SKILL.md`](../../.agents/skills/android-release-changelog/SKILL.md).
- Bump `versionName` and increment `versionCode` in `gradle/libs.versions.toml` (see [`bump_android_version.main.kts`](bump_android_version.main.kts)); release builds use these values.
- Open the release PR with GitHub MCP (`create_branch`, `push_files`, `create_pull_request`). Never commit release prep directly to `main`.
- Do not add changelog LLM API keys or OpenAI calls to GitHub Actions.

See [`.agents/instructions/android-release.md`](../../.agents/instructions/android-release.md).

## Rule: Changelog before distribute

- `CHANGELOG.md` at the repo root is the source of truth for tester-facing notes.
- `extract_release_notes` runs on tag push in CI; distribution must fail if the version section is missing.
