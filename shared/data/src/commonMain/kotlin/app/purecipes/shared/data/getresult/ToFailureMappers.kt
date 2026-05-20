package app.purecipes.shared.data.getresult

import app.purecipes.base.kotlin.result.Failure
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
suspend fun Throwable.handle() = when (this) {
	is IOException -> Failure.IoFailure
	is ResponseException -> response.handle()
	else -> throw this
}

/**
 * Handle all HTTP responses that are not successful.  This implementation mirrors
 * the old Retrofit version but works with Ktor's [HttpResponse].
 */
suspend fun HttpResponse.handle(): Failure = when (status) {
	HttpStatusCode.NotModified -> Failure.NotModified
	else -> Failure.ServerError(errorMessage())
}

private suspend fun HttpResponse.errorMessage(): String {
	val errorText = body<String>()

	if (errorText.isBlank()) {
		return status.description
	}

	val errorBody = runCatching {
		json.decodeFromString(ApiErrorBody.serializer(), errorText)
	}.getOrNull()

	if (errorBody != null) {
		return if (status.value >= InternalServerError.value) {
			errorBody.message?.takeIf { message -> message.isNotBlank() }
				?: DEFAULT_SERVER_ERROR_MESSAGE
		} else {
			errorBody.detail?.takeIf { detail -> detail.isNotBlank() }
				?: errorBody.message?.takeIf { message -> message.isNotBlank() }
				?: errorBody.error?.takeIf { error -> error.isNotBlank() }
				?: errorText
		}
	}

	return if (status.value >= InternalServerError.value) {
		DEFAULT_SERVER_ERROR_MESSAGE
	} else {
		errorText
	}
}

@Serializable
private data class ApiErrorBody(
	val detail: String? = null,
	val error: String? = null,
	val message: String? = null,
)

private const val DEFAULT_SERVER_ERROR_MESSAGE = "Something went wrong. Please try again."
