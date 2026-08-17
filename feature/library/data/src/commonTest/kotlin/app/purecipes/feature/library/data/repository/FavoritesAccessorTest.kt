package app.purecipes.feature.library.data.repository

import app.purecipes.feature.library.data.datasource.FavoritesRemoteDataSource
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesAccessorTest {

	@Test
	fun `favorites repository returns favorite recipes page`() = runTest {
		val expected = listOf(
			RecipeSummary(
				id = 42,
				title = "Tomato Pasta",
				cuisine = Cuisine.ITALIAN,
				imageUrl = "https://example.com/pasta.jpg",
				totalTime = 25,
				isFavorite = true,
			),
		)
		val accessor = FavoritesAccessor(
			FavoritesRemoteDataSource(FakePurecipesApi(favoriteRecipes = expected)),
		)

		val outcome = accessor.getFavoriteRecipesPage(pageNumber = 1, pageSize = 20)

		outcome.get()?.items shouldBe expected
		outcome.get()?.totalMatches shouldBe 1
		outcome.getError() shouldBe null
	}

	@Test
	fun `add favorite emits added event`() = runTest {
		val accessor = FavoritesAccessor(
			FavoritesRemoteDataSource(FakePurecipesApi()),
		)
		val event = async { accessor.observeFavoriteEvents().take(1).single() }
		runCurrent()

		accessor.addFavorite(recipeId = 42)

		event.await() shouldBe FavoriteEvent.Added(recipeId = 42)
	}

	@Test
	fun `remove favorite emits removed event`() = runTest {
		val accessor = FavoritesAccessor(
			FavoritesRemoteDataSource(FakePurecipesApi()),
		)
		val event = async { accessor.observeFavoriteEvents().take(1).single() }
		runCurrent()

		accessor.removeFavorite(recipeId = 7)

		event.await() shouldBe FavoriteEvent.Removed(recipeId = 7)
	}
}
