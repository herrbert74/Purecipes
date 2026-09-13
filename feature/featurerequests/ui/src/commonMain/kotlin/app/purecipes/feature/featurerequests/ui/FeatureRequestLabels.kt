package app.purecipes.feature.featurerequests.ui

import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus

internal fun FeatureRequestSort.label(): String = when (this) {
	FeatureRequestSort.TOP_VOTES -> "Top votes"
	FeatureRequestSort.NEWEST -> "Newest"
}

internal fun FeatureRequestStatus.label(): String = when (this) {
	FeatureRequestStatus.OPEN -> "Open"
	FeatureRequestStatus.PLANNED -> "Planned"
	FeatureRequestStatus.IN_PROGRESS -> "In progress"
	FeatureRequestStatus.DONE -> "Done"
}
