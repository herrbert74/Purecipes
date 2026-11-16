package app.purecipes.shared.testfixtures.uitest

data class AccessibilityCheckSuppression(
	val checkClassName: String,
	val messageContains: String? = null,
	val hierarchyContains: String? = null,
) {

	fun matches(
		actualCheckClassName: String,
		message: String,
		hierarchyDescription: String = "",
	): Boolean {
		val messageMatches = messageContains?.let(message::contains) ?: true
		val hierarchyMatches = hierarchyContains?.let(hierarchyDescription::contains) ?: true
		return checkClassName == actualCheckClassName && messageMatches && hierarchyMatches
	}
}
