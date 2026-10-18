#!/usr/bin/env bash
set -euo pipefail

UI_TEST_TAG_PACKAGE="app.purecipes.shared.testfixtures.uitest"

KNOWN_TAGS="UiTestEssential UiTestAuth UiTestCooking UiTestFeatureRequests UiTestHome UiTestLibrary UiTestMain UiTestNewRecipe UiTestOnboarding UiTestRecipeDetails UiTestSearch UiTestSettings UiTestSubscription"

usage() {
	echo "Usage: $0 <comma-separated-tags>" >&2
	echo "       $0 --from-labels <label1> <label2> ..." >&2
	echo "Prints comma-separated fully-qualified annotation names for AndroidJUnitRunner," >&2
	echo "or prints nothing when the selection means run all tests (UiTestAll / empty)." >&2
}

is_known_tag() {
	local candidate="$1"
	local known
	for known in ${KNOWN_TAGS}; do
		if [[ "${candidate}" == "${known}" ]]; then
			return 0
		fi
	done
	return 1
}

contains_tag() {
	local needle="$1"
	shift
	local item
	for item in "$@"; do
		if [[ "${item}" == "${needle}" ]]; then
			return 0
		fi
	done
	return 1
}

resolve_tags() {
	local requested=""
	local token
	for token in "$@"; do
		token="$(printf '%s' "${token}" | tr ',' ' ')"
		local part
		for part in ${token}; do
			part="$(printf '%s' "${part}" | tr -d '[:space:]')"
			[[ -z "${part}" ]] && continue
			requested="${requested} ${part}"
		done
	done

	# shellcheck disable=SC2086
	set -- ${requested}
	if [[ $# -eq 0 ]]; then
		return 0
	fi

	local selected=""
	local tag
	for tag in "$@"; do
		if [[ "${tag}" == "UiTestAll" ]]; then
			return 0
		fi
		if ! is_known_tag "${tag}"; then
			echo "Unknown UI test tag: ${tag}" >&2
			echo "Known tags: ${KNOWN_TAGS} UiTestAll" >&2
			exit 1
		fi
		if ! contains_tag "${tag}" ${selected}; then
			selected="${selected} ${tag}"
		fi
	done

	if ! contains_tag "UiTestEssential" ${selected}; then
		selected="UiTestEssential ${selected}"
	fi

	local ordered=""
	for tag in ${KNOWN_TAGS}; do
		if contains_tag "${tag}" ${selected}; then
			if [[ -n "${ordered}" ]]; then
				ordered="${ordered},"
			fi
			ordered="${ordered}${UI_TEST_TAG_PACKAGE}.${tag}"
		fi
	done

	echo "${ordered}"
}

if [[ $# -eq 0 ]]; then
	usage
	exit 1
fi

if [[ "$1" == "--from-labels" ]]; then
	shift
	resolve_tags "$@"
	exit 0
fi

if [[ "$1" == "-h" || "$1" == "--help" ]]; then
	usage
	exit 0
fi

resolve_tags "$@"
