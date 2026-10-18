#!/usr/bin/env python3
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]

CLASS_RE = re.compile(
	r"(?P<ann>(?:^[ \t]*@[^\n]*\n)*)"
	r"^[ \t]*(?:(?:abstract|open|internal|public)\s+)?class\s+(?P<name>\w+)",
	re.MULTILINE,
)
TEST_RE = re.compile(
	r"(?P<header>(?:^[ \t]*@[^\n]*\n)*^[ \t]*@Test\b[^\n]*\n(?:^[ \t]*@[^\n]*\n)*)"
	r"^[ \t]*fun\s+(?P<name>\w+)\s*\(",
	re.MULTILINE,
)
TAG_RE = re.compile(r"@(?P<name>UiTest\w+)\b")


def parse_requested_tags(raw: str) -> set[str] | None:
	tokens = [part.strip() for part in raw.split(",") if part.strip()]
	if not tokens or "UiTestAll" in tokens:
		return None
	tags = {token for token in tokens if token.startswith("UiTest")}
	tags.add("UiTestEssential")
	return tags


def tags_in(block: str) -> set[str]:
	return {match.group("name") for match in TAG_RE.finditer(block)}


def count_matching_tests(tag_filter: set[str] | None) -> int:
	total = 0
	for path in sorted(ROOT.glob("**/src/androidDeviceTest/**/*Test.kt")):
		text = path.read_text(encoding="utf-8")
		classes = [
			(match.start(), tags_in(match.group("ann")))
			for match in CLASS_RE.finditer(text)
		]
		if not classes:
			continue
		for test_match in TEST_RE.finditer(text):
			class_tags: set[str] = set()
			for start, annotations in classes:
				if start <= test_match.start():
					class_tags = annotations
				else:
					break
			method_tags = tags_in(test_match.group("header"))
			combined = class_tags | method_tags
			if tag_filter is None or combined & tag_filter:
				total += 1
	return total


def main() -> int:
	raw = sys.argv[1] if len(sys.argv) > 1 else "UiTestAll"
	print(count_matching_tests(parse_requested_tags(raw)))
	return 0


if __name__ == "__main__":
	raise SystemExit(main())
