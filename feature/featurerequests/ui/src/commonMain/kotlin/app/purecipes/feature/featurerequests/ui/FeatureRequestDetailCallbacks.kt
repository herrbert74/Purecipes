package app.purecipes.feature.featurerequests.ui

data class FeatureRequestDetailCallbacks(
	val onBack: () -> Unit,
	val onToggleVote: () -> Unit,
	val onAddComment: (String) -> Unit,
)
