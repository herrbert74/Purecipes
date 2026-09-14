#!/usr/bin/env bash
set -euo pipefail

UI_TEST_MODULES=(
	feature/auth/ui
	feature/cooking/ui
	feature/featurerequests/ui
	feature/library/ui
	feature/newrecipe/ui
	feature/onboarding/ui
	feature/recipedetails/ui
	feature/search/ui
	feature/settings/ui
	feature/subscription/ui
)

expected_test_count="$(
	grep -r '@Test' feature/*/ui/src/androidDeviceTest --include='*.kt' | wc -l | tr -d '[:space:]'
)"
executed_test_count=0
failure_count=0
error_count=0
missing_count=0

print_failed_tests() {
	python3 - "$1" <<'PY'
import re
import sys

text = open(sys.argv[1], encoding="utf-8", errors="replace").read()
for match in re.finditer(r"<testcase\b([^>]*)>(.*?)</testcase>", text, re.S):
	attrs, body = match.group(1), match.group(2)
	if "<failure" not in body and "<error" not in body:
		continue
	name = re.search(r'\bname="([^"]*)"', attrs)
	classname = re.search(r'\bclassname="([^"]*)"', attrs)
	label = ".".join(
		part for part in (
			classname.group(1) if classname else "",
			name.group(1) if name else "",
		) if part
	)
	message = re.search(r'<(?:failure|error)\b[^>]*message="([^"]*)"', body)
	if message:
		print(f"{label}: {message.group(1)}")
	else:
		print(label)
PY
}

for module in "${UI_TEST_MODULES[@]}"; do
	results_dir="${module}/build/outputs/androidTest-results/connected/androidMain"
	report_file="$(find "${results_dir}" -maxdepth 1 -name 'TEST-*.xml' -print -quit 2>/dev/null || true)"
	if [[ -z "${report_file}" ]]; then
		echo "::error::Missing connected Android test results XML under ${results_dir}"
		missing_count=$((missing_count + 1))
		continue
	fi

	testsuites_tag="$(grep -m1 '<testsuites ' "${report_file}" || true)"
	if [[ -z "${testsuites_tag}" ]]; then
		echo "::error::Could not parse testsuites attributes in ${report_file}"
		missing_count=$((missing_count + 1))
		continue
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

	if [[ "${failures}" -gt 0 || "${errors}" -gt 0 ]]; then
		echo "::error::${module} connected tests: ${failures} failure(s), ${errors} error(s)"
		while IFS= read -r failed_test; do
			echo "::error::${failed_test}"
		done < <(print_failed_tests "${report_file}")
	fi
done

if [[ "${missing_count}" -gt 0 ]]; then
	echo "::error::Missing connected Android test results for ${missing_count} UI test module(s)."
fi

if [[ "${executed_test_count}" -lt "${expected_test_count}" ]]; then
	echo "::error::Expected at least ${expected_test_count} connected UI tests from source, but results recorded ${executed_test_count}."
fi

if [[ "${failure_count}" -gt 0 || "${error_count}" -gt 0 ]]; then
	echo "::error::Connected UI test results contain ${failure_count} failure(s) and ${error_count} error(s)."
fi

if [[ "${missing_count}" -gt 0 || "${executed_test_count}" -lt "${expected_test_count}" || "${failure_count}" -gt 0 || "${error_count}" -gt 0 ]]; then
	exit 1
fi

echo "Verified ${executed_test_count} connected UI tests across ${#UI_TEST_MODULES[@]} modules."
