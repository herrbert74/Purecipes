package com.purecipes.feature.cooking.ui

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
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.ui.component.BackNavigationButton
import com.purecipes.shared.ui.component.CenteredMessageContent
import com.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun StepByStepCookingRoute(
	recipeId: Int,
	getRecipeDetails: GetRecipeDetailsUseCase,
	getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	trackEvent: TrackEventUseCase,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val viewModel = stepByStepCookingViewModel(
		recipeId = recipeId,
		getRecipeDetails = getRecipeDetails,
		getMeasurementPreferences = getMeasurementPreferences,
		processRecipeDetailsForMeasurementPreferences = processRecipeDetailsForMeasurementPreferences,
		trackEvent = trackEvent,
	)

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
private fun StepByStepCookingScreen(
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
					modifier = Modifier.padding(PurecipesTheme.space.l),
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
