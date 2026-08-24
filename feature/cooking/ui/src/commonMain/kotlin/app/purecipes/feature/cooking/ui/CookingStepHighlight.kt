package app.purecipes.feature.cooking.ui

internal sealed interface CookingStepHighlight {

	val startIndex: Int
	val endIndex: Int
	val text: String

	data class Duration(
		override val startIndex: Int,
		override val endIndex: Int,
		override val text: String,
		val totalSeconds: Int,
	) : CookingStepHighlight

	data class Temperature(
		override val startIndex: Int,
		override val endIndex: Int,
		override val text: String,
	) : CookingStepHighlight
}
