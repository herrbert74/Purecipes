package app.purecipes.feature.home.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.home.domain.repository.HomeFeedRepository
import app.purecipes.shared.domain.model.HomeFeed
import dev.zacsweers.metro.Inject

@Inject
class GetHomeFeedUseCase(
	private val repository: HomeFeedRepository,
) {

	suspend operator fun invoke(): Outcome<HomeFeed> = repository.getHomeFeed()
}
