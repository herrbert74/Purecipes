package com.purecipes.feature.favorites.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.favorites.data.datasource.FavoritesRemoteDataSource
import com.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FavoritesAccessorTest {

	@Test
	fun `favorites repository returns favorite recipes`() = runTest {
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

		val outcome = accessor.getFavoriteRecipes()

		outcome.get() shouldBe expected
		outcome.getError() shouldBe null
	}
}
