package app.purecipes.feature.analytics.domain.model

enum class AnalyticsOrigin(val value: String) {
	SEARCH(AnalyticsScreenName.SEARCH),
	RECIPE_DETAILS(AnalyticsScreenName.RECIPE_DETAILS),
	COOKING(AnalyticsScreenName.COOKING),
	FAVORITES(AnalyticsScreenName.FAVORITES),
	CREATE_RECIPE(AnalyticsScreenName.CREATE_RECIPE),
	ACCOUNT(AnalyticsScreenName.ACCOUNT),
	EMAIL_SIGN_IN(AnalyticsScreenName.EMAIL_SIGN_IN),
	EMAIL_REGISTRATION(AnalyticsScreenName.EMAIL_REGISTRATION),
	SETTINGS(AnalyticsScreenName.SETTINGS),
	ACCOUNT_SETTINGS(AnalyticsScreenName.ACCOUNT_SETTINGS),
	ABOUT(AnalyticsScreenName.ABOUT),
	LICENSES(AnalyticsScreenName.LICENSES),
	CONSENT_PREFERENCES(AnalyticsScreenName.CONSENT_PREFERENCES),
	DEEP_LINK("deep_link"),
	SHARE("share"),
	;

	companion object {

		fun fromValue(value: String): AnalyticsOrigin? =
			entries.find { it.value == value }
	}
}
