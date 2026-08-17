package app.purecipes.feature.cooking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeIngredient
import app.purecipes.shared.ui.component.BackNavigationButton
import app.purecipes.shared.ui.component.CenteredMessageContent
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.launch

internal const val STEP_BY_STEP_CURRENT_STEP_TEXT_TAG = "stepByStepCurrentStepText"
internal const val FINISH_COOKING_BUTTON_TAG = "finishCookingButton"

@Composable
fun StepByStepCookingRoute(
	recipeId: Int,
	canManageFavorites: Boolean,
	onBack: () -> Unit,
	onFindMoreRecipes: () -> Unit,
	modifier: Modifier = Modifier,
	sessionKey: String? = null,
	viewModel: StepByStepCookingViewModel =
		assistedMetroViewModel<StepByStepCookingViewModel, StepByStepCookingViewModel.Factory>(
			key = recipeId.toString(),
		) {
			create(recipeId = recipeId, sessionKey = sessionKey)
		},
) {
	var showCookbookSheet by remember { mutableStateOf(false) }
	var newCookbookName by remember { mutableStateOf("") }

	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
	}

	Box(modifier = modifier.fillMaxSize()) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = viewModel.recipeDetails?.title.orEmpty()) },
					navigationIcon = {
						BackNavigationButton(onBack = onBack)
					},
				)
			},
		) { innerPadding ->
			when {
				viewModel.isLoading -> Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentAlignment = Alignment.Center,
				) {
					CircularProgressIndicator()
				}

				viewModel.errorMessage != null -> CenteredMessageContent(
					message = viewModel.errorMessage ?: "Unknown error",
					modifier = Modifier.padding(innerPadding),
				)

				viewModel.recipeDetails == null || viewModel.recipeDetails?.steps.isNullOrEmpty() ->
					CenteredMessageContent(
						message = "No cooking steps available yet.",
						modifier = Modifier.padding(innerPadding),
					)

				else -> StepByStepCookingScreen(
					recipe = viewModel.recipeDetails ?: return@Scaffold,
					canManageFavorites = canManageFavorites,
					currentPageIndex = viewModel.currentPageIndex,
					favoriteErrorMessage = viewModel.favoriteErrorMessage,
					isFavoriteUpdating = viewModel.isFavoriteUpdating,
					onPageChange = viewModel::setCurrentPage,
					onToggleFavorite = viewModel::toggleFavorite,
					onShare = viewModel::shareCurrentRecipe,
					onShowCookbookSheet = {
						viewModel.prepareCookbookPicker()
						showCookbookSheet = true
					},
					onDone = onBack,
					onFindMoreRecipes = onFindMoreRecipes,
					modifier = Modifier.padding(innerPadding),
				)
			}
		}

		CookingCookbookSheet(
			showSheet = showCookbookSheet,
			sheetCookbooks = CookingSheetCookbooksList(viewModel.sheetCookbooks.toList()),
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

@Composable
internal fun StepByStepCookingScreen(
	recipe: RecipeDetails,
	canManageFavorites: Boolean,
	currentPageIndex: Int,
	favoriteErrorMessage: String?,
	isFavoriteUpdating: Boolean,
	onPageChange: (Int) -> Unit,
	onToggleFavorite: () -> Unit,
	onShare: () -> Unit,
	onShowCookbookSheet: () -> Unit,
	onDone: () -> Unit,
	onFindMoreRecipes: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val pagerState = rememberPagerState(
		initialPage = currentPageIndex,
		pageCount = { recipe.steps.size + 1 },
	)
	val currentOnPageChange by rememberUpdatedState(onPageChange)
	val pagerScope = rememberCoroutineScope()
	val isFinishPage = currentPageIndex >= recipe.steps.size
	val finishPageIndex = recipe.steps.size

	LaunchedEffect(pagerState.currentPage) {
		if (pagerState.currentPage != currentPageIndex) {
			currentOnPageChange(pagerState.currentPage)
		}
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.m),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		if (!isFinishPage) {
			Text(
				text = "${currentPageIndex + 1} of ${recipe.steps.size}",
				style = PurecipesTheme.typography.titleMedium,
				color = PurecipesTheme.colorScheme.primary,
			)
			PagerIndicator(
				currentPageIndex = currentPageIndex,
				stepCount = recipe.steps.size,
				modifier = Modifier.fillMaxWidth(),
			)
		}
		HorizontalPager(
			state = pagerState,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
		) { page ->
			if (page >= finishPageIndex) {
				CookingFinishedContent(
					recipe = recipe,
					canManageFavorites = canManageFavorites,
					isFavoriteUpdating = isFavoriteUpdating,
					favoriteErrorMessage = favoriteErrorMessage,
					onToggleFavorite = onToggleFavorite,
					onShare = onShare,
					onShowCookbookSheet = onShowCookbookSheet,
					onDone = onDone,
					onFindMoreRecipes = onFindMoreRecipes,
				)
			} else {
				Column(
					modifier = Modifier.fillMaxSize(),
					verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
				) {
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f),
						colors = CardDefaults.cardColors(
							containerColor = PurecipesTheme.colorScheme.surfaceContainerLow,
						),
					) {
						Text(
							text = recipe.steps[page],
							modifier = Modifier
								.padding(PurecipesTheme.space.l)
								.testTag(STEP_BY_STEP_CURRENT_STEP_TEXT_TAG),
							style = PurecipesTheme.typography.bodyLarge,
						)
					}
					if (page == recipe.steps.lastIndex) {
						Button(
							onClick = {
								pagerScope.launch {
									pagerState.animateScrollToPage(finishPageIndex)
								}
							},
							modifier = Modifier
								.fillMaxWidth()
								.testTag(FINISH_COOKING_BUTTON_TAG),
						) {
							Text(text = "Finish cooking")
						}
					}
				}
			}
		}
	}
}

