package app.purecipes.feature.featurerequests.domain.model

sealed interface FeatureRequestEvent {

	val requestId: Int

	data class CommentAdded(override val requestId: Int) : FeatureRequestEvent
}
