#!/usr/bin/env bash
set -euo pipefail

UI_TEST_MODULES=(
	feature/auth/ui
	feature/cooking/ui
	feature/favorites/ui
	feature/newrecipe/ui
	feature/recipedetails/ui
	feature/search/ui
	feature/settings/ui
)

expected_test_count="$(
	grep -r '@Test' feature/*/ui/src/androidDeviceTest --include='*.kt' | wc -l | tr -d '[:space:]'
)"
executed_test_count=0
failure_count=0
error_count=0

for module in "${UI_TEST_MODULES[@]}"; do
	results_dir="${module}/build/outputs/androidTest-results/connected/androidMain"
	report_file="$(find "${results_dir}" -maxdepth 1 -name 'TEST-*.xml' -print -quit 2>/dev/null || true)"
	if [[ -z "${report_file}" ]]; then
		echo "::error::Missing connected Android test results XML under ${results_dir}"
		exit 1
	fi

	testsuites_tag="$(grep -m1 '<testsuites ' "${report_file}" || true)"
	if [[ -z "${testsuites_tag}" ]]; then
		echo "::error::Could not parse testsuites attributes in ${report_file}"
		exit 1
	fi

	tests="$(sed -n 's/.* tests="\([0-9][0-9]*\)".*/\1/p' <<<"${testsuites_tag}")"
	failures="$(sed -n 's/.* failures="\([0-9][0-9]*\)".*/\1/p' <<<"${testsuites_tag}")"
	errors="$(sed -n 's/.* errors="\([0-9][0-9]*\)".*/\1/p' <<<"${testsuites_tag}")"
	tests="${tests:-0}"
	failures="${failures:-0}"
	errors="${errors:-0}"

	executed_test_count=$((executed_test_count + tests))
	failure_count=$((failure_count + failures))
	error_count=$((error_count + errors))
done

if [[ "${executed_test_count}" -lt "${expected_test_count}" ]]; then
	echo "::error::Expected at least ${expected_test_count} connected UI tests from source, but results recorded ${executed_test_count}."
	exit 1
fi

if [[ "${failure_count}" -gt 0 || "${error_count}" -gt 0 ]]; then
	echo "::error::Connected UI test results contain ${failure_count} failure(s) and ${error_count} error(s)."
	exit 1
fi

echo "Verified ${executed_test_count} connected UI tests across ${#UI_TEST_MODULES[@]} modules."
