# Release scripts — agent guidelines

Guidelines for Android release automation under [`scripts/release/`](.).

## Rule: Android release scripts use Kotlin

Release automation lives in `scripts/release/*.main.kts`. Prefer Kotlin scripts over bash. CI installs the Kotlin compiler via [`.github/scripts/install-kotlin-compiler.sh`](../../.github/scripts/install-kotlin-compiler.sh) only for the distribute workflow (`extract_release_notes`).

## Rule: Prepare releases locally with GitHub MCP

- Draft `CHANGELOG.md` locally using the GitHub MCP server and [`.github/prompts/android-release-changelog.md`](../../.github/prompts/android-release-changelog.md).
- Do not add changelog LLM API keys or OpenAI calls to GitHub Actions.
- After the maintainer reviews the changelog section, run [`open_android_release_pr.main.kts`](open_android_release_pr.main.kts) to open a PR (never commit release prep directly to `main`).

See [`.agents/instructions/android-release.md`](../../.agents/instructions/android-release.md).

## Rule: Changelog before distribute

- `CHANGELOG.md` at the repo root is the source of truth for tester-facing notes.
- `extract_release_notes` runs on tag push in CI; distribution must fail if the version section is missing.
