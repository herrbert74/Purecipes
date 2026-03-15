package com.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.ui.component.BackNavigationButton

@Composable
fun StepByStepCookingRoute(
	recipeId: Int,
	repository: RecipeDetailsRepository,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var isLoading by remember(recipeId) { mutableStateOf(true) }
	var recipeDetails by remember(recipeId) { mutableStateOf<RecipeDetails?>(null) }
	var errorMessage by remember(recipeId) { mutableStateOf<String?>(null) }
	var currentStepIndex by rememberSaveable(recipeId) { mutableIntStateOf(0) }

	LaunchedEffect(recipeId, repository) {
		isLoading = true
		errorMessage = null
		recipeDetails = null

		val outcome = repository.getRecipeDetails(recipeId)
		recipeDetails = outcome.get()
		errorMessage = outcome.getError()?.message
		currentStepIndex = 0
		isLoading = false
	}

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Step-by-step cooking") },
				navigationIcon = {
					BackNavigationButton(onBack = onBack)
				},
			)
		},
	) { innerPadding ->
		when {
			isLoading -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				contentAlignment = Alignment.Center,
			) {
				CircularProgressIndicator()
			}

			errorMessage != null -> RecipeCookingMessage(
				message = errorMessage ?: "Unknown error",
				modifier = Modifier.padding(innerPadding),
			)

			recipeDetails == null || recipeDetails?.steps.isNullOrEmpty() -> RecipeCookingMessage(
				message = "No cooking steps available yet.",
				modifier = Modifier.padding(innerPadding),
			)

			else -> StepByStepCookingScreen(
				recipe = recipeDetails ?: return@Scaffold,
				currentStepIndex = currentStepIndex,
				onPrevious = {
					if (currentStepIndex > 0) {
						currentStepIndex -= 1
					}
				},
				onNext = {
					if (recipeDetails != null && currentStepIndex < recipeDetails!!.steps.lastIndex) {
						currentStepIndex += 1
					}
				},
				onFinish = onBack,
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}

@Composable
private fun StepByStepCookingScreen(
	recipe: RecipeDetails,
	currentStepIndex: Int,
	onPrevious: () -> Unit,
	onNext: () -> Unit,
	onFinish: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val isLastStep = currentStepIndex == recipe.steps.lastIndex

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = 16.dp, vertical = 20.dp),
		verticalArrangement = Arrangement.spacedBy(20.dp),
	) {
		Text(
			text = recipe.title,
			style = MaterialTheme.typography.headlineSmall,
		)
		Text(
			text = "Step ${currentStepIndex + 1} of ${recipe.steps.size}",
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.primary,
		)
		Card(
			modifier = Modifier.fillMaxWidth(),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
		) {
			Text(
				text = recipe.steps[currentStepIndex],
				modifier = Modifier.padding(24.dp),
				style = MaterialTheme.typography.bodyLarge,
			)
		}
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			OutlinedButton(
				onClick = onPrevious,
				enabled = currentStepIndex > 0,
				modifier = Modifier.weight(1f),
			) {
				Text(text = "Previous")
			}

			if (isLastStep) {
				Button(
					onClick = onFinish,
					modifier = Modifier.weight(1f),
				) {
					Text(text = "Finish")
				}
			} else {
				Button(
					onClick = onNext,
					modifier = Modifier.weight(1f),
				) {
					Text(text = "Next")
				}
			}
		}
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
