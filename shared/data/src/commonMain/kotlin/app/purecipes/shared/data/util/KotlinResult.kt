package app.purecipes.shared.data.util

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.shared.data.getresult.handle
import app.purecipes.shared.data.getresult.toUserFacingRemoteErrorMessage
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

/**
 * [runCatching] version that handles expected Exceptions and rethrows everything else, including
 * CancellationException, @return [app.purecipes.base.kotlin.result.Outcome].
 */
suspend inline fun <V> runCatchingApi(crossinline block: suspend () -> V) = try {
	Ok(block())
} catch (exception: IOException) {
	Err(exception.handle())
} catch (exception: ResponseException) {
	Err(exception.handle())
	} catch (exception: JsonConvertException) {
	Err(
		Failure.ServerError(
			(exception.message ?: "").toUserFacingRemoteErrorMessage(),
		),
	)
}

/**
 * [runCatching] version that handles expected Exceptions and rethrows everything else, including Errors and
 * CancellationException, @return [app.purecipes.base.kotlin.result.Outcome].
 * To be called on a receiver, for example DataSource.runCatchingApi {...}.
 */
suspend inline fun <T, V> T.runCatchingApi(crossinline block: suspend T.() -> V) = try {
	Ok(block())
} catch (exception: IOException) {
	Err(exception.handle())
} catch (exception: ResponseException) {
	Err(exception.handle())
	} catch (exception: JsonConvertException) {
	Err(
		Failure.ServerError(
			(exception.message ?: "").toUserFacingRemoteErrorMessage(),
		),
	)
}

/**
 * [runCatching] version that handles all Throwable exceptions, logs them, and rethrows
 * CancellationException.
 * Use it to save data for example, when return type is Unit.
 */
inline fun <V> runCatchingUnit(block: () -> V) = runCatching(block)
	.mapError {
		if (it is CancellationException) {
			throw it
		}

		// Timber.d("zsoltbertalan* runCatchingUnit: ${it.message}")
		it
	}
