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
readonly SDK_DIR="$OUTPUT_DIR/Usercentrics.xcframework"
readonly UI_DIR="$OUTPUT_DIR/UsercentricsUI.xcframework"

xcframework_ready() {
	local dir="$1"
	[[ -f "$dir/Info.plist" ]] || return 1
	find "$dir" \( -name '*.framework' -o -name '*.a' \) -print -quit | grep -q .
}

mkdir -p "$OUTPUT_DIR"

if xcframework_ready "$SDK_DIR" && xcframework_ready "$UI_DIR"; then
	echo "Usercentrics xcframeworks already present at $OUTPUT_DIR"
	exit 0
fi

rm -rf "$SDK_DIR" "$UI_DIR"

tmpdir="$(mktemp -d)"
trap 'rm -rf "$tmpdir"' EXIT

curl -fL "$SDK_URL" -o "$tmpdir/$SDK_ZIP"
curl -fL "$UI_URL" -o "$tmpdir/$UI_ZIP"
unzip -q -o "$tmpdir/$SDK_ZIP" -d "$OUTPUT_DIR"
unzip -q -o "$tmpdir/$UI_ZIP" -d "$OUTPUT_DIR"

if ! xcframework_ready "$SDK_DIR" || ! xcframework_ready "$UI_DIR"; then
	echo "Fetched Usercentrics archives but xcframeworks are still incomplete at $OUTPUT_DIR" >&2
	exit 1
fi

echo "Fetched Usercentrics xcframeworks to $OUTPUT_DIR"
