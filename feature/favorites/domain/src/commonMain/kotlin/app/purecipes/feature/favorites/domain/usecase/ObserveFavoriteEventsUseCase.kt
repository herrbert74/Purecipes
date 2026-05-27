package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.feature.favorites.domain.model.FavoriteEvent
import app.purecipes.feature.favorites.domain.repository.FavoritesRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveFavoriteEventsUseCase(
	private val repository: FavoritesRepository,
) {

	operator fun invoke(): Flow<FavoriteEvent> = repository.observeFavoriteEvents()
}
