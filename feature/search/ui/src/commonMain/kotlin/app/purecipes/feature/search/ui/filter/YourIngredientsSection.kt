package app.purecipes.feature.search.ui.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.ingredient.IngredientLookup
import app.purecipes.shared.domain.model.IngredientCatalogue
import app.purecipes.shared.domain.model.IngredientMatchResponse
import app.purecipes.shared.domain.model.LikelyIngredientMatch
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

internal const val FILTER_ADD_INGREDIENT_BUTTON_TAG = "filterAddIngredientButton"
internal const val ADD_INGREDIENT_DIALOG_TAG = "addIngredientDialog"
internal const val ADD_INGREDIENT_NAME_FIELD_TAG = "addIngredientNameField"
internal const val ADD_INGREDIENT_CONFIRM_BUTTON_TAG = "addIngredientConfirmButton"
internal const val ADD_INGREDIENT_USE_CATALOGUE_BUTTON_TAG = "addIngredientUseCatalogueButton"

@Composable
internal fun YourIngredientsSection(
	customPantryIngredients: ImmutableSet<String>,
	pantryIngredients: ImmutableSet<String>,
	excludedIngredients: ImmutableSet<String>,
	ingredientMatchPreview: IngredientMatchResponse?,
	isIngredientMatchLoading: Boolean,
	onCustomIngredientToggle: (String) -> Unit,
	onRemoveCustomIngredient: (String) -> Unit,
	onAddIngredientQueryChange: (String) -> Unit,
	onAddIngredient: (String) -> Unit,
	onClearIngredientMatchPreview: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var collapsed by rememberSaveable { mutableStateOf(false) }
	var showAddDialog by rememberSaveable { mutableStateOf(false) }

	Column(modifier = modifier) {
		FilterSectionHeader(
			title = "Your ingredients",
			modifier = Modifier.padding(start = PurecipesTheme.space.m),
			isCollapsed = collapsed,
			onToggleCollapse = { collapsed = !collapsed },
		)
		AnimatedVisibility(
			visible = !collapsed,
			enter = expandVertically(),
			exit = shrinkVertically(),
		) {
			Column(
				modifier = Modifier.padding(bottom = PurecipesTheme.space.s),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				if (customPantryIngredients.isNotEmpty()) {
					Text(
						text = "Tap to switch between in pantry and excluded. Tap × to remove.",
						style = PurecipesTheme.typography.bodySmall,
						color = PurecipesTheme.colorScheme.onSurfaceVariant,
						modifier = Modifier.padding(horizontal = PurecipesTheme.space.xl),
					)
					FlowRow(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = PurecipesTheme.space.xl),
						horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
						verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
					) {
						customPantryIngredients.sorted().forEach { item ->
							val state = when {
								item in excludedIngredients -> IngredientChipState.EXCLUDED
								item in pantryIngredients -> IngredientChipState.SELECTED
								else -> IngredientChipState.SELECTED
							}
							CustomIngredientChip(
								item = item,
								state = state,
								onToggle = { onCustomIngredientToggle(item) },
								onRemove = { onRemoveCustomIngredient(item) },
							)
						}
					}
				}
				Button(
					onClick = { showAddDialog = true },
					modifier = Modifier
						.padding(horizontal = PurecipesTheme.space.m)
						.testTag(FILTER_ADD_INGREDIENT_BUTTON_TAG),
				) {
					Text(text = "Add ingredient")
				}
			}
		}
	}

	if (showAddDialog) {
		AddIngredientDialog(
			pantryIngredients = pantryIngredients,
			excludedIngredients = excludedIngredients,
			preview = ingredientMatchPreview,
			isLoading = isIngredientMatchLoading,
			onQueryChange = onAddIngredientQueryChange,
			onDismiss = {
				showAddDialog = false
				onClearIngredientMatchPreview()
			},
			onAdd = { ingredientName ->
				onAddIngredient(ingredientName)
				showAddDialog = false
				onClearIngredientMatchPreview()
			},
		)
	}
}

