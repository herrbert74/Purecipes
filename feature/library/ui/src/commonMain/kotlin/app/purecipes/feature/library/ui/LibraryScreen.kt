package app.purecipes.feature.library.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.library.ui.cookbooks.CookbooksTabContent
import app.purecipes.feature.library.ui.cookbooks.CreateCookbookDialog
import app.purecipes.feature.library.ui.cookbooks.DeleteCookbookDialog
import app.purecipes.feature.library.ui.favorites.FavoritesTabContent
import app.purecipes.feature.library.ui.myrecipes.MyRecipesTabContent
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.collections.immutable.toImmutableList

internal const val LIBRARY_TITLE_TAG = "libraryTitle"

@Composable
fun LibraryScreen(
	modifier: Modifier = Modifier,
	sessionKey: String? = null,
	initialCookbookShareToken: String? = null,
	openMyRecipes: Boolean = false,
	recipeSaveMessage: String? = null,
	onRecipeSelect: (Int) -> Unit = {},
	onCookbookSelect: (CookbookSummary) -> Unit = {},
	onCookbookImportSuccess: (cookbookId: Int, name: String, recipeCount: Int) -> Unit = { _, _, _ -> },
	onCreateRecipe: () -> Unit = {},
	onEditCreatedRecipe: (Int) -> Unit = {},
	onRequestLogIn: () -> Unit = {},
	bannerAdViewModel: BannerAdViewModel? = null,
	viewModel: LibraryViewModel =
		assistedMetroViewModel<LibraryViewModel, LibraryViewModel.Factory> {
			create(sessionKey = sessionKey)
		},
) {
	var showCreateCookbookDialog by remember { mutableStateOf(false) }
	var pendingDeleteCookbook by remember { mutableStateOf<CookbookSummary?>(null) }
	val snackbarHostState = remember { SnackbarHostState() }
	val currentOnCookbookImportSuccess by rememberUpdatedState(onCookbookImportSuccess)

	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
		if (sessionKey != null) {
			viewModel.loadLibrary()
		}
	}

	LaunchedEffect(initialCookbookShareToken, sessionKey) {
		val shareToken = initialCookbookShareToken ?: return@LaunchedEffect
		if (sessionKey != null) {
			viewModel.importSharedCookbook(shareToken) { cookbookId, name, recipeCount ->
				currentOnCookbookImportSuccess(cookbookId, name, recipeCount)
			}
		}
	}

	LaunchedEffect(openMyRecipes) {
		if (openMyRecipes) {
			viewModel.onTabSelected(LibraryTab.MyRecipes)
		}
	}

	LaunchedEffect(recipeSaveMessage) {
		val message = recipeSaveMessage ?: return@LaunchedEffect
		snackbarHostState.showSnackbar(message)
	}

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Library", modifier = Modifier.testTag(LIBRARY_TITLE_TAG)) },
			)
		},
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
	) { innerPadding ->
		if (sessionKey == null) {
			LibrarySignedOutContent(
				onRequestLogIn = onRequestLogIn,
				modifier = Modifier.padding(innerPadding),
			)
			return@Scaffold
		}

		if (viewModel.isImportingSharedCookbook) {
			Box(
				modifier = Modifier
					.padding(innerPadding)
					.fillMaxSize(),
				contentAlignment = Alignment.Center,
			) {
				CircularProgressIndicator()
			}
			return@Scaffold
		}

		viewModel.sharedCookbookImportErrorMessage?.let { message ->
			Box(
				modifier = Modifier
					.padding(innerPadding)
					.fillMaxSize()
					.padding(PurecipesTheme.space.l),
				contentAlignment = Alignment.Center,
			) {
				ErrorText(text = message, textAlign = TextAlign.Center)
			}
			return@Scaffold
		}

		Column(modifier = Modifier.padding(innerPadding)) {
			PrimaryTabRow(selectedTabIndex = viewModel.selectedTab.ordinal) {
				Tab(
					selected = viewModel.selectedTab == LibraryTab.Favorites,
					onClick = { viewModel.onTabSelected(LibraryTab.Favorites) },
					text = { Text(text = "Favorites") },
				)
				Tab(
					selected = viewModel.selectedTab == LibraryTab.Cookbooks,
					onClick = { viewModel.onTabSelected(LibraryTab.Cookbooks) },
					text = { Text(text = "Cookbooks") },
				)
				Tab(
					selected = viewModel.selectedTab == LibraryTab.MyRecipes,
					onClick = { viewModel.onTabSelected(LibraryTab.MyRecipes) },
					text = { Text(text = "My recipes") },
				)
			}

			when (viewModel.selectedTab) {
				LibraryTab.Favorites -> FavoritesTabContent(
					errorMessage = viewModel.savedErrorMessage,
					paginationState = viewModel.savedPaginationState,
					recipes = viewModel.savedRecipes,
					totalMatches = viewModel.totalSavedMatches,
					onRecipeSelect = onRecipeSelect,
					bannerAdViewModel = bannerAdViewModel,
					modifier = Modifier.weight(1f),
				)

				LibraryTab.Cookbooks -> CookbooksTabContent(
					cookbooks = viewModel.cookbooks,
					cookbooksErrorMessage = viewModel.cookbooksErrorMessage,
					paginationState = viewModel.cookbooksPaginationState,
					totalMatches = viewModel.totalCookbooksMatches,
					getCookbookCoverUrl = { id -> viewModel.cookbookCoverUrls[id] },
					onRequestCookbookCover = viewModel::loadCookbookCover,
					onCreateClick = { showCreateCookbookDialog = true },
					deleteCookbookError = viewModel.deleteCookbookError,
					isDeletingCookbook = viewModel.isDeletingCookbook,
					onDeleteCookbook = { cookbook, _ ->
						pendingDeleteCookbook = cookbook
					},
					onCookbookClick = onCookbookSelect,
					modifier = Modifier.weight(1f),
				)

				LibraryTab.MyRecipes -> MyRecipesTabContent(
					onCreateRecipe = onCreateRecipe,
					onRecipeSelect = onRecipeSelect,
					onEditRecipe = onEditCreatedRecipe,
					modifier = Modifier.weight(1f),
				)
			}
		}

		if (showCreateCookbookDialog) {
			CreateCookbookDialog(
				existingCookbookNames = viewModel.cookbooks.map { it.name }.toImmutableList(),
				isLoading = viewModel.isCreatingCookbook,
				errorMessage = viewModel.createCookbookError,
				onDismiss = {
					showCreateCookbookDialog = false
				},
				onConfirm = { name ->
					viewModel.createCookbookFromName(name) { ok ->
						if (ok) {
							showCreateCookbookDialog = false
						}
					}
				},
			)
		}
		pendingDeleteCookbook?.let { cookbook ->
			DeleteCookbookDialog(
				cookbookName = cookbook.name,
				onDismiss = { pendingDeleteCookbook = null },
				onConfirm = {
					viewModel.deleteCookbook(cookbook) {
						pendingDeleteCookbook = null
					}
				},
			)
		}
	}
}
