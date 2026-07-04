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
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val KEY_INGREDIENT_REMOVE_BUTTON_TAG_PREFIX = "keyIngredientRemove"

internal fun keyIngredientRemoveTag(item: String): String =
	"$KEY_INGREDIENT_REMOVE_BUTTON_TAG_PREFIX:$item"

@Composable
internal fun KeyIngredientChip(
	item: String,
	onRemove: () -> Unit,
	modifier: Modifier = Modifier,
) {
	InputChip(
		selected = true,
		onClick = onRemove,
		modifier = modifier,
		label = { Text(item) },
		trailingIcon = {
			IconButton(
				onClick = onRemove,
				modifier = Modifier
					.size(InputChipDefaults.IconSize)
					.testTag(keyIngredientRemoveTag(item)),
			) {
				Icon(
					imageVector = Icons.Filled.Close,
					contentDescription = "Remove $item",
					modifier = Modifier.size(InputChipDefaults.IconSize),
				)
			}
		},
	)
}

@Preview(showBackground = true)
@Composable
private fun KeyIngredientChipPreview() {
	PurecipesTheme {
		KeyIngredientChip(
			item = "Tomato",
			onRemove = {},
		)
	}
}
