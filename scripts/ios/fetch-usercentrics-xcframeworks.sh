#!/usr/bin/env bash
set -euo pipefail

readonly VERSION="2.25.1"
readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
readonly OUTPUT_DIR="$REPO_ROOT/iosApp/PurecipesIOSApp/LocalPackages/XCFrameworks"
readonly SDK_ZIP="Usercentrics-${VERSION}.xcframework.zip"
readonly UI_ZIP="UsercentricsUI-${VERSION}.xcframework.zip"
readonly SDK_URL="https://bitbucket.org/usercentricscode/usercentrics-spm-sdk/downloads/${SDK_ZIP}"
readonly UI_URL="https://bitbucket.org/usercentricscode/usercentrics-spm-ui/downloads/${UI_ZIP}"

mkdir -p "$OUTPUT_DIR"

if [[ -d "$OUTPUT_DIR/Usercentrics.xcframework" && -d "$OUTPUT_DIR/UsercentricsUI.xcframework" ]]; then
	echo "Usercentrics xcframeworks already present at $OUTPUT_DIR"
	exit 0
fi

tmpdir="$(mktemp -d)"
trap 'rm -rf "$tmpdir"' EXIT

curl -fL "$SDK_URL" -o "$tmpdir/$SDK_ZIP"
curl -fL "$UI_URL" -o "$tmpdir/$UI_ZIP"
unzip -q -o "$tmpdir/$SDK_ZIP" -d "$OUTPUT_DIR"
unzip -q -o "$tmpdir/$UI_ZIP" -d "$OUTPUT_DIR"

echo "Fetched Usercentrics xcframeworks to $OUTPUT_DIR"
