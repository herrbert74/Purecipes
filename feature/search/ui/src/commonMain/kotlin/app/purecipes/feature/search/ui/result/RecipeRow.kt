package app.purecipes.feature.search.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.BodyText
import app.purecipes.shared.ui.component.TitleText
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

@Composable
internal fun RecipeRow(recipe: RecipeSummary, onClick: () -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.s),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			verticalAlignment = Alignment.CenterVertically,
		) {
			AsyncImage(
				model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = recipe.title,
				modifier = Modifier
					.size(56.dp)
					.clip(RoundedCornerShape(PurecipesTheme.space.s))
					.background(PurecipesTheme.colorScheme.secondaryContainer),
				contentScale = ContentScale.Crop,
			)

			Column(modifier = Modifier.weight(1f)) {
				TitleText(
					text = recipe.title,
				)
				BodyText(
					text = listOfNotNull(
						recipe.cuisine?.displayName ?: "Unknown cuisine",
						recipe.totalTime?.let { "$it min" },
					).joinToString(separator = " • "),
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
				recipe.measurementSystem?.let { measurementSystem ->
					Text(
						text = measurementSystem.displayName(),
						style = PurecipesTheme.typography.labelMedium,
						color = PurecipesTheme.colorScheme.primary,
					)
				}
			}
		}
	}
}

internal fun MeasurementSystem.displayName(): String {
	return when (this) {
		MeasurementSystem.IMPERIAL -> "Imperial"
		MeasurementSystem.METRIC -> "Metric"
		MeasurementSystem.MIXED -> "Mixed"
	}
}

private val previewRecipeRowSample = RecipeSummary(
	id = 1,
	title = "Tomato Pasta",
	cuisine = Cuisine.ITALIAN,
	imageUrl = null,
	totalTime = 20,
	measurementSystem = MeasurementSystem.METRIC,
)

private val previewRecipeRowSampleB = RecipeSummary(
	id = 2,
	title = "Smoked brisket",
	cuisine = Cuisine.AMERICAN,
	imageUrl = null,
	totalTime = 240,
	measurementSystem = MeasurementSystem.IMPERIAL,
)

@Preview(
	name = "Recipe row light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeRowLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(modifier = Modifier.padding(PurecipesTheme.space.m)) {
			RecipeRow(recipe = previewRecipeRowSample, onClick = {})
		}
	}
}

@Preview(
	name = "Recipe row dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun RecipeRowDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface(modifier = Modifier.padding(PurecipesTheme.space.m)) {
			RecipeRow(recipe = previewRecipeRowSampleB, onClick = {})
		}
	}
}
