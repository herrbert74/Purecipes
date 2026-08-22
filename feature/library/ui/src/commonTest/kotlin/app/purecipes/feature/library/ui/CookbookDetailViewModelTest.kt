package app.purecipes.feature.library.ui

import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.repository.CookbookCoverRepository
import app.purecipes.feature.library.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveRecipeFromCookbookUseCase
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CookbookDetailViewModelTest {

	@Test
	fun `remove recipe from cookbook updates detail list`() = runViewModelTest {
		val recipe = RecipeSummary(
			id = 77,
			title = "Creamy tomato pasta",
			cuisine = Cuisine.ITALIAN,
			imageUrl = null,
			totalTime = 30,
			isFavorite = true,
		)
		val cookbook = CookbookSummary(
			id = 23,
			name = "Weeknight dinners",
			recipeCount = 1,
			updatedAtEpochMillis = 0L,
		)
		val cookbooksRepository = FakeCookbooksRepository(
			cookbooksPageResult = Ok(
				CookbookListPage(
					items = listOf(cookbook),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
			cookbookRecipesPageResult = Ok(
				SearchResultsPage(
					items = listOf(recipe),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		val viewModel = cookbookDetailViewModel(
			cookbookId = cookbook.id,
			name = cookbook.name,
			cookbooksRepository = cookbooksRepository,
		)
		advanceUntilIdle()

		var removed = false
		viewModel.removeRecipe(recipe) { ok ->
			removed = ok
		}
		advanceUntilIdle()

		assertEquals(true, removed)
		assertEquals(1, cookbooksRepository.removeRecipeFromCookbookCallCount)
		assertEquals(cookbook.id, cookbooksRepository.lastRemovedCookbookId)
		assertEquals(recipe.id, cookbooksRepository.lastRemovedRecipeId)
		assertEquals(emptyList(), viewModel.recipes.toList())
		assertEquals(0, viewModel.totalMatches)
		assertEquals(null, viewModel.errorMessage)
	}

	private val getCookbookCoverImageUrl = GetCookbookCoverImageUrlUseCase(
		repository = object : CookbookCoverRepository {
			override fun getCookbookCoverImageUrl(
				cookbookId: Int,
				candidateImageUrls: List<String>,
				nowMillis: Long,
				random: Random,
			): String? = candidateImageUrls.firstOrNull()
		},
	)

	private fun cookbookDetailViewModel(
		cookbookId: Int,
		name: String,
		cookbooksRepository: FakeCookbooksRepository = FakeCookbooksRepository(),
	): CookbookDetailViewModel = CookbookDetailViewModel(
		getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
		removeRecipeFromCookbookUseCase = RemoveRecipeFromCookbookUseCase(cookbooksRepository),
		getCookbookCoverImageUrl = getCookbookCoverImageUrl,
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
		shareCookbook = unusedShareCookbookUseCase(),
		trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
		cookbookId = cookbookId,
		initialName = name,
		sessionKey = "session",
	)
}
