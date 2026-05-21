# Scripts

Kotlin scripts and small shell helpers for local and CI workflows. Each area has its own folder and README.

| Folder | Purpose | Documentation |
|--------|---------|---------------|
| [`scraping/`](scraping/) | Recipe website scraping and Postgres import | [`scraping/README.md`](scraping/README.md) |
| [`release/`](release/) | Changelog (local + GitHub MCP), release PR, Firebase notes | [`release/README.md`](release/README.md) |

Other files at this level:

- [`pod-wrapper.sh`](pod-wrapper.sh) — CocoaPods wrapper used by iOS builds (not documented here).

Agent guidelines: [`scraping/AGENTS.md`](scraping/AGENTS.md), [`release/AGENTS.md`](release/AGENTS.md).
