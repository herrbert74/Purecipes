package app.purecipes.feature.library.domain.usecase

import app.purecipes.feature.library.domain.model.CookbookMembershipEvent
import app.purecipes.feature.library.domain.repository.CookbooksRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveCookbookMembershipEventsUseCase(
	private val repository: CookbooksRepository,
) {

	operator fun invoke(): Flow<CookbookMembershipEvent> = repository.observeCookbookMembershipEvents()
}
