package app.purecipes.feature.search.ui.filter

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val CUSTOM_INGREDIENT_REMOVE_BUTTON_TAG_PREFIX = "customIngredientRemove"
internal const val CUSTOM_INGREDIENT_CHIP_TAG_PREFIX = "customIngredientChip"

internal fun customIngredientRemoveTag(item: String): String =
	"$CUSTOM_INGREDIENT_REMOVE_BUTTON_TAG_PREFIX:$item"

internal fun customIngredientChipTag(item: String): String =
	"$CUSTOM_INGREDIENT_CHIP_TAG_PREFIX:${item.filter(Char::isLetterOrDigit)}"

@Composable
internal fun CustomIngredientChip(
	item: String,
	state: IngredientChipState,
	onToggle: () -> Unit,
	onRemove: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val isExcluded = state == IngredientChipState.EXCLUDED
	val chipColors = if (isExcluded) {
		InputChipDefaults.inputChipColors(
			selectedContainerColor = PurecipesTheme.colorScheme.errorContainer,
			selectedLabelColor = PurecipesTheme.colorScheme.onErrorContainer,
			selectedTrailingIconColor = PurecipesTheme.colorScheme.onErrorContainer,
		)
	} else {
		InputChipDefaults.inputChipColors()
	}

	InputChip(
		selected = state != IngredientChipState.NEUTRAL,
		onClick = onToggle,
		modifier = modifier,
		label = { Text(item) },
		trailingIcon = {
			IconButton(
				onClick = onRemove,
				modifier = Modifier
					.size(InputChipDefaults.IconSize)
					.testTag(customIngredientRemoveTag(item)),
			) {
				Icon(
					imageVector = Icons.Filled.Close,
					contentDescription = "Remove $item",
					modifier = Modifier.size(InputChipDefaults.IconSize),
				)
			}
		},
		colors = chipColors,
	)
}
