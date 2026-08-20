package app.purecipes.feature.library.data.repository

import app.purecipes.feature.library.data.datasource.CookbooksRemoteDataSource
import app.purecipes.feature.library.domain.model.CookbookMembershipEvent
import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.CookbookSummary
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

	@Test
	fun `add recipe to cookbook emits added membership event`() = runTest {
		val accessor = CookbooksAccessor(
			CookbooksRemoteDataSource(FakePurecipesApi()),
		)
		val event = async { accessor.observeCookbookMembershipEvents().take(1).single() }
		runCurrent()

		accessor.addRecipeToCookbook(cookbookId = 10, recipeId = 42)

		event.await() shouldBe CookbookMembershipEvent.Added(recipeId = 42, cookbookId = 10)
	}

	@Test
	fun `remove recipe from cookbook emits removed membership event`() = runTest {
		val accessor = CookbooksAccessor(
			CookbooksRemoteDataSource(FakePurecipesApi()),
		)
		val event = async { accessor.observeCookbookMembershipEvents().take(1).single() }
		runCurrent()

		accessor.removeRecipeFromCookbook(cookbookId = 10, recipeId = 42)

		event.await() shouldBe CookbookMembershipEvent.Removed(recipeId = 42, cookbookId = 10)
	}
}
