package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.HomeFeedRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.HomeFeed
import dev.zacsweers.metro.Inject

@Inject
class GetHomeFeedUseCase(
	private val repository: HomeFeedRepository,
) {

	suspend operator fun invoke(): SearchOutcome<HomeFeed> = repository.getHomeFeed()
}
