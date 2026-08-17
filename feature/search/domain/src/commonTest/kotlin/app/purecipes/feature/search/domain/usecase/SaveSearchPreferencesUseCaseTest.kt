package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.shared.testfixtures.fake.FakeSearchPreferencesRepository
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SaveSearchPreferencesUseCaseTest {

	@Test
	fun `saves preferences on repository`() {
		val repository = FakeSearchPreferencesRepository()
		val useCase = SaveSearchPreferencesUseCase(repository)
		val preferences = SearchPreferences(applyRecipeFiltersToTitleSearch = false)

		useCase(preferences)

		repository.getSearchPreferences() shouldBe preferences
	}
}
