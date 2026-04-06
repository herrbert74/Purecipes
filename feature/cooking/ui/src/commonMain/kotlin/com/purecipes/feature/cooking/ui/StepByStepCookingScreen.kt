package com.purecipes.feature.cooking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.ui.component.BackNavigationButton

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

			viewModel.errorMessage != null -> RecipeCookingMessage(
				message = viewModel.errorMessage ?: "Unknown error",
				modifier = Modifier.padding(innerPadding),
			)

			viewModel.recipeDetails == null || viewModel.recipeDetails?.steps.isNullOrEmpty() -> RecipeCookingMessage(
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
			.padding(horizontal = 16.dp, vertical = 20.dp),
		verticalArrangement = Arrangement.spacedBy(20.dp),
	) {
		Text(
			text = "${currentStepIndex + 1} of ${recipe.steps.size}",
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.primary,
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
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
			) {
				Text(
					text = recipe.steps[page],
					modifier = Modifier.padding(24.dp),
					style = MaterialTheme.typography.bodyLarge,
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
			.clip(MaterialTheme.shapes.extraLarge)
			.background(MaterialTheme.colorScheme.surfaceContainerHighest)
			.height(8.dp),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth(progress)
				.fillMaxHeight()
				.background(MaterialTheme.colorScheme.primary),
		)
	}
}

@Composable
private fun RecipeCookingMessage(
	message: String,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.padding(PaddingValues(horizontal = 24.dp, vertical = 16.dp)),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = message,
			style = MaterialTheme.typography.bodyLarge,
			textAlign = TextAlign.Center,
		)
	}
}
