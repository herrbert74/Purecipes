package app.purecipes.feature.featurerequests.ui

import androidx.compose.runtime.Immutable

@Immutable
data class FeatureRequestDetailUiState(
	val isLoading: Boolean,
	val isSendingComment: Boolean,
	val errorMessage: String?,
	val commentErrorMessage: String?,
)
