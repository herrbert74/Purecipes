package app.purecipes.feature.analytics.domain.model

object CookbookAnalyticsProperty {

	const val COOKBOOK_ID = "cookbook_id"
	const val COOKBOOK_NAME = "cookbook_name"

	fun identity(
		cookbookId: Int,
		cookbookName: String?,
	): Map<String, AnalyticsValue> = buildMap {
		put(COOKBOOK_ID, cookbookId.asAnalyticsValue())
		if (!cookbookName.isNullOrBlank()) {
			put(COOKBOOK_NAME, AnalyticsValue.TextValue(cookbookName))
		}
	}
}