@Composable
internal fun AddIngredientDialog(
	pantryIngredients: ImmutableSet<String>,
	excludedIngredients: ImmutableSet<String>,
	preview: IngredientMatchResponse?,
	isLoading: Boolean,
	onQueryChange: (String) -> Unit,
	onDismiss: () -> Unit,
	onAdd: (String) -> Unit,
) {
	var query by rememberSaveable { mutableStateOf("") }
	val trimmedQuery = query.trim()
	val catalogueMatch = remember(trimmedQuery) {
		if (trimmedQuery.isBlank()) {
			null
		} else {
			IngredientLookup.resolveCatalogueIngredient(trimmedQuery, IngredientCatalogue.allItems)
		}
	}

	AlertDialog(
		onDismissRequest = onDismiss,
		modifier = Modifier.testTag(ADD_INGREDIENT_DIALOG_TAG),
		title = { Text(text = "Add ingredient") },
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
				OutlinedTextField(
					value = query,
					onValueChange = {
						query = it
						onQueryChange(it)
					},
					modifier = Modifier
						.fillMaxWidth()
						.testTag(ADD_INGREDIENT_NAME_FIELD_TAG),
					label = { Text(text = "Ingredient name") },
					singleLine = true,
				)
				if (catalogueMatch != null) {
					Text(
						text = "Matches $catalogueMatch in our catalogue.",
						style = PurecipesTheme.typography.bodyMedium,
						color = PurecipesTheme.colorScheme.onSurfaceVariant,
					)
					if (catalogueMatch !in pantryIngredients && catalogueMatch !in excludedIngredients) {
						TextButton(
							onClick = { onAdd(catalogueMatch) },
							modifier = Modifier.testTag(ADD_INGREDIENT_USE_CATALOGUE_BUTTON_TAG),
						) {
							Text(text = "Use $catalogueMatch")
						}
					}
				}
				when {
					trimmedQuery in pantryIngredients -> Text(
						text = "Already in your pantry.",
						style = PurecipesTheme.typography.bodyMedium,
					)

					trimmedQuery in excludedIngredients -> Text(
						text = "Currently excluded from search results.",
						style = PurecipesTheme.typography.bodyMedium,
					)
				}
				if (isLoading) {
					CircularProgressIndicator()
				}
				preview?.exactMatches.orEmpty().forEach { match ->
					Text(
						text = ingredientMatchLabel(match.ingredient, match.recipeCount),
						style = PurecipesTheme.typography.bodyMedium,
					)
				}
				preview?.likelyMatches.orEmpty().forEach { match ->
					Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
						Text(
							text = "Did you mean ${match.ingredient}? " +
								ingredientMatchCountLabel(match.recipeCount),
							style = PurecipesTheme.typography.bodyMedium,
						)
						TextButton(onClick = { onAdd(match.ingredient) }) {
							Text(text = "Use ${match.ingredient}")
						}
					}
				}
			}
		},
		confirmButton = {
			Button(
				onClick = { onAdd(trimmedQuery) },
				enabled = trimmedQuery.isNotEmpty(),
				modifier = Modifier.testTag(ADD_INGREDIENT_CONFIRM_BUTTON_TAG),
			) {
				Text(text = "Add to pantry")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = "Cancel")
			}
		},
	)
}

private fun ingredientMatchLabel(ingredient: String, recipeCount: Int): String =
	"$ingredient — ${ingredientMatchCountLabel(recipeCount)}"

private fun ingredientMatchCountLabel(recipeCount: Int): String =
	when (recipeCount) {
		1 -> "1 recipe"
		else -> "$recipeCount recipes"
	}

@Preview(showBackground = true)
@Composable
private fun CustomIngredientChipPreview() {
	PurecipesTheme {
		CustomIngredientChip(
			item = "Gochujang",
			state = IngredientChipState.SELECTED,
			onToggle = {},
			onRemove = {},
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun AddIngredientDialogPreview() {
	PurecipesTheme {
		AddIngredientDialog(
			pantryIngredients = persistentSetOf(),
			excludedIngredients = persistentSetOf(),
			preview = IngredientMatchResponse(
				query = "tarragone",
				likelyMatches = listOf(
					LikelyIngredientMatch(
						ingredient = "Tarragon",
						recipeCount = 18,
						confidence = 0.92,
					),
				),
			),
			isLoading = false,
			onQueryChange = {},
			onDismiss = {},
			onAdd = {},
		)
	}
}
