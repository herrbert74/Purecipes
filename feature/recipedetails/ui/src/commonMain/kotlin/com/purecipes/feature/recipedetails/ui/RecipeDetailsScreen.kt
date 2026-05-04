package com.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.favorites.domain.CookbookNameSuggestions
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.CookbookRef
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.ui.component.BackNavigationButton
import com.purecipes.shared.ui.component.ErrorText
import com.purecipes.shared.ui.component.PurecipesTextButton
import com.purecipes.shared.ui.theme.PurecipesTheme

internal const val RECIPE_DETAILS_CONTENT_TAG = "recipeDetailsContent"

@Immutable
private data class RecipeCookbooksList(val items: List<CookbookRef>)

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
						items(CookbookNameSuggestions.values, key = { it }) { suggestion ->
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
private fun RecipeDetailsContent(
	canManageFavorites: Boolean,
	favoriteErrorMessage: String?,
	isFavoriteUpdating: Boolean,
	isRecipeConverted: Boolean,
	recipe: RecipeDetails,
	recipeCookbooks: RecipeCookbooksList,
	onStartCooking: () -> Unit,
	onToggleFavorite: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val cookbookRefs = recipeCookbooks.items
	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.testTag(RECIPE_DETAILS_CONTENT_TAG),
		contentPadding = PaddingValues(
			start = PurecipesTheme.space.m,
			top = PurecipesTheme.space.m,
			end = PurecipesTheme.space.m,
			bottom = PurecipesTheme.space.l,
		),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		item {
			AsyncImage(
				model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = recipe.title,
				modifier = Modifier
					.fillMaxWidth()
					.height(240.dp)
					.clip(RoundedCornerShape(PurecipesTheme.space.l))
					.background(PurecipesTheme.colorScheme.surfaceContainerLow),
				contentScale = ContentScale.Crop,
			)
		}

		item {
			Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
				Text(
					text = recipe.title,
					style = PurecipesTheme.typography.headlineMedium,
				)
				Text(
					text = recipe.description,
					style = PurecipesTheme.typography.bodyLarge,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
		}

		item {
			RecipeMetadataRow(recipe = recipe, isRecipeConverted = isRecipeConverted)
		}

		item {
			if (cookbookRefs.isNotEmpty()) {
				LazyRow(
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
					modifier = Modifier.fillMaxWidth(),
				) {
					items(cookbookRefs.size, key = { cookbookRefs[it].id }) { index ->
						val cookbook = cookbookRefs[index]
						Surface(
							shape = RoundedCornerShape(999.dp),
							color = PurecipesTheme.colorScheme.secondaryContainer,
						) {
							Text(
								text = cookbook.name,
								modifier = Modifier.padding(
									horizontal = PurecipesTheme.space.s,
									vertical = PurecipesTheme.space.s,
								),
								style = PurecipesTheme.typography.labelLarge,
							)
						}
					}
				}
			}
		}

		item {
			Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
				Button(
					onClick = onStartCooking,
					enabled = recipe.steps.isNotEmpty(),
					modifier = Modifier.fillMaxWidth(),
				) {
					Text(text = "Start cooking")
				}
				Button(
					onClick = onToggleFavorite,
					enabled = canManageFavorites && !isFavoriteUpdating,
					modifier = Modifier.fillMaxWidth(),
				) {
					Text(
						text = if (!canManageFavorites) {
							"Sign in to save favorites"
						} else if (recipe.isFavorite) {
							"Remove from favorites"
						} else {
							"Add to favorites"
						},
					)
				}
				favoriteErrorMessage?.let {
					ErrorText(text = it)
				}
			}
		}

		item {
			Text(
				text = "Ingredients",
				style = PurecipesTheme.typography.titleLarge,
			)
		}

		items(recipe.ingredientGroups) { group ->
			IngredientGroupCard(group = group)
		}

		item {
			Text(
				text = "Steps",
				style = PurecipesTheme.typography.titleLarge,
			)
		}

		items(recipe.steps.indices.toList()) { stepIndex ->
			StepCard(
				stepNumber = stepIndex + 1,
				step = recipe.steps[stepIndex],
			)
		}
	}
}

@Composable
private fun RecipeMetadataRow(recipe: RecipeDetails, isRecipeConverted: Boolean) {
	val items = listOfNotNull(
		recipe.cuisine?.displayName,
		recipe.totalTime?.let { "$it min" },
		recipe.yields?.takeIf { it.isNotBlank() },
		recipe.measurementSystem?.displayName(isRecipeConverted),
	)

	if (items.isEmpty()) return

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		items.forEach { item ->
			Surface(
				shape = RoundedCornerShape(999.dp),
				color = PurecipesTheme.colorScheme.secondaryContainer,
			) {
				Text(
					text = item,
					modifier = Modifier.padding(horizontal = PurecipesTheme.space.s, vertical = PurecipesTheme.space.s),
					style = PurecipesTheme.typography.labelLarge,
				)
			}
		}
	}
}

private fun MeasurementSystem.displayName(isRecipeConverted: Boolean): String {
	return when (this) {
		MeasurementSystem.IMPERIAL -> if (isRecipeConverted) "Converted to imperial" else "Imperial"
		MeasurementSystem.METRIC -> if (isRecipeConverted) "Converted to metric" else "Metric"
		MeasurementSystem.MIXED -> "Mixed"
	}
}

@Composable
private fun IngredientGroupCard(group: IngredientGroup) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			group.name?.takeIf { it.isNotBlank() }?.let {
				Text(
					text = it,
					style = PurecipesTheme.typography.titleMedium,
				)
			}

			group.ingredients.forEach { ingredient ->
				Text(
					text = "- $ingredient",
					style = PurecipesTheme.typography.bodyLarge,
				)
			}
		}
	}
}

@Composable
private fun StepCard(stepNumber: Int, step: String) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Row(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Surface(
				modifier = Modifier.size(PurecipesTheme.space.xl),
				shape = RoundedCornerShape(PurecipesTheme.space.m),
				color = PurecipesTheme.colorScheme.primaryContainer,
			) {
				Box(contentAlignment = Alignment.Center) {
					Text(
						text = stepNumber.toString(),
						style = PurecipesTheme.typography.titleMedium,
					)
				}
			}

			Text(
				text = step,
				modifier = Modifier.weight(1f),
				style = PurecipesTheme.typography.bodyLarge,
			)
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
