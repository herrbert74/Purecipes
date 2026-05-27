package app.purecipes.feature.favorites.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.DeleteCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.feature.favorites.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.sharing.domain.usecase.ImportCookbookShareUseCase
import app.purecipes.feature.sharing.domain.usecase.ShareCookbookUseCase
import app.purecipes.shared.domain.model.CookbookSummary
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
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock

private const val FIRST_PAGE_NUMBER = 1

private const val PAGE_SIZE = 20

private const val COVER_FETCH_PAGE_SIZE = 50

enum class FavoritesTab {
	SavedRecipes,
	Cookbooks,
}

@AssistedInject
class FavoritesViewModel(
	private val getFavoriteRecipesPage: GetFavoriteRecipesPageUseCase,
	private val getCookbooksPage: GetCookbooksPageUseCase,
	private val createCookbook: CreateCookbookUseCase,
	private val deleteCookbookUseCase: DeleteCookbookUseCase,
	private val getCookbookRecipesPage: GetCookbookRecipesPageUseCase,
	private val getCookbookCoverImageUrl: GetCookbookCoverImageUrlUseCase,
	private val importCookbookShare: ImportCookbookShareUseCase,
	private val shareCookbook: ShareCookbookUseCase,
	private val observeFavoriteEvents: ObserveFavoriteEventsUseCase,
	@Assisted private val sessionKey: String?,
) : ViewModel() {

	init {
		if (sessionKey != null) {
			viewModelScope.launch {
				observeFavoriteEvents().collect {
					loadFavorites()
				}
			}
		}
	}

	var selectedTab by mutableStateOf(FavoritesTab.SavedRecipes)
		private set

	var viewingCookbookId by mutableStateOf<Int?>(null)
		private set

	var viewingCookbookName by mutableStateOf("")
		private set

	var isInitialLoading by mutableStateOf(false)
		private set

	var savedErrorMessage by mutableStateOf<String?>(null)
		private set

	var cookbooksErrorMessage by mutableStateOf<String?>(null)
		private set

	var cookbookDetailErrorMessage by mutableStateOf<String?>(null)
		private set

	var isImportingSharedCookbook by mutableStateOf(false)
		private set

	var sharedCookbookImportErrorMessage by mutableStateOf<String?>(null)
		private set

	var totalSavedMatches by mutableIntStateOf(0)
		private set

	var totalCookbooksMatches by mutableIntStateOf(0)
		private set

	var totalCookbookDetailMatches by mutableIntStateOf(0)
		private set

	var createCookbookError by mutableStateOf<String?>(null)
		private set

	var isCreatingCookbook by mutableStateOf(false)
		private set

	var deleteCookbookError by mutableStateOf<String?>(null)
		private set

	var isDeletingCookbook by mutableStateOf(false)
		private set

	val savedRecipes = mutableStateListOf<RecipeSummary>()
	val cookbooks = mutableStateListOf<CookbookSummary>()
	val cookbookDetailRecipes = mutableStateListOf<RecipeSummary>()

	val cookbookCoverUrls = mutableStateMapOf<Int, String?>()

	val savedPaginationState: PaginationState<Int, RecipeSummary> = PaginationState(
		initialPageKey = FIRST_PAGE_NUMBER,
		onRequestPage = { pageKey ->
			viewModelScope.launch {
				loadSavedPage(pageKey)
			}
		},
	)

	val cookbooksPaginationState: PaginationState<Int, CookbookSummary> = PaginationState(
		initialPageKey = FIRST_PAGE_NUMBER,
		onRequestPage = { pageKey ->
			viewModelScope.launch {
				loadCookbooksPage(pageKey)
			}
		},
	)

	val cookbookDetailPaginationState: PaginationState<Int, RecipeSummary> = PaginationState(
		initialPageKey = FIRST_PAGE_NUMBER,
		onRequestPage = { pageKey ->
			viewModelScope.launch {
				loadCookbookDetailPage(pageKey)
			}
		},
	)

	fun onTabSelected(tab: FavoritesTab) {
		selectedTab = tab
		sharedCookbookImportErrorMessage = null
	}

	fun openCookbookDetail(cookbookId: Int, name: String) {
		viewingCookbookId = cookbookId
		viewingCookbookName = name
		cookbookDetailRecipes.clear()
		totalCookbookDetailMatches = 0
		cookbookDetailErrorMessage = null
		cookbookDetailPaginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
		viewModelScope.launch {
			loadCookbookDetailPage(FIRST_PAGE_NUMBER)
		}
	}

	fun closeCookbookDetail() {
		viewingCookbookId = null
		viewingCookbookName = ""
		cookbookDetailRecipes.clear()
		cookbookDetailPaginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
	}

	fun loadFavorites() {
		if (sessionKey == null) {
			return
		}
		viewModelScope.launch {
			isInitialLoading = true
			savedErrorMessage = null
			cookbooksErrorMessage = null
			savedRecipes.clear()
			totalSavedMatches = 0
			cookbooks.clear()
			totalCookbooksMatches = 0
			savedPaginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
			cookbooksPaginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
			loadSavedPage(FIRST_PAGE_NUMBER)
			loadCookbooksPage(FIRST_PAGE_NUMBER)
			refreshCookbookDetailIfOpen()
			isInitialLoading = false
		}
	}

	private suspend fun refreshCookbookDetailIfOpen() {
		if (viewingCookbookId == null) {
			return
		}
		cookbookDetailRecipes.clear()
		totalCookbookDetailMatches = 0
		cookbookDetailErrorMessage = null
		cookbookDetailPaginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
		loadCookbookDetailPage(FIRST_PAGE_NUMBER)
	}

	fun importSharedCookbook(shareToken: String) {
		if (sessionKey == null) {
			return
		}
		viewModelScope.launch {
			isImportingSharedCookbook = true
			sharedCookbookImportErrorMessage = null
			val outcome = importCookbookShare(shareToken)
			val result = outcome.get()
			isImportingSharedCookbook = false
			if (result != null) {
				selectedTab = FavoritesTab.Cookbooks
				cookbooksPaginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
				loadCookbooksPage(FIRST_PAGE_NUMBER)
				openCookbookDetail(result.cookbook.id, result.cookbook.name)
			} else {
				sharedCookbookImportErrorMessage = outcome.getError()?.message
			}
		}
	}

	fun loadCookbookCover(cookbookId: Int) {
		viewModelScope.launch {
			val page = getCookbookRecipesPage(cookbookId, FIRST_PAGE_NUMBER, COVER_FETCH_PAGE_SIZE).get()
				?: return@launch
			val urls = page.items.mapNotNull { it.imageUrl?.trim()?.takeIf { u -> u.isNotEmpty() } }
			val url = getCookbookCoverImageUrl(
				cookbookId = cookbookId,
				candidateImageUrls = urls,
				nowMillis = Clock.System.now().toEpochMilliseconds(),
				random = Random.Default,
			)
			cookbookCoverUrls[cookbookId] = url
		}
	}

	fun createCookbookFromName(name: String, onDone: (Boolean) -> Unit) {
		val trimmed = name.trim()
		if (trimmed.isEmpty()) {
			onDone(false)
			return
		}
		if (cookbooks.any { it.name.trim().equals(trimmed, ignoreCase = true) }) {
			createCookbookError = "Cookbook already exists"
			onDone(false)
			return
		}
		viewModelScope.launch {
			isCreatingCookbook = true
			createCookbookError = null
			val outcome = createCookbook(trimmed)
			val ok = outcome.getError() == null
			if (ok) {
				cookbooksPaginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
				loadCookbooksPage(FIRST_PAGE_NUMBER)
				selectedTab = FavoritesTab.Cookbooks
			} else {
				createCookbookError = outcome.getError()?.message
			}
			isCreatingCookbook = false
			onDone(ok)
		}
	}

	fun deleteCookbook(cookbook: CookbookSummary, onDone: (Boolean) -> Unit) {
		if (cookbook.recipeCount > 0) {
			deleteCookbookError = "Only empty cookbooks can be deleted"
			onDone(false)
			return
		}
		viewModelScope.launch {
			isDeletingCookbook = true
			deleteCookbookError = null
			val outcome = deleteCookbookUseCase(cookbook.id)
			val ok = outcome.getError() == null
			if (ok) {
				cookbooksPaginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
				loadCookbooksPage(FIRST_PAGE_NUMBER)
			} else {
				deleteCookbookError = outcome.getError()?.message
			}
			isDeletingCookbook = false
			onDone(ok)
		}
	}

	fun shareOpenCookbook() {
		val cookbookId = viewingCookbookId ?: return
		val title = viewingCookbookName.takeIf { it.isNotBlank() }
		viewModelScope.launch {
			shareCookbook(
				cookbookId = cookbookId,
				recipeCount = totalCookbookDetailMatches,
				title = title,
			)
		}
	}

	private suspend fun loadSavedPage(pageNumber: Int) {
		val outcome = getFavoriteRecipesPage(pageNumber, PAGE_SIZE)
		val page = outcome.get()
		if (page != null) {
			if (pageNumber == FIRST_PAGE_NUMBER) {
				savedRecipes.clear()
				totalSavedMatches = page.totalMatches
			}
			savedRecipes.addAll(page.items)
			val nextPageKey = pageNumber + 1
			val isLastPage = (page.pageNumber * page.pageSize) >= page.totalMatches
			savedPaginationState.appendPage(
				pageKey = pageNumber,
				items = page.items,
				nextPageKey = nextPageKey,
				isLastPage = isLastPage,
			)
		} else {
			val err = outcome.getError()
			if (err != null) {
				savedPaginationState.setError(IllegalStateException(err.message))
				savedErrorMessage = err.message
			}
		}
	}

	private suspend fun loadCookbooksPage(pageNumber: Int) {
		val outcome = getCookbooksPage(pageNumber, PAGE_SIZE)
		val page = outcome.get()
		if (page != null) {
			if (pageNumber == FIRST_PAGE_NUMBER) {
				cookbooks.clear()
				totalCookbooksMatches = page.totalMatches
			}
			cookbooks.addAll(page.items)
			val nextPageKey = pageNumber + 1
			val isLastPage = (page.pageNumber * page.pageSize) >= page.totalMatches
			cookbooksPaginationState.appendPage(
				pageKey = pageNumber,
				items = page.items,
				nextPageKey = nextPageKey,
				isLastPage = isLastPage,
			)
		} else {
			val err = outcome.getError()
			if (err != null) {
				cookbooksPaginationState.setError(IllegalStateException(err.message))
				cookbooksErrorMessage = err.message
			}
		}
	}

	private suspend fun loadCookbookDetailPage(pageNumber: Int) {
		val cookbookId = viewingCookbookId ?: return
		val outcome = getCookbookRecipesPage(cookbookId, pageNumber, PAGE_SIZE)
		val page = outcome.get()
		if (page != null) {
			if (pageNumber == FIRST_PAGE_NUMBER) {
				cookbookDetailRecipes.clear()
				totalCookbookDetailMatches = page.totalMatches
			}
			cookbookDetailRecipes.addAll(page.items)
			val nextPageKey = pageNumber + 1
			val isLastPage = (page.pageNumber * page.pageSize) >= page.totalMatches
			cookbookDetailPaginationState.appendPage(
				pageKey = pageNumber,
				items = page.items,
				nextPageKey = nextPageKey,
				isLastPage = isLastPage,
			)
		} else {
			val err = outcome.getError()
			if (err != null) {
				cookbookDetailPaginationState.setError(IllegalStateException(err.message))
				cookbookDetailErrorMessage = err.message
			}
		}
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(sessionKey: String?): FavoritesViewModel
	}
}
