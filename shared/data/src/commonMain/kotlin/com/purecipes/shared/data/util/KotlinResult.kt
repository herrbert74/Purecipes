package com.purecipes.shared.data.util

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import com.purecipes.shared.data.getresult.handle
import kotlinx.coroutines.CancellationException

/**
 * [runCatching] version that handles expected Exceptions and rethrows everything else, including
 * CancellationException, @return [com.purecipes.base.kotlin.result.Outcome].
 */
suspend inline fun <V> runCatchingApi(crossinline block: suspend () -> V) = try {
	Ok(block())
} catch (throwable: Throwable) {
	Err(throwable.handle())
}

/**
 * [runCatching] version that handles expected Exceptions and rethrows everything else, including Errors and
 * CancellationException, @return [com.purecipes.base.kotlin.result.Outcome].
 * To be called on a receiver, for example DataSource.runCatchingApi {...}.
 */
suspend inline fun <T, V> T.runCatchingApi(crossinline block: suspend T.() -> V) = try {
	Ok(block())
} catch (throwable: Throwable) {
	Err(throwable.handle())
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

		//Timber.d("zsoltbertalan* runCatchingUnit: ${it.message}")
		it
	}
