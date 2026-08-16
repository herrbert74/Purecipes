package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.shared.testfixtures.fake.FakeSearchPreferencesRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObserveSearchPreferencesUseCaseTest {

	@Test
	fun `emits preferences from repository`() = runTest {
		val repository = FakeSearchPreferencesRepository(
			SearchPreferences(applyRecipeFiltersToTitleSearch = false),
		)
		val useCase = ObserveSearchPreferencesUseCase(repository)

		useCase().first() shouldBe SearchPreferences(applyRecipeFiltersToTitleSearch = false)
	}
}
