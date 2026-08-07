package app.purecipes.feature.search.ui.filter

private const val MAX_VISIBLE_SELECTION_LABELS = 3
private const val OVERFLOW_VISIBLE_SELECTION_LABELS = 2
private const val SELECTION_SUBTITLE_SEPARATOR = ", "

internal fun formatFilterSectionSelectionSubtitle(labels: List<String>): String? {
	if (labels.isEmpty()) {
		return null
	}
	return when {
		labels.size <= MAX_VISIBLE_SELECTION_LABELS -> {
			labels.joinToString(SELECTION_SUBTITLE_SEPARATOR)
		}

		else -> {
			val visible = labels.take(OVERFLOW_VISIBLE_SELECTION_LABELS)
			val remaining = labels.size - OVERFLOW_VISIBLE_SELECTION_LABELS
			"${visible.joinToString(SELECTION_SUBTITLE_SEPARATOR)} +$remaining"
		}
	}
}
