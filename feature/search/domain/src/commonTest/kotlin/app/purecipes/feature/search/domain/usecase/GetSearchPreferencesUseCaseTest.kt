package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.shared.testfixtures.fake.FakeSearchPreferencesRepository
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class GetSearchPreferencesUseCaseTest {

	@Test
	fun `returns preferences from repository`() {
		val repository = FakeSearchPreferencesRepository(
			SearchPreferences(applyRecipeFiltersToTitleSearch = false),
		)
		val useCase = GetSearchPreferencesUseCase(repository)

		useCase() shouldBe SearchPreferences(applyRecipeFiltersToTitleSearch = false)
	}
}
