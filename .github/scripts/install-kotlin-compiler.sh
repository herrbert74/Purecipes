#!/usr/bin/env bash
set -euo pipefail

KOTLIN_VERSION="${KOTLIN_VERSION:-2.3.21}"
INSTALL_DIR="${RUNNER_TEMP:-/tmp}/kotlin-compiler"

if [[ -x "${INSTALL_DIR}/kotlinc/bin/kotlin" ]]; then
	echo "${INSTALL_DIR}/kotlinc/bin" >> "${GITHUB_PATH}"
	exit 0
fi

curl -sSL "https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip" \
	-o "${INSTALL_DIR}.zip"
mkdir -p "${INSTALL_DIR}"
unzip -q "${INSTALL_DIR}.zip" -d "${INSTALL_DIR}"
echo "${INSTALL_DIR}/kotlinc/bin" >> "${GITHUB_PATH}"
