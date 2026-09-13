package app.purecipes.feature.featurerequests.ui

import androidx.compose.runtime.Immutable
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class FeatureRequestsListState(
	val featureRequests: ImmutableList<FeatureRequest>,
	val sort: FeatureRequestSort,
	val statusFilter: FeatureRequestStatus?,
	val totalMatches: Int,
	val errorMessage: String?,
	val isLoading: Boolean,
)
