package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.purecipes.feature.ads.ui.BannerAd
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.sharing.ui.ShareIconButton
import app.purecipes.shared.ui.component.BackNavigationButton
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
	sessionKey: String? = null,
	origin: String = AnalyticsOrigin.SEARCH.value,
	bannerAdViewModel: BannerAdViewModel? = null,
	viewModel: RecipeDetailsViewModel = assistedMetroViewModel<RecipeDetailsViewModel, RecipeDetailsViewModel.Factory>(
		key = "$recipeId-$origin",
	) {
		create(recipeId = recipeId, sessionKey = sessionKey, origin = origin)
	},
) {
	var showCookbookSheet by remember { mutableStateOf(false) }
	var showNutritionDialog by remember { mutableStateOf(false) }
	var newCookbookName by remember { mutableStateOf("") }

	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
	}

	LaunchedEffect(recipeId, sessionKey) {
		viewModel.onScreenVisible()
	}

	Box(modifier = modifier.fillMaxSize()) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = {
						Text(
							text = viewModel.recipeDetails?.title ?: "Recipe details",
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
						)
					},
					navigationIcon = {
						BackNavigationButton(onBack = onBack)
					},
					actions = {
						if (viewModel.recipeDetails?.isPrivate != true) {
							ShareIconButton(
								onShare = viewModel::shareCurrentRecipe,
								contentDescription = "Share recipe",
							)
						}
					},
				)
			},
			bottomBar = {
				BannerAd(viewModel = bannerAdViewModel)
			},
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
				viewModel.isLoading -> Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentAlignment = Alignment.Center,
				) {
					CircularProgressIndicator()
				}

				viewModel.errorMessage != null -> RecipeDetailsMessageScreen(
					message = viewModel.errorMessage ?: "Unknown error",
					onBack = onBack,
					modifier = Modifier.padding(innerPadding),
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
					modifier = Modifier.padding(innerPadding),
				)

				else -> RecipeDetailsMessageScreen(
					message = "Recipe not found",
					onBack = onBack,
					modifier = Modifier.padding(innerPadding),
				)
			}
		}

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
