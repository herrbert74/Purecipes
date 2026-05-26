package app.purecipes.feature.sharing.data.datasource

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class IncomingLinkDataSource {

	private val urls = MutableSharedFlow<String>(extraBufferCapacity = 8)

	val incomingUrls: SharedFlow<String> = urls.asSharedFlow()

	fun deliver(url: String) {
		urls.tryEmit(url)
	}
}
