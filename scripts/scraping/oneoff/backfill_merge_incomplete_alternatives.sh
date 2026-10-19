#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
cd "$ROOT_DIR"

# Bust kotlin-main-kts jars that can pin a stale ScrapedIngredientLines.kt import.
find "$HOME/.kotlin-script" -name 'backfill_merge_incomplete_alternatives.main.kts*.jar' -delete 2>/dev/null || true
find /var/folders -path '*kotlin-script*' -name 'backfill_merge_incomplete_alternatives.main.kts*.jar' -delete 2>/dev/null || true

# Cache-bust when ScrapedIngredientLines.kt changes (invalidates kotlin-main-kts jars).
# backfill-merge-incomplete-v2

exec kotlin scripts/scraping/oneoff/backfill_merge_incomplete_alternatives.main.kts "$@"
