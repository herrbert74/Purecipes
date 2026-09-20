#!/usr/bin/env bash
set -euo pipefail

if [ "${SCRIPT_INPUT_FILE_0:-}" = "" ] && [ "${PROJECT_DIR:-}" != "" ]; then
	GOOGLE_SERVICE_INFO="${PROJECT_DIR}/PurecipesIOSApp/GoogleService-Info.plist"
else
	GOOGLE_SERVICE_INFO="${SCRIPT_INPUT_FILE_0:-}"
fi

UPLOAD_SYMBOLS=""
SEARCH_ROOTS=(
	"${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk"
	"${PROJECT_DIR}/KotlinMultiplatformLinkedPackage"
	"${SRCROOT}/KotlinMultiplatformLinkedPackage"
)

for root in "${SEARCH_ROOTS[@]}"; do
	if [ -d "${root}" ]; then
		found="$(find "${root}" -name upload-symbols -type f 2>/dev/null | head -n 1 || true)"
		if [ -n "${found}" ]; then
			UPLOAD_SYMBOLS="${found}"
			break
		fi
	fi
done

if [ -z "${UPLOAD_SYMBOLS}" ] || [ ! -f "${GOOGLE_SERVICE_INFO}" ]; then
	echo "warning: Crashlytics upload-symbols skipped (script or GoogleService-Info.plist not found)."
	exit 0
fi

if [ ! -d "${DWARF_DSYM_FOLDER_PATH:-}" ]; then
	echo "warning: Crashlytics upload-symbols skipped (dSYM folder missing)."
	exit 0
fi

"${UPLOAD_SYMBOLS}" -gsp "${GOOGLE_SERVICE_INFO}" -p ios "${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}"
