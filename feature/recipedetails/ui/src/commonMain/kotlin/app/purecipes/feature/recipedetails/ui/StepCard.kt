package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun StepCard(stepNumber: Int, step: String) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Row(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Surface(
				modifier = Modifier.size(PurecipesTheme.space.xl),
				shape = RoundedCornerShape(PurecipesTheme.space.m),
				color = PurecipesTheme.colorScheme.primaryContainer,
			) {
				Box(contentAlignment = Alignment.Center) {
					Text(
						text = stepNumber.toString(),
						style = PurecipesTheme.typography.titleMedium,
						color = PurecipesTheme.colorScheme.onPrimaryContainer,
					)
				}
			}

			Text(
				text = step,
				modifier = Modifier.weight(1f),
				style = PurecipesTheme.typography.bodyLarge,
			)
		}
	}
}
