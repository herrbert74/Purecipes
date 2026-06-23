package app.purecipes.feature.search.ui.filter

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun IngredientTriStateChip(
	item: String,
	state: IngredientChipState,
	onToggle: () -> Unit,
) {
	when (state) {
		IngredientChipState.NEUTRAL -> FilterChip(
			selected = false,
			onClick = onToggle,
			label = { Text(item) },
		)
		IngredientChipState.SELECTED -> FilterChip(
			selected = true,
			onClick = onToggle,
			label = { Text(item) },
		)
		IngredientChipState.EXCLUDED -> FilterChip(
			selected = true,
			onClick = onToggle,
			label = { Text(item) },
			colors = FilterChipDefaults.filterChipColors(
				selectedContainerColor = PurecipesTheme.colorScheme.errorContainer,
				selectedLabelColor = PurecipesTheme.colorScheme.onErrorContainer,
			),
		)
	}
}
