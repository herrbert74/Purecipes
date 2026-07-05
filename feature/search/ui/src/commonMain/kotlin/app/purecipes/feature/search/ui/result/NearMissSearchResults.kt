package app.purecipes.feature.search.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.domain.model.NearMissRecipe
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun NearMissSearchResults(
	nearMissRecipes: ImmutableList<NearMissRecipe>,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (nearMissRecipes.isEmpty()) {
		return
	}

	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		Text(
			text = "These are almost a match — you're only missing one ingredient.",
			style = PurecipesTheme.typography.bodyMedium,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.padding(bottom = PurecipesTheme.space.xs),
		)
		nearMissRecipes
			.groupBy { nearMiss -> nearMiss.missingIngredient }
			.forEach { (missingIngredient, recipes) ->
				NearMissIngredientSection(
					missingIngredient = missingIngredient,
					recipes = recipes.toImmutableList(),
					onRecipeSelect = onRecipeSelect,
				)
			}
	}
}

@Composable
private fun NearMissIngredientSection(
	missingIngredient: String,
	recipes: ImmutableList<NearMissRecipe>,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		Text(
			text = "Do you have $missingIngredient?",
			style = PurecipesTheme.typography.titleMedium,
			color = PurecipesTheme.colorScheme.onSurface,
		)
		recipes.forEach { nearMiss ->
			RecipeRow(
				recipe = nearMiss.recipe,
				onClick = { onRecipeSelect(nearMiss.recipe.id) },
			)
		}
	}
}
