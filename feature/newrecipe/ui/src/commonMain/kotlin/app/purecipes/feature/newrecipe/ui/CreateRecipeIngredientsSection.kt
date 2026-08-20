package app.purecipes.feature.newrecipe.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val INGREDIENTS_ADD_BUTTON_TAG = "createRecipeAddIngredientButton"
internal const val INGREDIENTS_PASTE_BUTTON_TAG = "createRecipePasteIngredientsButton"
internal const val INGREDIENTS_PASTE_FIELD_TAG = "createRecipePasteIngredientsField"
internal const val INGREDIENT_AMOUNT_FIELD_TAG_PREFIX = "createRecipeIngredientAmountField"
internal const val INGREDIENT_NAME_FIELD_TAG_PREFIX = "createRecipeIngredientNameField"
internal const val INGREDIENT_ROW_TAG_PREFIX = "createRecipeIngredientRow"
internal const val INGREDIENT_UNIT_FIELD_TAG_PREFIX = "createRecipeIngredientUnitField"

@Composable
internal fun CreateRecipeIngredientsSection(
	ingredientRows: IngredientRowsState,
	suggestedUnits: SuggestedIngredientUnits,
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
	val expandedRows = remember { mutableStateMapOf(0 to true) }
	val colors = createRecipeSegmentedListColors()

	LaunchedEffect(ingredientRows.items.size) {
		if (ingredientRows.items.size > previousRowCount) {
			expandedRows[ingredientRows.items.lastIndex] = true
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

		Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
			ingredientRows.items.forEachIndexed { index, row ->
				val expanded = expandedRows[index] == true
				IngredientRowEditor(
					index = index,
					row = row,
					suggestedUnits = suggestedUnits,
					expanded = expanded,
					canRemove = ingredientRows.items.size > 1,
					colors = colors,
					onExpandToggle = {
						expandedRows[index] = !expanded
					},
					onRowChange = { onRowChange(index, it) },
					onRemoveRowClick = { onRemoveRowClick(index) },
					onAddAlternativeClick = { onAddAlternativeClick(index) },
					onRemoveAlternativeClick = { altIndex ->
						onRemoveAlternativeClick(index, altIndex)
					},
				)
			}
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
	suggestedUnits: SuggestedIngredientUnits,
	expanded: Boolean,
	canRemove: Boolean,
	colors: ListItemColors,
	onExpandToggle: () -> Unit,
	onRowChange: (IngredientRowInput) -> Unit,
	onRemoveRowClick: () -> Unit,
	onAddAlternativeClick: () -> Unit,
	onRemoveAlternativeClick: (Int) -> Unit,
) {
	val headline = IngredientRowComposer.collapsedHeadline(index = index, row = row)
	val groupItemCount = if (expanded) 2 else 1
	val editorVisibility = remember { MutableTransitionState(expanded) }
	if (editorVisibility.targetState != expanded) {
		editorVisibility.targetState = expanded
	}
	val chevronRotation by animateFloatAsState(
		targetValue = if (expanded) 180f else 0f,
		label = "ingredientChevron",
	)

	Column(
		modifier = Modifier.animateContentSize(),
		verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
	) {
		SegmentedListItem(
			onClick = onExpandToggle,
			shapes = ListItemDefaults.segmentedShapes(index = 0, count = groupItemCount),
			modifier = Modifier.testTag("$INGREDIENT_ROW_TAG_PREFIX$index"),
			colors = colors,
			verticalAlignment = Alignment.CenterVertically,
			trailingContent = {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Box(
						modifier = Modifier.size(PurecipesTheme.space.xxl),
						contentAlignment = Alignment.Center,
					) {
						Icon(
							imageVector = Icons.Filled.ExpandMore,
							contentDescription = if (expanded) {
								"Collapse ingredient ${index + 1}"
							} else {
								"Expand ingredient ${index + 1}"
							},
							modifier = Modifier.rotate(chevronRotation),
						)
					}
					if (canRemove) {
						IconButton(onClick = onRemoveRowClick) {
							Icon(
								imageVector = Icons.Filled.Delete,
								contentDescription = "Remove ingredient ${index + 1}",
							)
						}
					}
				}
			},
			content = {
				Text(
					text = headline,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			},
		)
		AnimatedVisibility(
			visibleState = editorVisibility,
			enter = fadeIn() + expandVertically(),
			exit = fadeOut() + shrinkVertically(),
		) {
			Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
				IngredientExpandedEditor(
					index = index,
					row = row,
					suggestedUnits = suggestedUnits,
					onRowChange = onRowChange,
					onAddAlternativeClick = onAddAlternativeClick,
					onRemoveAlternativeClick = onRemoveAlternativeClick,
				)
				SegmentedListItem(
					checked = row.isOptional,
					onCheckedChange = { checked -> onRowChange(row.copy(isOptional = checked)) },
					shapes = ListItemDefaults.segmentedShapes(index = 1, count = groupItemCount),
					colors = colors,
					leadingContent = {
						Checkbox(
							checked = row.isOptional,
							onCheckedChange = null,
						)
					},
					content = { Text(text = "Optional") },
				)
			}
		}
	}
}

@Composable
private fun IngredientExpandedEditor(
	index: Int,
	row: IngredientRowInput,
	suggestedUnits: SuggestedIngredientUnits,
	onRowChange: (IngredientRowInput) -> Unit,
	onAddAlternativeClick: () -> Unit,
	onRemoveAlternativeClick: (Int) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(
				start = PurecipesTheme.space.m,
				end = PurecipesTheme.space.m,
				bottom = PurecipesTheme.space.s,
			),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		IngredientPartFields(
			part = row.primary,
			suggestedUnits = suggestedUnits,
			amountTestTag = "$INGREDIENT_AMOUNT_FIELD_TAG_PREFIX$index",
			nameTestTag = "$INGREDIENT_NAME_FIELD_TAG_PREFIX$index",
			unitTestTag = "$INGREDIENT_UNIT_FIELD_TAG_PREFIX$index",
			onPartChange = { primary -> onRowChange(row.copy(primary = primary)) },
		)
		row.alternatives.forEachIndexed { altIndex, alternative ->
			Text(
				text = "or",
				style = PurecipesTheme.typography.labelMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(start = PurecipesTheme.space.xs),
			)
			IngredientPartFields(
				part = alternative,
				suggestedUnits = suggestedUnits,
				amountTestTag = "${INGREDIENT_AMOUNT_FIELD_TAG_PREFIX}${index}Alt$altIndex",
				nameTestTag = "${INGREDIENT_NAME_FIELD_TAG_PREFIX}${index}Alt$altIndex",
				unitTestTag = "${INGREDIENT_UNIT_FIELD_TAG_PREFIX}${index}Alt$altIndex",
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
	suggestedUnits: SuggestedIngredientUnits,
	amountTestTag: String,
	nameTestTag: String,
	unitTestTag: String,
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
			DenseOutlinedTextField(
				value = part.amount,
				onValueChange = { nextAmount ->
					if (IngredientRowComposer.isAllowedAmountInput(nextAmount)) {
						onPartChange(part.copy(amount = nextAmount))
					}
				},
				modifier = Modifier
					.weight(1f)
					.testTag(amountTestTag),
				label = { Text(text = "Amount") },
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
			)
			IngredientUnitField(
				value = part.unit,
				suggestedUnits = suggestedUnits,
				onValueChange = { onPartChange(part.copy(unit = it)) },
				modifier = Modifier.weight(1f),
				testTag = unitTestTag,
			)
		}
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			verticalAlignment = Alignment.CenterVertically,
		) {
			DenseOutlinedTextField(
				value = part.name,
				onValueChange = { onPartChange(part.copy(name = it)) },
				modifier = Modifier
					.weight(1f)
					.testTag(nameTestTag),
				label = { Text(text = "Ingredient") },
			)
			trailing?.invoke()
		}
	}
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

@Preview(showBackground = true)
@Composable
private fun CreateRecipeIngredientsSectionPreview() {
	PurecipesTheme {
		CreateRecipeIngredientsSection(
			ingredientRows = IngredientRowsState(
				items = listOf(
					IngredientRowInput(
						primary = IngredientPartInput(
							amount = "400",
							unit = "g",
							name = "spaghetti",
						),
					),
					IngredientRowInput(
						primary = IngredientPartInput(
							amount = "2",
							name = "tomatoes",
						),
					),
				),
			),
			suggestedUnits = SuggestedIngredientUnits(
				items = listOf("g", "kg", "ml", "l"),
			),
			onRowChange = { _, _ -> },
			onAddRowClick = {},
			onRemoveRowClick = {},
			onAddAlternativeClick = {},
			onRemoveAlternativeClick = { _, _ -> },
			onPasteLines = {},
		)
	}
}
