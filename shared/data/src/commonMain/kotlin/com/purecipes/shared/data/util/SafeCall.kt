package com.purecipes.shared.data.util

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.shared.data.getresult.handle
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

/**
 * Executes a network request that returns a Ktor [HttpResponse].
 *
 * @param makeNetworkRequest The request
 * @param mapper, which DOES NOT use the response metadata.
 *
 * @return [Outcome] where [DOMAIN] does NOT contain any response metadata.
 */
suspend inline fun <reified REMOTE, DOMAIN> safeCall(
	crossinline makeNetworkRequest: suspend () -> HttpResponse,
	crossinline mapper: REMOTE.() -> DOMAIN,
): Outcome<DOMAIN> {
	return runCatchingApi {
		makeNetworkRequest()
	}.andThen { response ->
		if (response.status.isSuccess()) {
			Ok(response.body<REMOTE>())
		} else {
			Err(response.handle())
		}
	}.map {
		it.mapper()
	}
}

/**
 * Executes a network request that returns a Ktor [HttpResponse].
 *
 * @param makeNetworkRequest The request
 * @param mapper, which uses the response metadata.
 *
 * @return [Outcome], where [DOMAIN] contains metadata from the [HttpResponse]. This can be used to save it to
 * the database or use it in the Presentation layer.
 *
 * The downside of this is that the metadata is exposed to the domain layer. To mitigate this another model layer
 * should be introduced, if the amount of the data in the domain (or presentation) is a concern.
 */
suspend inline fun <REMOTE, DOMAIN> safeCallWithMetadata(
	crossinline makeNetworkRequest: suspend () -> HttpResponse,
	crossinline mapper: HttpResponse.() -> DOMAIN,
): Outcome<DOMAIN> {
	return runCatchingApi {
		makeNetworkRequest()
	}.andThen { response ->
		if (response.status.isSuccess()) {
			Ok(response)
		} else {
			Err(response.handle())
		}
	}.map {
		it.mapper()
	}
}
