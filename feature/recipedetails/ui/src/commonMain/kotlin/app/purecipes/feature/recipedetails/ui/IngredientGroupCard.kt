package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun IngredientGroupCard(group: IngredientGroup) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			group.name?.takeIf { it.isNotBlank() }?.let {
				Text(
					text = it,
					style = PurecipesTheme.typography.titleMedium,
				)
			}

			group.ingredients.forEach { ingredient ->
				val isOptional = ingredient.requirement == IngredientRequirement.OPTIONAL
				Text(
					text = buildString {
						append("- ")
						append(ingredient.text)
						if (isOptional) {
							append(" (optional)")
						}
					},
					style = if (isOptional) {
						PurecipesTheme.typography.bodyMedium
					} else {
						PurecipesTheme.typography.bodyLarge
					},
					color = if (isOptional) {
						PurecipesTheme.colorScheme.onSurfaceVariant
					} else {
						PurecipesTheme.colorScheme.onSurface
					},
				)
			}
		}
	}
}
