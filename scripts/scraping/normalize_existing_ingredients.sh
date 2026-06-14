#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
SCRIPT_PATH="$REPO_ROOT/scripts/scraping/normalize_existing_ingredients.main.kts"

for cache_dir in \
	"${HOME}/Library/Caches/main.kts.compiled.cache" \
	"${HOME}/.cache/main.kts.compiled.cache" \
	"${HOME}/.kotlin/main.kts.compiled.cache"
do
	if [[ -d "$cache_dir" ]]; then
		rm -rf "$cache_dir"
	fi
done

exec kotlin "$SCRIPT_PATH" "$@"
