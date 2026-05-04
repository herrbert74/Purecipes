package com.purecipes.feature.favorites.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.favorites.data.datasource.CookbooksRemoteDataSource
import com.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import com.purecipes.shared.domain.model.CookbookSummary
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CookbooksAccessorTest {

	@Test
	fun `cookbooks repository returns cookbook page`() = runTest {
		val now = 1_700_000_000_000L
		val initial = listOf(
			CookbookSummary(id = 1, name = "Weeknight", recipeCount = 0, updatedAtEpochMillis = now),
		)
		val accessor = CookbooksAccessor(
			CookbooksRemoteDataSource(FakePurecipesApi(initialCookbooks = initial)),
		)

		val outcome = accessor.getCookbooksPage(pageNumber = 1, pageSize = 20)

		outcome.get()?.items?.single()?.id shouldBe 1
		outcome.get()?.totalMatches shouldBe 1
		outcome.getError() shouldBe null
	}
}
