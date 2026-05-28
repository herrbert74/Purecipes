package app.purecipes.feature.sharing.data.datasource

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Inject
@SingleIn(AppScope::class)
class IncomingLinkDataSource {

	private val urls = Channel<String>(capacity = Channel.UNLIMITED)

	val incomingUrls: Flow<String> = urls.receiveAsFlow()

	fun deliver(url: String) {
		urls.trySend(url)
	}
}
