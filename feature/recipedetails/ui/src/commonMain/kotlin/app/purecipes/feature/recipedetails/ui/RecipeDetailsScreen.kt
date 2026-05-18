package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.favorites.domain.CookbookNameSuggestions
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
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.PurecipesTextButton
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
				title = { Text(text = "Recipe details") },
				actions = {
					Row(verticalAlignment = Alignment.CenterVertically) {
						IconButton(
							onClick = viewModel::toggleFavorite,
							enabled = canManageFavorites && viewModel.recipeDetails != null &&
								!viewModel.isFavoriteUpdating,
						) {
							Icon(
								imageVector = if (viewModel.recipeDetails?.isFavorite == true) {
									Icons.Filled.Favorite
								} else {
									Icons.Outlined.FavoriteBorder
								},
								contentDescription = if (viewModel.recipeDetails?.isFavorite == true) {
									"Remove from favorites"
								} else {
									"Add to favorites"
								},
								tint = if (viewModel.recipeDetails?.isFavorite == true) {
									PurecipesTheme.colorScheme.primary
								} else {
									PurecipesTheme.colorScheme.onSurfaceVariant
								},
							)
						}
						PurecipesTextButton(
							text = "Add to cookbook",
							onClick = {
								viewModel.prepareCookbookPicker()
								showCookbookSheet = true
							},
							modifier = Modifier,
							enabled = canManageFavorites && viewModel.recipeDetails?.isFavorite == true &&
								!viewModel.isFavoriteUpdating,
						)
					}
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

		if (showCookbookSheet) {
			val existingCookbookNamesNormalized = remember(viewModel.sheetCookbooks) {
				viewModel.sheetCookbooks
					.map { it.name.trim().lowercase() }
					.toSet()
			}
			val suggestionNames = remember(existingCookbookNamesNormalized) {
				CookbookNameSuggestions.values.filter { suggestion ->
					suggestion.trim().lowercase() !in existingCookbookNamesNormalized
				}
			}
			ModalBottomSheet(
				onDismissRequest = {
					showCookbookSheet = false
					newCookbookName = ""
				},
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(PurecipesTheme.space.m),
					verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
				) {
					Text(
						text = "Add to cookbook",
						style = PurecipesTheme.typography.titleMedium,
					)
					LazyRow(horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
						items(suggestionNames, key = { it }) { suggestion ->
							FilterChip(
								selected = false,
								onClick = { newCookbookName = suggestion },
								label = { Text(text = suggestion) },
							)
						}
					}
					OutlinedTextField(
						value = newCookbookName,
						onValueChange = { newCookbookName = it },
						modifier = Modifier.fillMaxWidth(),
						label = { Text(text = "New cookbook name") },
						singleLine = true,
					)
					viewModel.sheetCookbooks.forEach { cookbook ->
						TextButton(
							onClick = {
								viewModel.addRecipeToCookbookId(cookbook.id) { err ->
									if (err == null) {
										showCookbookSheet = false
									}
								}
							},
							enabled = !viewModel.isCookbookActionInFlight,
						) {
							Text(text = cookbook.name)
						}
					}
					viewModel.cookbookActionError?.let { ErrorText(text = it) }
					Button(
						onClick = {
							viewModel.createCookbookAndAdd(newCookbookName) { err ->
								if (err == null) {
									showCookbookSheet = false
									newCookbookName = ""
								}
							}
						},
						enabled = !viewModel.isCookbookActionInFlight && newCookbookName.trim().isNotEmpty(),
					) {
						Text(text = "Create and add")
					}
				}
			}
		}
	}
}

@Composable
private fun RecipeDetailsMessageScreen(
	message: String,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.padding(PurecipesTheme.space.l),
		contentAlignment = Alignment.Center,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Text(
				text = message,
				style = PurecipesTheme.typography.bodyLarge,
			)
			Spacer(modifier = Modifier.height(PurecipesTheme.space.xs))
			TextButton(onClick = onBack) {
				Text(text = "Back to search")
			}
		}
	}
}
