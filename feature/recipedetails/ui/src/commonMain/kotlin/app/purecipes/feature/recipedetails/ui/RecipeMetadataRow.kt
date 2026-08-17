package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun RecipeMetadataRow(recipe: RecipeDetails, isRecipeConverted: Boolean) {
	val items = listOfNotNull(
		recipe.cuisine?.displayName,
		recipe.totalTime?.let { "$it min" },
		recipe.yields?.takeIf { it.isNotBlank() },
		"Private".takeIf { recipe.isPrivate },
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

private fun MeasurementSystem.displayName(isRecipeConverted: Boolean): String =
	when (this) {
		MeasurementSystem.IMPERIAL -> if (isRecipeConverted) "Converted to imperial" else "Imperial"
		MeasurementSystem.METRIC -> if (isRecipeConverted) "Converted to metric" else "Metric"
		MeasurementSystem.MIXED -> "Mixed"
	}
