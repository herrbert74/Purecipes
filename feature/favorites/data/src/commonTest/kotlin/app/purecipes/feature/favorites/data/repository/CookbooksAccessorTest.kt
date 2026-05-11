package app.purecipes.feature.favorites.data.repository

import app.purecipes.feature.favorites.data.datasource.CookbooksRemoteDataSource
import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.CookbookSummary
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
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

	@Test
	fun `delete cookbook removes empty cookbook`() = runTest {
		val now = 1_700_000_000_000L
		val initial = listOf(
			CookbookSummary(id = 1, name = "Weeknight", recipeCount = 0, updatedAtEpochMillis = now),
		)
		val accessor = CookbooksAccessor(
			CookbooksRemoteDataSource(FakePurecipesApi(initialCookbooks = initial)),
		)

		val deleteOutcome = accessor.deleteCookbook(cookbookId = 1)
		val pageAfterDelete = accessor.getCookbooksPage(pageNumber = 1, pageSize = 20)

		deleteOutcome.getError() shouldBe null
		pageAfterDelete.get()?.totalMatches shouldBe 0
	}
}
