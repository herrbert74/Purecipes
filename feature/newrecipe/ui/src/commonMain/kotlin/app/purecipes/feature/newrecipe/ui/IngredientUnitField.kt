package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import app.purecipes.shared.domain.ingredient.IngredientUnitTokens
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun IngredientUnitField(
	value: String,
	suggestedUnits: SuggestedIngredientUnits,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
	testTag: String? = null,
) {
	var expanded by remember { mutableStateOf(false) }
	val selectedUnit = IngredientUnitTokens.canonicalUnit(value)
	val options = remember(selectedUnit, suggestedUnits) {
		buildList {
			add("")
			addAll(suggestedUnits.items)
			if (selectedUnit.isNotBlank() && selectedUnit !in this) {
				add(selectedUnit)
			}
		}
	}
	val colors = OutlinedTextFieldDefaults.colors(
		disabledTextColor = PurecipesTheme.colorScheme.onSurface,
		disabledBorderColor = PurecipesTheme.colorScheme.outline,
		disabledLabelColor = PurecipesTheme.colorScheme.onSurfaceVariant,
		disabledTrailingIconColor = PurecipesTheme.colorScheme.onSurfaceVariant,
		disabledSuffixColor = PurecipesTheme.colorScheme.onSurfaceVariant,
	)

	Box(modifier = modifier) {
		DenseOutlinedTextField(
			value = selectedUnit,
			onValueChange = {},
			modifier = Modifier.fillMaxWidth(),
			enabled = false,
			readOnly = true,
			label = { Text(text = "Unit") },
			suffix = {
				ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
			},
			colors = colors,
		)
		Box(
			modifier = Modifier
				.matchParentSize()
				.then(
					if (testTag != null) {
						Modifier.testTag(testTag)
					} else {
						Modifier
					},
				)
				.clickable { expanded = true },
		)
		DropdownMenu(
			expanded = expanded,
			onDismissRequest = { expanded = false },
		) {
			options.forEach { unit ->
				DropdownMenuItem(
					text = {
						Text(
							text = if (unit.isBlank()) {
								"None"
							} else {
								unit
							},
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
						)
					},
					onClick = {
						onValueChange(unit)
						expanded = false
					},
				)
			}
		}
	}
}