@Composable
private fun PagerIndicator(
	currentPageIndex: Int,
	stepCount: Int,
	modifier: Modifier = Modifier,
) {
	val progress = (currentPageIndex + 1) / stepCount.toFloat()

	Box(
		modifier = modifier
			.clip(PurecipesTheme.shapes.extraLarge)
			.background(PurecipesTheme.colorScheme.surfaceContainerHighest)
			.height(PurecipesTheme.space.s),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth(progress)
				.fillMaxHeight()
				.background(PurecipesTheme.colorScheme.primary),
		)
	}
}

private val previewCookingRecipe = RecipeDetails(
	id = 42,
	title = "Tomato Pasta",
	description = "A quick weeknight dinner.",
	imageUrl = "https://example.com/pasta.jpg",
	ingredientGroups = listOf(
		IngredientGroup(
			name = "Sauce",
			ingredients = listOf(
				RecipeIngredient(text = "2 tomatoes"),
				RecipeIngredient(text = "1 garlic clove"),
			),
		),
	),
	steps = listOf(
		"Bring a large pot of salted water to a boil.",
		"Cook the pasta until al dente, then reserve some pasta water.",
		"Sauté garlic in olive oil, add tomatoes, and simmer until saucy.",
		"Toss pasta with sauce, adding pasta water to loosen if needed.",
	),
	totalTime = 25,
	yields = "2 servings",
	cuisine = Cuisine.ITALIAN,
)

@Preview(
	name = "Step by step cooking light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun StepByStepCookingScreenLightPreview() {
	StepByStepCookingScreenContent(
		darkTheme = false,
		recipe = previewCookingRecipe,
		currentPageIndex = 1,
	)
}

@Preview(
	name = "Step by step cooking dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun StepByStepCookingScreenDarkPreview() {
	StepByStepCookingScreenContent(
		darkTheme = true,
		recipe = previewCookingRecipe,
		currentPageIndex = 0,
	)
}

@Preview(
	name = "Step by step cooking last step",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun StepByStepCookingScreenLastStepPreview() {
	StepByStepCookingScreenContent(
		darkTheme = false,
		recipe = previewCookingRecipe,
		currentPageIndex = previewCookingRecipe.steps.lastIndex,
	)
}

@Preview(
	name = "Step by step cooking finished",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun StepByStepCookingScreenFinishedPreview() {
	StepByStepCookingScreenContent(
		darkTheme = false,
		recipe = previewCookingRecipe,
		currentPageIndex = previewCookingRecipe.steps.size,
	)
}
