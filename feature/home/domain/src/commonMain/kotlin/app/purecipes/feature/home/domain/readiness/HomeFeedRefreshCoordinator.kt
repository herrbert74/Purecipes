package app.purecipes.feature.home.domain.readiness

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Inject
@SingleIn(AppScope::class)
class HomeFeedRefreshCoordinator {

	private val mutableRevision = MutableStateFlow(0)

	val revision: StateFlow<Int> = mutableRevision.asStateFlow()

	fun markStale() {
		mutableRevision.value += 1
	}
}
