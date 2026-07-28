# Scripts

Kotlin scripts and small shell helpers for local and CI workflows. Each area has its own folder and README.

| Folder | Purpose | Documentation |
|--------|---------|---------------|
| [`scraping/`](scraping/) | Recipe website scraping and Postgres import | [`scraping/README.md`](scraping/README.md) |
| [`release/`](release/) | Changelog (local + GitHub MCP), release PR, Firebase notes | [`release/README.md`](release/README.md) |
| [`ios/`](ios/) | iOS-only helpers (Usercentrics xcframework fetch) | — |

Agent guidelines: [`scraping/AGENTS.md`](scraping/AGENTS.md), [`release/AGENTS.md`](release/AGENTS.md).

Full Android distribution (Firebase + Play): [`../docs/releases/android-app-distribution.md`](../docs/releases/android-app-distribution.md).

Play Store screenshots (generate + upload, credentials shared with Play MCP): [`../tools/store-screenshots/README.md`](../tools/store-screenshots/README.md).

Play Store AAB upload (closed testing / other tracks): [`../tools/play-publish/README.md`](../tools/play-publish/README.md).
