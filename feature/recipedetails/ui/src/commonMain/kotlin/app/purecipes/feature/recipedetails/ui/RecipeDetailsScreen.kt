package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.purecipes.feature.ads.ui.BannerAd
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.shared.ui.component.ShowLoading
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
	recipeId: Int,
	canManageFavorites: Boolean,
	onOpenMeasurementPreferences: () -> Unit,
	onBack: () -> Unit,
	onStartCooking: (Int) -> Unit,
	modifier: Modifier = Modifier,
	transitionKey: String? = null,
	sessionKey: String? = null,
	origin: String = AnalyticsOrigin.SEARCH.value,
	bannerAdViewModel: BannerAdViewModel? = null,
	viewModel: RecipeDetailsViewModel = assistedMetroViewModel<RecipeDetailsViewModel, RecipeDetailsViewModel.Factory>(
		key = "$recipeId-$origin",
	) {
		create(recipeId = recipeId, sessionKey = sessionKey, origin = origin)
	},
) {
	var showCookbookSheet by rememberSaveable { mutableStateOf(false) }
	var showNutritionDialog by remember { mutableStateOf(false) }
	var newCookbookName by rememberSaveable { mutableStateOf("") }
	val showBackNavigation = rememberShowRecipeDetailsBackNavigation()
	val listState = rememberLazyListState()
	val topBarAlpha by remember {
		derivedStateOf {
			if (listState.firstVisibleItemIndex > 0) {
				1f
			} else {
				(listState.firstVisibleItemScrollOffset / TOP_BAR_SCROLL_RANGE).coerceIn(0f, 1f)
			}
		}
	}
	val titleAlpha by remember {
		derivedStateOf {
			when {
				listState.firstVisibleItemIndex == 0 -> 0f
				listState.firstVisibleItemIndex == 1 -> {
					(listState.firstVisibleItemScrollOffset / TITLE_FADE_RANGE).coerceIn(0f, 1f)
				}

				else -> 1f
			}
		}
	}
	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
	}

	LaunchedEffect(recipeId, sessionKey) {
		viewModel.onScreenVisible()
	}

	val edgeSwipeBackState = rememberRecipeDetailsEdgeSwipeBackState(
		enabled = showBackNavigation,
		onDismiss = onBack,
	)

	Box(
		modifier = modifier
			.fillMaxSize()
			.recipeDetailsEdgeSwipeBack(edgeSwipeBackState),
	) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			bottomBar = {
				BannerAd(viewModel = bannerAdViewModel)
			},
			contentWindowInsets = WindowInsets.safeDrawing.only(
				WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
			),
		) { innerPadding ->
			if (viewModel.showMeasurementMismatchDialog) {
				AlertDialog(
					onDismissRequest = viewModel::dismissMeasurementMismatchDialog,
					confirmButton = {
						Button(onClick = viewModel::convertCurrentRecipe) {
							Text(text = "Convert recipe")
						}
					},
					dismissButton = {
						TextButton(onClick = viewModel::dismissMeasurementMismatchDialog) {
							Text(text = "Keep original")
						}
					},
					title = { Text(text = "Measurement system mismatch") },
					text = {
						Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
							Text(text = "This recipe uses measurements outside your preferred system.")
							TextButton(onClick = onOpenMeasurementPreferences) {
								Text(text = "Update my preferences")
							}
						}
					},
				)
			}
			when {
				viewModel.isLoading -> ShowLoading(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding)
						.padding(top = LOADING_TOP_PADDING),
				)

				viewModel.errorMessage != null -> RecipeDetailsMessageScreen(
					message = viewModel.errorMessage ?: "Unknown error",
					onBack = onBack,
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding)
						.padding(top = LOADING_TOP_PADDING),
				)

				viewModel.recipeDetails != null -> RecipeDetailsContent(
					canManageFavorites = canManageFavorites,
					favoriteErrorMessage = viewModel.favoriteErrorMessage,
					isFavoriteUpdating = viewModel.isFavoriteUpdating,
					isRecipeConverted = viewModel.isRecipeConverted,
					recipe = viewModel.recipeDetails ?: return@Scaffold,
					recipeCookbooks = RecipeCookbooksList(viewModel.recipeCookbooks.toList()),
					showNutrition = viewModel.recipeDetails?.nutrition?.hasDisplayableData() == true,
					onShowNutrition = { showNutritionDialog = true },
					onShowCookbookSheet = {
						viewModel.prepareCookbookPicker()
						showCookbookSheet = true
					},
					onStartCooking = { onStartCooking(recipeId) },
					onToggleFavorite = viewModel::toggleFavorite,
					modifier = Modifier.fillMaxSize(),
					transitionKey = transitionKey,
					bottomPadding = innerPadding.calculateBottomPadding(),
					listState = listState,
				)

				else -> RecipeDetailsMessageScreen(
					message = "Recipe not found",
					onBack = onBack,
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding)
						.padding(top = LOADING_TOP_PADDING),
				)
			}
		}

		RecipeDetailsTopBar(
			title = viewModel.recipeDetails?.title ?: "Recipe details",
			showBackNavigation = showBackNavigation,
			isPrivate = viewModel.recipeDetails?.isPrivate == true,
			onBack = onBack,
			onShare = viewModel::shareCurrentRecipe,
			containerColor = if (viewModel.recipeDetails != null) {
				PurecipesTheme.colorScheme.surface.copy(alpha = topBarAlpha)
			} else {
				PurecipesTheme.colorScheme.surface
			},
			titleAlpha = if (viewModel.recipeDetails != null) {
				titleAlpha
			} else {
				1f
			},
		)

		RecipeDetailsNutritionOverlay(
			nutrition = viewModel.recipeDetails?.nutrition,
			showDialog = showNutritionDialog,
			onDismiss = { showNutritionDialog = false },
		)

		RecipeDetailsCookbookSheet(
			showSheet = showCookbookSheet,
			sheetCookbooks = RecipeSheetCookbooksList(viewModel.sheetCookbooks.toList()),
			cookbookActionError = viewModel.cookbookActionError,
			isCookbookActionInFlight = viewModel.isCookbookActionInFlight,
			newCookbookName = newCookbookName,
			onNewCookbookNameChange = { newCookbookName = it },
			onDismiss = {
				showCookbookSheet = false
				newCookbookName = ""
			},
			onAddToCookbook = { cookbookId, onComplete ->
				viewModel.addRecipeToCookbookId(cookbookId, onComplete)
			},
			onCreateCookbookAndAdd = { name, onComplete ->
				viewModel.createCookbookAndAdd(name, onComplete)
			},
		)
	}
}

private const val TITLE_FADE_RANGE = 120f
private const val TOP_BAR_SCROLL_RANGE = 400f
private val LOADING_TOP_PADDING = 64.dp
