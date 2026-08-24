package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.domain.model.RecipeNutrition
import app.purecipes.shared.ui.component.NUTRITION_FACTS_BUTTON_TAG
import app.purecipes.shared.ui.component.NutritionSummaryCard
import app.purecipes.shared.ui.component.PurecipesTextButton
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun RecipeDetailsNutritionSection(
	nutrition: RecipeNutrition,
	onShowFullNutrition: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		NutritionSummaryCard(
			nutrition = nutrition.perServing ?: nutrition.recipeTotals,
			isLoading = false,
		)
		PurecipesTextButton(
			text = "View full nutrition facts",
			onClick = onShowFullNutrition,
			modifier = Modifier.testTag(NUTRITION_FACTS_BUTTON_TAG),
		)
	}
}
