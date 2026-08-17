#!/usr/bin/env bash
set -euo pipefail

if [ "${OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED:-}" = "YES" ]; then
	echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
	exit 0
fi

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

java_major() {
	local home="$1"
	"$home/bin/java" -version 2>&1 | sed -n 's/.*version "\([0-9]*\).*/\1/p' | head -n 1
}

is_supported_jdk() {
	local home="$1"
	local major
	if [ ! -x "$home/bin/java" ]; then
		return 1
	fi
	major="$(java_major "$home")"
	[ -n "$major" ] && [ "$major" -ge 21 ]
}

JAVA_HOME_CANDIDATE=""

if [ -n "${JAVA_HOME:-}" ] && is_supported_jdk "$JAVA_HOME"; then
	JAVA_HOME_CANDIDATE="$JAVA_HOME"
fi

if [ -z "$JAVA_HOME_CANDIDATE" ]; then
	CANDIDATES=(
		"/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
		"/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
		"$HOME/Applications/Android Studio.app/Contents/jbr/Contents/Home"
		"/Applications/Android Studio.app/Contents/jbr/Contents/Home"
	)
	for cellar in /opt/homebrew/Cellar/openjdk@21/*/libexec/openjdk.jdk/Contents/Home /usr/local/Cellar/openjdk@21/*/libexec/openjdk.jdk/Contents/Home; do
		CANDIDATES+=("$cellar")
	done
	for CANDIDATE in "${CANDIDATES[@]}"; do
		if is_supported_jdk "$CANDIDATE"; then
			JAVA_HOME_CANDIDATE="$CANDIDATE"
			break
		fi
	done
fi

if [ -z "$JAVA_HOME_CANDIDATE" ] && [ -x /usr/libexec/java_home ]; then
	if JAVA_HOME_FROM_UTIL="$(/usr/libexec/java_home -v 21 2>/dev/null)" && is_supported_jdk "$JAVA_HOME_FROM_UTIL"; then
		JAVA_HOME_CANDIDATE="$JAVA_HOME_FROM_UTIL"
	fi
fi

if [ -z "$JAVA_HOME_CANDIDATE" ]; then
	echo "Java 21 or newer is required for the iOS Gradle build." >&2
	echo "Install a JDK 21 (brew install openjdk@21) or use Android Studio's bundled JBR." >&2
	exit 1
fi

PURECIPES_BUILD_TYPE_VALUE="$(printf '%s' "${PURECIPES_BUILD_TYPE:-${CONFIGURATION:-debug}}" | tr '[:upper:]' '[:lower:]')"
export JAVA_HOME="$JAVA_HOME_CANDIDATE"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
cd "$REPO_ROOT"
./gradlew --stop
./gradlew -Dorg.gradle.java.home="$JAVA_HOME" -Ppurecipes.buildType="$PURECIPES_BUILD_TYPE_VALUE" :umbrella:embedAndSignAppleFrameworkForXcode --stacktrace
