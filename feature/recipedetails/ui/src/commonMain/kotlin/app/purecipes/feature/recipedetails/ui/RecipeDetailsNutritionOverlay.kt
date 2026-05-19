package app.purecipes.feature.recipedetails.ui

import androidx.compose.runtime.Composable
import app.purecipes.shared.domain.model.RecipeNutrition
import app.purecipes.shared.ui.component.NutritionFactsDialog

@Composable
internal fun RecipeDetailsNutritionOverlay(
	nutrition: RecipeNutrition?,
	showDialog: Boolean,
	onDismiss: () -> Unit,
) {
	if (showDialog) {
		nutrition?.let { data ->
			NutritionFactsDialog(
				nutrition = data,
				onDismiss = onDismiss,
			)
		}
	}
}
