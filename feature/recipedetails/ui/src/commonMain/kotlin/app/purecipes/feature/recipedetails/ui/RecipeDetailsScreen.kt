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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.shared.ui.component.BackNavigationButton
import app.purecipes.shared.ui.theme.PurecipesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
	recipeId: Int,
	addFavoriteRecipe: AddFavoriteRecipeUseCase,
	addRecipeToCookbook: AddRecipeToCookbookUseCase,
	canManageFavorites: Boolean,
	createCookbook: CreateCookbookUseCase,
	getCookbooksPage: GetCookbooksPageUseCase,
	getRecipeCookbooks: GetRecipeCookbooksUseCase,
	getRecipeDetails: GetRecipeDetailsUseCase,
	getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	markMeasurementMismatchSeen: MarkMeasurementMismatchSeenUseCase,
	onOpenMeasurementPreferences: () -> Unit,
	processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	trackEvent: TrackEventUseCase,
	onBack: () -> Unit,
	onFavoriteChange: () -> Unit,
	onStartCooking: (Int) -> Unit,
	removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
	sessionKey: String?,
	modifier: Modifier = Modifier,
) {
	val viewModel = recipeDetailsViewModel(
		recipeId = recipeId,
		addFavoriteRecipe = addFavoriteRecipe,
		getRecipeDetails = getRecipeDetails,
		getMeasurementPreferences = getMeasurementPreferences,
		markMeasurementMismatchSeen = markMeasurementMismatchSeen,
		processRecipeDetailsForMeasurementPreferences = processRecipeDetailsForMeasurementPreferences,
		removeFavoriteRecipe = removeFavoriteRecipe,
		trackEvent = trackEvent,
		sessionKey = sessionKey,
		getRecipeCookbooks = getRecipeCookbooks,
		getCookbooksPage = getCookbooksPage,
		createCookbook = createCookbook,
		addRecipeToCookbook = addRecipeToCookbook,
	)
	val currentOnFavoriteChange by rememberUpdatedState(onFavoriteChange)
	var showCookbookSheet by remember { mutableStateOf(false) }
	var showNutritionDialog by remember { mutableStateOf(false) }
	var newCookbookName by remember { mutableStateOf("") }

	LaunchedEffect(viewModel.favoriteChangeCount) {
		if (viewModel.favoriteChangeCount > 0) {
			currentOnFavoriteChange()
		}
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
				)
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
