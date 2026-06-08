package app.purecipes.feature.search.domain.readiness

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Inject
@SingleIn(AppScope::class)
class SearchReadinessCoordinator {

	private val mutableIsReady = MutableStateFlow(false)

	val isReady: StateFlow<Boolean> = mutableIsReady.asStateFlow()

	fun reportReady() {
		mutableIsReady.value = true
	}
}
