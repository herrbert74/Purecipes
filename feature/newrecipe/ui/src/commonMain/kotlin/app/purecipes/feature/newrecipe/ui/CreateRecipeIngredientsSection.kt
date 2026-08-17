package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val INGREDIENTS_ADD_BUTTON_TAG = "createRecipeAddIngredientButton"
internal const val INGREDIENTS_PASTE_BUTTON_TAG = "createRecipePasteIngredientsButton"
internal const val INGREDIENTS_PASTE_FIELD_TAG = "createRecipePasteIngredientsField"
internal const val INGREDIENT_NAME_FIELD_TAG_PREFIX = "createRecipeIngredientNameField"

@Composable
internal fun CreateRecipeIngredientsSection(
	ingredientRows: IngredientRowsState,
	onRowChange: (Int, IngredientRowInput) -> Unit,
	onAddRowClick: () -> Unit,
	onRemoveRowClick: (Int) -> Unit,
	onAddAlternativeClick: (Int) -> Unit,
	onRemoveAlternativeClick: (Int, Int) -> Unit,
	onPasteLines: (String) -> Unit,
) {
	var showPasteDialog by remember { mutableStateOf(false) }
	val addButtonBringIntoViewRequester = remember { BringIntoViewRequester() }
	var previousRowCount by remember { mutableIntStateOf(ingredientRows.items.size) }

	LaunchedEffect(ingredientRows.items.size) {
		if (ingredientRows.items.size > previousRowCount) {
			addButtonBringIntoViewRequester.bringIntoView()
		}
		previousRowCount = ingredientRows.items.size
	}

	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = "Ingredients",
				style = PurecipesTheme.typography.titleMedium,
			)
			TextButton(
				onClick = { showPasteDialog = true },
				modifier = Modifier.testTag(INGREDIENTS_PASTE_BUTTON_TAG),
			) {
				Text(text = "Paste list")
			}
		}

		ingredientRows.items.forEachIndexed { index, row ->
			IngredientRowEditor(
				index = index,
				row = row,
				canRemove = ingredientRows.items.size > 1,
				onRowChange = { onRowChange(index, it) },
				onRemoveRowClick = { onRemoveRowClick(index) },
				onAddAlternativeClick = { onAddAlternativeClick(index) },
				onRemoveAlternativeClick = { altIndex ->
					onRemoveAlternativeClick(index, altIndex)
				},
			)
		}

		FilledTonalButton(
			onClick = onAddRowClick,
			modifier = Modifier
				.fillMaxWidth()
				.bringIntoViewRequester(addButtonBringIntoViewRequester)
				.testTag(INGREDIENTS_ADD_BUTTON_TAG),
		) {
			Text(text = "Add ingredient")
		}
	}

	if (showPasteDialog) {
		PasteIngredientsDialog(
			onDismiss = { showPasteDialog = false },
			onConfirm = { pasted ->
				onPasteLines(pasted)
				showPasteDialog = false
			},
		)
	}
}

@Composable
private fun IngredientRowEditor(
	index: Int,
	row: IngredientRowInput,
	canRemove: Boolean,
	onRowChange: (IngredientRowInput) -> Unit,
	onRemoveRowClick: () -> Unit,
	onAddAlternativeClick: () -> Unit,
	onRemoveAlternativeClick: (Int) -> Unit,
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		Text(
			text = "Ingredient ${index + 1}",
			style = PurecipesTheme.typography.labelLarge,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
		IngredientPartFields(
			part = row.primary,
			nameTestTag = "$INGREDIENT_NAME_FIELD_TAG_PREFIX$index",
			onPartChange = { primary -> onRowChange(row.copy(primary = primary)) },
			trailing = {
				if (canRemove) {
					IconButton(onClick = onRemoveRowClick) {
						Icon(
							imageVector = Icons.Filled.Delete,
							contentDescription = "Remove ingredient ${index + 1}",
						)
					}
				}
			},
		)
		Row(verticalAlignment = Alignment.CenterVertically) {
			Checkbox(
				checked = row.isOptional,
				onCheckedChange = { checked -> onRowChange(row.copy(isOptional = checked)) },
			)
			Text(
				text = "Optional",
				style = PurecipesTheme.typography.bodyMedium,
			)
		}
		row.alternatives.forEachIndexed { altIndex, alternative ->
			Text(
				text = "or",
				style = PurecipesTheme.typography.labelMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(start = PurecipesTheme.space.xs),
			)
			IngredientPartFields(
				part = alternative,
				nameTestTag = "${INGREDIENT_NAME_FIELD_TAG_PREFIX}${index}Alt$altIndex",
				onPartChange = { updated ->
					val alternatives = row.alternatives.toMutableList()
					alternatives[altIndex] = updated
					onRowChange(row.copy(alternatives = alternatives))
				},
				trailing = {
					IconButton(onClick = { onRemoveAlternativeClick(altIndex) }) {
						Icon(
							imageVector = Icons.Filled.Delete,
							contentDescription = "Remove alternative",
						)
					}
				},
			)
		}
		TextButton(onClick = onAddAlternativeClick) {
			Text(text = "Add alternative")
		}
	}
}

@Composable
private fun IngredientPartFields(
	part: IngredientPartInput,
	nameTestTag: String,
	onPartChange: (IngredientPartInput) -> Unit,
	trailing: @Composable (() -> Unit)? = null,
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			verticalAlignment = Alignment.CenterVertically,
		) {
			OutlinedTextField(
				value = part.amount,
				onValueChange = { nextAmount ->
					if (IngredientRowComposer.isAllowedAmountInput(nextAmount)) {
						onPartChange(part.copy(amount = nextAmount))
					}
				},
				modifier = Modifier.weight(1f),
				label = { IngredientFieldLabel(text = "Amount") },
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
				singleLine = true,
			)
			OutlinedTextField(
				value = part.unit,
				onValueChange = { onPartChange(part.copy(unit = it)) },
				modifier = Modifier.weight(1f),
				label = { IngredientFieldLabel(text = "Unit") },
				singleLine = true,
			)
			trailing?.invoke()
		}
		OutlinedTextField(
			value = part.name,
			onValueChange = { onPartChange(part.copy(name = it)) },
			modifier = Modifier
				.fillMaxWidth()
				.testTag(nameTestTag),
			label = { IngredientFieldLabel(text = "Ingredient") },
			singleLine = true,
		)
	}
}

@Composable
private fun IngredientFieldLabel(text: String) {
	Text(
		text = text,
		maxLines = 1,
		softWrap = false,
		overflow = TextOverflow.Ellipsis,
	)
}

@Composable
private fun PasteIngredientsDialog(
	onDismiss: () -> Unit,
	onConfirm: (String) -> Unit,
) {
	var pasteText by remember { mutableStateOf("") }
	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			Button(
				onClick = { onConfirm(pasteText) },
				enabled = pasteText.isNotBlank(),
			) {
				Text(text = "Paste")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = "Cancel")
			}
		},
		title = { Text(text = "Paste ingredient list") },
		text = {
			OutlinedTextField(
				value = pasteText,
				onValueChange = { pasteText = it },
				modifier = Modifier
					.fillMaxWidth()
					.testTag(INGREDIENTS_PASTE_FIELD_TAG),
				label = { Text(text = "One ingredient per line") },
				supportingText = {
					Text(text = "Use optional: and or the same way as before.")
				},
				minLines = 5,
			)
		},
	)
}
