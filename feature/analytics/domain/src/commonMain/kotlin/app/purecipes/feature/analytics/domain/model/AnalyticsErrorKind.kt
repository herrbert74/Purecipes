package app.purecipes.feature.analytics.domain.model

import app.purecipes.base.kotlin.result.Failure

object AnalyticsErrorKind {

	const val SERVER_ERROR = "server_error"
	const val UNKNOWN_API_ERROR = "unknown_api_error"
	const val IO_FAILURE = "io_failure"
	const val UNKNOWN_HOST = "unknown_host"
	const val UNEXPECTED = "unexpected"
	const val NOT_MODIFIED = "not_modified"
	const val USER_NOT_LOGGED_IN = "user_not_logged_in"
	const val USER_CANCELLED = "user_cancelled"
	const val UNKNOWN = "unknown"
}

fun Failure.toAnalyticsErrorKind(): String {
	return when (this) {
		is Failure.ServerError -> AnalyticsErrorKind.SERVER_ERROR
		is Failure.UnknownApiError -> AnalyticsErrorKind.UNKNOWN_API_ERROR
		is Failure.IoFailure -> AnalyticsErrorKind.IO_FAILURE
		is Failure.UnknownHostFailure -> AnalyticsErrorKind.UNKNOWN_HOST
		is Failure.UnexpectedFailure -> AnalyticsErrorKind.UNEXPECTED
		is Failure.NotModified -> AnalyticsErrorKind.NOT_MODIFIED
		is Failure.UserNotLoggedIn -> AnalyticsErrorKind.USER_NOT_LOGGED_IN
	}
}

fun Failure.asHandledException(): Exception = Exception(toAnalyticsErrorKind())
