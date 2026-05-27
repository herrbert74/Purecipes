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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.ui.component.BackNavigationButton
import app.purecipes.shared.ui.component.CenteredMessageContent
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

internal const val STEP_BY_STEP_CURRENT_STEP_TEXT_TAG = "stepByStepCurrentStepText"

@Composable
fun StepByStepCookingRoute(
	recipeId: Int,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StepByStepCookingViewModel =
		assistedMetroViewModel<StepByStepCookingViewModel, StepByStepCookingViewModel.Factory> {
			create(recipeId = recipeId)
		},
) {
	Scaffold(
		modifier = modifier.fillMaxSize(),
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

			viewModel.recipeDetails == null || viewModel.recipeDetails?.steps.isNullOrEmpty() -> CenteredMessageContent(
				message = "No cooking steps available yet.",
				modifier = Modifier.padding(innerPadding),
			)

			else -> StepByStepCookingScreen(
				recipe = viewModel.recipeDetails ?: return@Scaffold,
				currentStepIndex = viewModel.currentStepIndex,
				onStepChange = viewModel::setCurrentStep,
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}

@Composable
internal fun StepByStepCookingScreen(
	recipe: RecipeDetails,
	currentStepIndex: Int,
	onStepChange: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	val pagerState = rememberPagerState(
		initialPage = currentStepIndex,
		pageCount = { recipe.steps.size },
	)
	val currentOnStepChange by rememberUpdatedState(onStepChange)

	LaunchedEffect(pagerState.currentPage) {
		if (pagerState.currentPage != currentStepIndex) {
			currentOnStepChange(pagerState.currentPage)
		}
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.m),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		Text(
			text = "${currentStepIndex + 1} of ${recipe.steps.size}",
			style = PurecipesTheme.typography.titleMedium,
			color = PurecipesTheme.colorScheme.primary,
		)
		PagerIndicator(
			currentStepIndex = currentStepIndex,
			stepCount = recipe.steps.size,
			modifier = Modifier.fillMaxWidth(),
		)
		HorizontalPager(
			state = pagerState,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
		) { page ->
			Card(
				modifier = Modifier.fillMaxSize(),
				colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
			) {
				Text(
					text = recipe.steps[page],
					modifier = Modifier
						.padding(PurecipesTheme.space.l)
						.testTag(STEP_BY_STEP_CURRENT_STEP_TEXT_TAG),
					style = PurecipesTheme.typography.bodyLarge,
				)
			}
		}
	}
}

@Composable
private fun PagerIndicator(
	currentStepIndex: Int,
	stepCount: Int,
	modifier: Modifier = Modifier,
) {
	val progress = (currentStepIndex + 1) / stepCount.toFloat()

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
			ingredients = listOf("2 tomatoes", "1 garlic clove"),
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
	StepByStepCookingScreenPreviewContent(darkTheme = false, currentStepIndex = 1)
}

@Preview(
	name = "Step by step cooking dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun StepByStepCookingScreenDarkPreview() {
	StepByStepCookingScreenPreviewContent(darkTheme = true, currentStepIndex = 0)
}

@Composable
private fun StepByStepCookingScreenPreviewContent(
	darkTheme: Boolean,
	currentStepIndex: Int,
) {
	PurecipesTheme(darkTheme = darkTheme) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = previewCookingRecipe.title) },
					navigationIcon = {
						BackNavigationButton(onBack = {})
					},
				)
			},
		) { innerPadding ->
			StepByStepCookingScreen(
				recipe = previewCookingRecipe,
				currentStepIndex = currentStepIndex,
				onStepChange = {},
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}
