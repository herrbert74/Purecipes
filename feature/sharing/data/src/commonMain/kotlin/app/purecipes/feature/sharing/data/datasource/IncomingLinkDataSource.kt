package app.purecipes.feature.sharing.data.datasource

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

internal class IncomingLinkDataSource {

	private val urls = Channel<String>(capacity = Channel.UNLIMITED)

	val incomingUrls: Flow<String> = urls.receiveAsFlow()

	fun deliver(url: String) {
		urls.trySend(url)
	}
}
