package com.purecipes.shared.data.getresult

import com.purecipes.base.kotlin.result.Failure
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException

/**
 * Handle only expected Exceptions, throw Errors and other Exceptions, like CancellationException
 */
fun Throwable.handle() = when (this) {
    // is HttpException -> {
    //     val errorJson = this.response()?.errorBody()?.string() ?: ""
    //     val errorBody = Json.decodeFromString<ErrorBody>(errorJson)
    //     Failure.ServerError(errorBody.status_message)
    // }
    is IOException -> Failure.IoFailure
    else -> throw this
}

/**
 * Handle all HTTP responses that are not successful.  This implementation mirrors
 * the old Retrofit version but works with Ktor's [HttpResponse].
 */
suspend fun HttpResponse.handle(): Failure = when (status) {
    HttpStatusCode.NotModified -> Failure.NotModified
    else -> {
        // read the body as text so callers can log or present it
        val errorText = body<String>()
        Failure.ServerError(errorText)
    }
}
