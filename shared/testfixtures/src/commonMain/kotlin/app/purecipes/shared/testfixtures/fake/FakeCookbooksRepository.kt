package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.library.domain.model.CookbookMembershipEvent
import app.purecipes.feature.library.domain.repository.CookbooksRepository
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeCookbooksRepository(
	private val cookbooksPageResult: Outcome<CookbookListPage> = Ok(
		CookbookListPage(
			items = emptyList(),
			pageNumber = 1,
			pageSize = 20,
			totalMatches = 0,
		),
	),
	private val createCookbookResult: Outcome<CookbookSummary> = Ok(
		CookbookSummary(id = 1, name = "Test", recipeCount = 0, updatedAtEpochMillis = 0L),
	),
	private val cookbookRecipesPageResult: Outcome<SearchResultsPage> = Ok(
		SearchResultsPage(
			items = emptyList(),
			pageNumber = 1,
			pageSize = 20,
			totalMatches = 0,
		),
	),
	private val deleteCookbookResult: Outcome<Unit> = Ok(Unit),
	private val removeRecipeFromCookbookResult: Outcome<Unit> = Ok(Unit),
	private val addRecipeToCookbookResult: Outcome<Unit> = Ok(Unit),
	initialRecipeCookbooksResult: Outcome<List<CookbookRef>> = Ok(emptyList()),
) : CookbooksRepository {

	private val cookbookMembershipEvents = MutableSharedFlow<CookbookMembershipEvent>(extraBufferCapacity = 1)

	var recipeCookbooksResult: Outcome<List<CookbookRef>> = initialRecipeCookbooksResult
		private set

	var createCookbookCallCount: Int = 0
		private set

	var addRecipeToCookbookCallCount: Int = 0
		private set

	var deleteCookbookCallCount: Int = 0
		private set

	var removeRecipeFromCookbookCallCount: Int = 0
		private set

	var lastRemovedCookbookId: Int? = null
		private set

	var lastRemovedRecipeId: Int? = null
		private set

	fun setRecipeCookbooksResult(result: Outcome<List<CookbookRef>>) {
		recipeCookbooksResult = result
	}

	fun emitCookbookMembershipEvent(event: CookbookMembershipEvent) {
		cookbookMembershipEvents.tryEmit(event)
	}

	override suspend fun getCookbooksPage(pageNumber: Int, pageSize: Int): Outcome<CookbookListPage> =
		cookbooksPageResult

	override suspend fun createCookbook(name: String): Outcome<CookbookSummary> {
		createCookbookCallCount += 1
		return createCookbookResult
	}

	override suspend fun deleteCookbook(cookbookId: Int): Outcome<Unit> {
		deleteCookbookCallCount += 1
		return deleteCookbookResult
	}

	override suspend fun getCookbookRecipesPage(
		cookbookId: Int,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<SearchResultsPage> = cookbookRecipesPageResult

	override suspend fun addRecipeToCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> {
		addRecipeToCookbookCallCount += 1
		return addRecipeToCookbookResult.also { outcome ->
			if (outcome.getError() == null) {
				cookbookMembershipEvents.tryEmit(
					CookbookMembershipEvent.Added(recipeId = recipeId, cookbookId = cookbookId),
				)
			}
		}
	}

	override suspend fun removeRecipeFromCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> {
		removeRecipeFromCookbookCallCount += 1
		lastRemovedCookbookId = cookbookId
		lastRemovedRecipeId = recipeId
		return removeRecipeFromCookbookResult.also { outcome ->
			if (outcome.getError() == null) {
				cookbookMembershipEvents.tryEmit(
					CookbookMembershipEvent.Removed(recipeId = recipeId, cookbookId = cookbookId),
				)
			}
		}
	}

	override suspend fun getRecipeCookbooks(recipeId: Int): Outcome<List<CookbookRef>> = recipeCookbooksResult

	override fun observeCookbookMembershipEvents(): Flow<CookbookMembershipEvent> =
		cookbookMembershipEvents.asSharedFlow()
}
