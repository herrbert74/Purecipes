package app.purecipes.feature.search.ui.filter

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.ui.theme.PurecipesTheme

internal fun ingredientTriStateChipTag(item: String): String =
	"ingredientTriStateChip${item.filter(Char::isLetterOrDigit)}"

@Composable
internal fun IngredientTriStateChip(
	item: String,
	state: IngredientChipState,
	onToggle: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val chipModifier = modifier.testTag(ingredientTriStateChipTag(item))
	when (state) {
		IngredientChipState.NEUTRAL -> FilterChip(
			selected = false,
			onClick = onToggle,
			label = { Text(item) },
			modifier = chipModifier,
		)

		IngredientChipState.SELECTED -> FilterChip(
			selected = true,
			onClick = onToggle,
			label = { Text(item) },
			modifier = chipModifier,
		)

		IngredientChipState.EXCLUDED -> FilterChip(
			selected = true,
			onClick = onToggle,
			label = { Text(item) },
			modifier = chipModifier,
			colors = FilterChipDefaults.filterChipColors(
				selectedContainerColor = PurecipesTheme.colorScheme.errorContainer,
				selectedLabelColor = PurecipesTheme.colorScheme.onErrorContainer,
			),
		)
	}
}
