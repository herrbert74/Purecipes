package app.purecipes.feature.search.ui.filter

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import app.purecipes.shared.ui.theme.PurecipesTheme
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.ingredient_state_excluded
import purecipes.shared.ui.generated.resources.ingredient_state_included
import purecipes.shared.ui.generated.resources.ingredient_state_neutral

internal fun ingredientTriStateChipTag(item: String): String =
	"ingredientTriStateChip${item.filter(Char::isLetterOrDigit)}"

@Composable
internal fun IngredientTriStateChip(
	item: String,
	state: IngredientChipState,
	onToggle: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val chipStateDescription = when (state) {
		IngredientChipState.NEUTRAL -> stringResource(Res.string.ingredient_state_neutral)
		IngredientChipState.SELECTED -> stringResource(Res.string.ingredient_state_included)
		IngredientChipState.EXCLUDED -> stringResource(Res.string.ingredient_state_excluded)
	}
	val chipModifier = modifier
		.testTag(ingredientTriStateChipTag(item))
		.semantics { stateDescription = chipStateDescription }
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
