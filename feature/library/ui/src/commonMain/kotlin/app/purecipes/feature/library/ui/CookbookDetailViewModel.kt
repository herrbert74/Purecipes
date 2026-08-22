package app.purecipes.feature.library.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveRecipeFromCookbookUseCase
import app.purecipes.feature.sharing.domain.usecase.ShareCookbookUseCase
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.paging.PaginationState
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock

private const val FIRST_PAGE_NUMBER = 1

private const val PAGE_SIZE = 20

private const val COVER_FETCH_PAGE_SIZE = 50

@AssistedInject
class CookbookDetailViewModel(
	private val getCookbookRecipesPage: GetCookbookRecipesPageUseCase,
	private val removeRecipeFromCookbookUseCase: RemoveRecipeFromCookbookUseCase,
	private val getCookbookCoverImageUrl: GetCookbookCoverImageUrlUseCase,
	private val observeFavoriteEvents: ObserveFavoriteEventsUseCase,
	private val shareCookbook: ShareCookbookUseCase,
	private val trackEvent: TrackEventUseCase,
	@Assisted private val cookbookId: Int,
	@Assisted private val initialName: String,
	@Assisted sessionKey: String?,
) : ViewModel() {

	private var activeSessionKey: String? = sessionKey
	private var favoriteEventsJob: Job? = null
	private var hasTrackedOpen = false

	val title: String get() = initialName

	var coverUrl by mutableStateOf<String?>(null)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var totalMatches by mutableIntStateOf(0)
		private set

	val recipes = mutableStateListOf<RecipeSummary>()

	val paginationState: PaginationState<Int, RecipeSummary> = PaginationState(
		initialPageKey = FIRST_PAGE_NUMBER,
		onRequestPage = { pageKey ->
			viewModelScope.launch {
				loadPage(pageKey)
			}
		},
	)

	init {
		startFavoriteEventsCollection()
		viewModelScope.launch {
			loadPage(FIRST_PAGE_NUMBER)
		}
	}

	fun onSessionKeyChanged(sessionKey: String?) {
		if (sessionKey == activeSessionKey) {
			return
		}
		activeSessionKey = sessionKey
		favoriteEventsJob?.cancel()
		favoriteEventsJob = null
		startFavoriteEventsCollection()
	}

	fun loadCookbookCover() {
		viewModelScope.launch {
			val page = getCookbookRecipesPage(cookbookId, FIRST_PAGE_NUMBER, COVER_FETCH_PAGE_SIZE).get()
				?: return@launch
			val urls = page.items.mapNotNull { it.imageUrl?.trim()?.takeIf { url -> url.isNotEmpty() } }
			coverUrl = getCookbookCoverImageUrl(
				cookbookId = cookbookId,
				candidateImageUrls = urls,
				nowMillis = Clock.System.now().toEpochMilliseconds(),
				random = Random.Default,
			)
		}
	}

	fun removeRecipe(recipe: RecipeSummary, onDone: (Boolean) -> Unit = {}) {
		viewModelScope.launch {
			val outcome = removeRecipeFromCookbookUseCase(cookbookId, recipe.id)
			val ok = outcome.getError() == null
			if (ok) {
				trackEvent(
					AnalyticsEvent.RecipeRemovedFromCookbook(
						recipeId = recipe.id,
						recipeName = recipe.title,
						cookbookId = cookbookId,
						cookbookName = initialName.takeIf { it.isNotBlank() },
						origin = AnalyticsOrigin.FAVORITES,
						isPrivate = recipe.isPrivate,
					),
				)
				recipes.removeAll { it.id == recipe.id }
				totalMatches = (totalMatches - 1).coerceAtLeast(0)
			} else {
				errorMessage = outcome.getError()?.message
			}
			onDone(ok)
		}
	}

	fun shareCookbook() {
		viewModelScope.launch {
			shareCookbook(
				cookbookId = cookbookId,
				recipeCount = totalMatches,
				title = initialName.takeIf { it.isNotBlank() },
			)
			trackEvent(
				AnalyticsEvent.CookbookShared(
					cookbookId = cookbookId,
					cookbookName = initialName.takeIf { it.isNotBlank() },
					origin = AnalyticsOrigin.FAVORITES,
				),
			)
		}
	}

	private fun startFavoriteEventsCollection() {
		if (activeSessionKey == null) {
			return
		}
		favoriteEventsJob = viewModelScope.launch {
			observeFavoriteEvents().collect {
				refreshDetail()
			}
		}
	}

	private fun refreshDetail() {
		viewModelScope.launch {
			recipes.clear()
			totalMatches = 0
			errorMessage = null
			paginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
			loadPage(FIRST_PAGE_NUMBER)
		}
	}

	private suspend fun loadPage(pageNumber: Int) {
		val outcome = getCookbookRecipesPage(cookbookId, pageNumber, PAGE_SIZE)
		val page = outcome.get()
		if (page != null) {
			if (pageNumber == FIRST_PAGE_NUMBER) {
				recipes.clear()
				totalMatches = page.totalMatches
				trackCookbookOpenedIfNeeded(recipeCount = page.totalMatches)
			}
			recipes.addAll(page.items)
			val nextPageKey = pageNumber + 1
			val isLastPage = (page.pageNumber * page.pageSize) >= page.totalMatches
			paginationState.appendPage(
				pageKey = pageNumber,
				items = page.items,
				nextPageKey = nextPageKey,
				isLastPage = isLastPage,
			)
		} else {
			val err = outcome.getError()
			if (err != null) {
				paginationState.setError(IllegalStateException(err.message))
				errorMessage = err.message
			}
		}
	}

	private fun trackCookbookOpenedIfNeeded(recipeCount: Int) {
		if (hasTrackedOpen) {
			return
		}
		hasTrackedOpen = true
		trackEvent(
			AnalyticsEvent.CookbookOpened(
				cookbookId = cookbookId,
				cookbookName = initialName,
				recipeCount = recipeCount,
			),
		)
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(
			cookbookId: Int,
			initialName: String,
			sessionKey: String?,
		): CookbookDetailViewModel
	}
}
