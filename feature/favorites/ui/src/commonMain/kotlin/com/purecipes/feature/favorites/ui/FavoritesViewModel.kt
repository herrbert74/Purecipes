package com.purecipes.feature.favorites.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.DeleteCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbookCoverImageUrlUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import com.purecipes.shared.domain.model.CookbookSummary
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.ui.component.paging.PaginationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val FIRST_PAGE_NUMBER = 1

private const val PAGE_SIZE = 20

private const val COVER_FETCH_PAGE_SIZE = 50

internal enum class FavoritesTab {
	SavedRecipes,
	Cookbooks,
}

internal class FavoritesViewModel(
	private val getFavoriteRecipesPage: GetFavoriteRecipesPageUseCase,
	private val getCookbooksPage: GetCookbooksPageUseCase,
	private val createCookbook: CreateCookbookUseCase,
	private val deleteCookbookUseCase: DeleteCookbookUseCase,
	private val getCookbookRecipesPage: GetCookbookRecipesPageUseCase,
	private val getCookbookCoverImageUrl: GetCookbookCoverImageUrlUseCase,
	coroutineScope: CoroutineScope? = null,
) : ViewModel() {

	private val ownsCoroutineScope = coroutineScope == null
	private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

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
			scope.launch {
				loadSavedPage(pageKey)
			}
		},
	)

	val cookbooksPaginationState: PaginationState<Int, CookbookSummary> = PaginationState(
		initialPageKey = FIRST_PAGE_NUMBER,
		onRequestPage = { pageKey ->
			scope.launch {
				loadCookbooksPage(pageKey)
			}
		},
	)

	val cookbookDetailPaginationState: PaginationState<Int, RecipeSummary> = PaginationState(
		initialPageKey = FIRST_PAGE_NUMBER,
		onRequestPage = { pageKey ->
			scope.launch {
				loadCookbookDetailPage(pageKey)
			}
		},
	)

	fun onTabSelected(tab: FavoritesTab) {
		selectedTab = tab
	}

	fun openCookbookDetail(cookbookId: Int, name: String) {
		viewingCookbookId = cookbookId
		viewingCookbookName = name
		cookbookDetailRecipes.clear()
		totalCookbookDetailMatches = 0
		cookbookDetailErrorMessage = null
		cookbookDetailPaginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
		scope.launch {
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
		scope.launch {
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

	fun loadCookbookCover(cookbookId: Int) {
		scope.launch {
			val page = getCookbookRecipesPage(cookbookId, FIRST_PAGE_NUMBER, COVER_FETCH_PAGE_SIZE).get()
				?: return@launch
			val urls = page.items.mapNotNull { it.imageUrl?.trim()?.takeIf { u -> u.isNotEmpty() } }
			val url = getCookbookCoverImageUrl(
				cookbookId = cookbookId,
				candidateImageUrls = urls,
				nowMillis = System.currentTimeMillis(),
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
		scope.launch {
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
		scope.launch {
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

	override fun onCleared() {
		if (ownsCoroutineScope) {
			scope.cancel()
		}
	}
}

@Composable
internal fun favoritesViewModel(
	getFavoriteRecipesPage: GetFavoriteRecipesPageUseCase,
	getCookbooksPage: GetCookbooksPageUseCase,
	createCookbook: CreateCookbookUseCase,
	deleteCookbook: DeleteCookbookUseCase,
	getCookbookRecipesPage: GetCookbookRecipesPageUseCase,
	getCookbookCoverImageUrl: GetCookbookCoverImageUrlUseCase,
	sessionKey: String?,
): FavoritesViewModel {
	return viewModel(
		key = "FavoritesViewModel:${getFavoriteRecipesPage.hashCode()}:${sessionKey ?: "signed-out"}",
		factory = viewModelFactory {
			initializer {
				FavoritesViewModel(
					getFavoriteRecipesPage = getFavoriteRecipesPage,
					getCookbooksPage = getCookbooksPage,
					createCookbook = createCookbook,
					deleteCookbookUseCase = deleteCookbook,
					getCookbookRecipesPage = getCookbookRecipesPage,
					getCookbookCoverImageUrl = getCookbookCoverImageUrl,
				)
			}
		},
	)
}
