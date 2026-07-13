package app.purecipes.feature.analytics.domain.model

object AnalyticsGlobalProperty {

	const val ENVIRONMENT = "environment"
	const val PLATFORM = "platform"
	const val APP_VERSION = "app_version"
	const val USER_STATE = "user_state"
	const val ACTIVE_TAB = "active_tab"
	const val CURRENT_SCREEN = "current_screen"
}

object AnalyticsUserState {

	const val LOGGED_IN = "logged_in"
	const val ANONYMOUS = "anonymous"
}

object AnalyticsActiveTab {

	const val SEARCH = "search"
	const val FAVORITES = "favorites"
	const val CREATE = "create"
	const val ACCOUNT = "account"
}
