package app.purecipes.feature.search.ui.filter

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.key_ingredient_state_selected
import purecipes.shared.ui.generated.resources.remove_ingredient

internal const val KEY_INGREDIENT_REMOVE_BUTTON_TAG_PREFIX = "keyIngredientRemove"

internal fun keyIngredientRemoveTag(item: String): String =
	"$KEY_INGREDIENT_REMOVE_BUTTON_TAG_PREFIX:$item"

@Composable
internal fun KeyIngredientChip(
	item: String,
	onRemove: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val removeDescription = stringResource(Res.string.remove_ingredient, item)
	val selectedState = stringResource(Res.string.key_ingredient_state_selected)
	InputChip(
		selected = true,
		onClick = onRemove,
		modifier = modifier.semantics {
			contentDescription = removeDescription
			stateDescription = selectedState
		},
		label = { Text(item) },
		trailingIcon = {
			Icon(
				imageVector = AppIcons.Close,
				contentDescription = null,
				modifier = Modifier
					.size(InputChipDefaults.IconSize)
					.testTag(keyIngredientRemoveTag(item)),
			)
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
