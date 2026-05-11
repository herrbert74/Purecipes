package app.purecipes.feature.favorites.data.repository

import app.purecipes.feature.favorites.data.datasource.FavoritesRemoteDataSource
import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

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
}
