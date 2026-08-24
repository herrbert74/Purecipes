package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.ui.component.ContainerTint
import app.purecipes.shared.ui.component.MetadataPillChip
import app.purecipes.shared.ui.theme.PurecipesTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RecipeMetadataRow(
	recipe: RecipeDetails,
	isRecipeConverted: Boolean,
	modifier: Modifier = Modifier,
) {
	val chips = buildList {
		recipe.cuisine?.displayName?.let { add(it to ContainerTint.Primary) }
		recipe.mealType?.displayName?.let { add(it to ContainerTint.Secondary) }
		recipe.difficultyLevel?.displayName?.let { add(it to ContainerTint.Tertiary) }
		recipe.dietaryPreferences.forEach { preference ->
			add(preference.displayName to ContainerTint.Primary)
		}
		"Private".takeIf { recipe.isPrivate }?.let { add(it to ContainerTint.Secondary) }
		recipe.measurementSystem?.let { system ->
			add(system.displayName(isRecipeConverted) to ContainerTint.Secondary)
		}
	}
	if (chips.isEmpty()) return

	FlowRow(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		chips.forEach { (text, tint) ->
			MetadataPillChip(
				text = text,
				tint = tint,
			)
		}
	}
}

private fun MeasurementSystem.displayName(isRecipeConverted: Boolean): String =
	when (this) {
		MeasurementSystem.IMPERIAL -> if (isRecipeConverted) "Converted to imperial" else "Imperial"
		MeasurementSystem.METRIC -> if (isRecipeConverted) "Converted to metric" else "Metric"
		MeasurementSystem.MIXED -> "Mixed"
	}
