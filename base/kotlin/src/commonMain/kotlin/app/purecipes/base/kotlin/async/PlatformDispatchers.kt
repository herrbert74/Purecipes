package app.purecipes.base.kotlin.async

import kotlinx.coroutines.CoroutineDispatcher

expect fun ioDispatcher(): CoroutineDispatcher
