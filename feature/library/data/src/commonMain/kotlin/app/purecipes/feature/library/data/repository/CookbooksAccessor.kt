package app.purecipes.feature.library.data.repository

import app.purecipes.feature.library.data.datasource.CookbooksDataSource
import app.purecipes.feature.library.domain.model.CookbookMembershipEvent
import app.purecipes.feature.library.domain.repository.CookbooksRepository
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class CookbooksAccessor(
	private val remoteDataSource: CookbooksDataSource.Remote,
) : CookbooksRepository {

	private val cookbookMembershipEvents = MutableSharedFlow<CookbookMembershipEvent>(extraBufferCapacity = 1)

	override suspend fun getCookbooksPage(pageNumber: Int, pageSize: Int) =
		remoteDataSource.getCookbooksPage(pageNumber, pageSize)

	override suspend fun createCookbook(name: String) = remoteDataSource.createCookbook(name)

	override suspend fun deleteCookbook(cookbookId: Int) = remoteDataSource.deleteCookbook(cookbookId)

	override suspend fun getCookbookRecipesPage(cookbookId: Int, pageNumber: Int, pageSize: Int) =
		remoteDataSource.getCookbookRecipesPage(cookbookId, pageNumber, pageSize)

	override suspend fun addRecipeToCookbook(cookbookId: Int, recipeId: Int) =
		remoteDataSource.addRecipeToCookbook(cookbookId, recipeId).also { outcome ->
			if (outcome.getError() == null) {
				cookbookMembershipEvents.tryEmit(
					CookbookMembershipEvent.Added(recipeId = recipeId, cookbookId = cookbookId),
				)
			}
		}

	override suspend fun removeRecipeFromCookbook(cookbookId: Int, recipeId: Int) =
		remoteDataSource.removeRecipeFromCookbook(cookbookId, recipeId).also { outcome ->
			if (outcome.getError() == null) {
				cookbookMembershipEvents.tryEmit(
					CookbookMembershipEvent.Removed(recipeId = recipeId, cookbookId = cookbookId),
				)
			}
		}

	override suspend fun getRecipeCookbooks(recipeId: Int) = remoteDataSource.getRecipeCookbooks(recipeId)

	override fun observeCookbookMembershipEvents(): Flow<CookbookMembershipEvent> =
		cookbookMembershipEvents.asSharedFlow()
}
