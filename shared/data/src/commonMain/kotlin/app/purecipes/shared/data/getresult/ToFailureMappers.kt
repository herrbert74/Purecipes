package app.purecipes.shared.data.getresult

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.shared.data.session.UnauthorizedSessionClearer
import app.purecipes.shared.data.session.UnauthorizedSessionClearers
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val json = Json {
	ignoreUnknownKeys = true
	explicitNulls = false
}

/**
 * Handle only expected Exceptions, throw Errors and other Exceptions, like CancellationException
 */
suspend fun Throwable.handle(
	unauthorizedSessionClearer: UnauthorizedSessionClearer = UnauthorizedSessionClearers.instance,
) = when (this) {
	is IOException -> Failure.IoFailure
	is ResponseException -> response.handle(unauthorizedSessionClearer)
	else -> throw this
}

/**
 * Handle all HTTP responses that are not successful.  This implementation mirrors
 * the old Retrofit version but works with Ktor's [HttpResponse].
 */
suspend fun HttpResponse.handle(
	unauthorizedSessionClearer: UnauthorizedSessionClearer = UnauthorizedSessionClearers.instance,
): Failure = when (status) {
	HttpStatusCode.NotModified -> Failure.NotModified
	HttpStatusCode.Unauthorized -> unauthorizedFailure(unauthorizedSessionClearer)
	else -> Failure.ServerError(errorMessage())
}

private suspend fun HttpResponse.unauthorizedFailure(
	unauthorizedSessionClearer: UnauthorizedSessionClearer,
): Failure {
	val message = errorMessage()
	return if (message.isInvalidOrExpiredSessionDetail()) {
		unauthorizedSessionClearer.clearUnauthorizedSession()
		Failure.UserNotLoggedIn
	} else {
		Failure.ServerError(message)
	}
}

internal fun String.isInvalidOrExpiredSessionDetail(): Boolean {
	val normalized = trim().lowercase()
	return normalized == SESSION_INVALID_OR_EXPIRED_DETAIL ||
		normalized == MISSING_BEARER_TOKEN_DETAIL ||
		normalized == SESSION_USER_INVALID_DETAIL
}

private suspend fun HttpResponse.errorMessage(): String {
	val errorText = body<String>()

	if (errorText.isBlank()) {
		return status.description
	}

	val errorBody = runCatching {
		json.decodeFromString(ApiErrorBody.serializer(), errorText)
	}.getOrNull()

	return if (errorBody != null) {
		if (status.value >= InternalServerError.value) {
			errorBody.message?.takeIf { message -> message.isNotBlank() }
				?: DEFAULT_SERVER_ERROR_MESSAGE
		} else {
			errorBody.detail?.takeIf { detail -> detail.isNotBlank() }
				?: errorBody.message?.takeIf { message -> message.isNotBlank() }
				?: errorBody.error?.takeIf { error -> error.isNotBlank() }
				?: errorText.toUserFacingRemoteErrorMessage()
		}
	} else if (status.value >= InternalServerError.value) {
		DEFAULT_SERVER_ERROR_MESSAGE
	} else {
		errorText.toUserFacingRemoteErrorMessage()
	}
}

@Serializable
private data class ApiErrorBody(
	val detail: String? = null,
	val error: String? = null,
	val message: String? = null,
)

private const val SESSION_INVALID_OR_EXPIRED_DETAIL = "session is invalid or expired"
private const val MISSING_BEARER_TOKEN_DETAIL = "missing bearer token"
private const val SESSION_USER_INVALID_DETAIL = "session user is invalid"
