package app.purecipes.feature.featurerequests.ui

import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus

data class FeatureRequestsListCallbacks(
	val onSortSelected: (FeatureRequestSort) -> Unit,
	val onStatusFilterSelected: (FeatureRequestStatus?) -> Unit,
	val onFeatureRequestSelect: (Int) -> Unit,
	val onToggleVote: (FeatureRequest) -> Unit,
	val onCreateClick: () -> Unit,
)
