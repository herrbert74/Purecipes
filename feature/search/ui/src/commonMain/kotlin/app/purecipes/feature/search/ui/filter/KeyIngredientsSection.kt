package app.purecipes.feature.search.ui.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

internal const val FILTER_KEY_INGREDIENTS_SECTION_TAG = "filterKeyIngredientsSection"
internal const val FILTER_KEY_INGREDIENTS_INTRO_TAG = "filterKeyIngredientsIntro"
internal const val FILTER_KEY_INGREDIENTS_CLEAR_ALL_TAG = "filterKeyIngredientsClearAll"
internal const val FILTER_KEY_INGREDIENTS_PANTRY_QUICK_PICKS_TAG = "filterKeyIngredientsPantryQuickPicks"

internal fun keyIngredientChipTag(item: String): String =
	"keyIngredientChip${item.filter(Char::isLetterOrDigit)}"

internal fun keyIngredientPantryQuickPickTag(item: String): String =
	"keyIngredientPantryQuickPick${item.filter(Char::isLetterOrDigit)}"

@Composable
internal fun KeyIngredientsSection(
	keyIngredients: ImmutableSet<String>,
	pantryIngredients: ImmutableSet<String>,
	onKeyIngredientsChange: (Set<String>) -> Unit,
	modifier: Modifier = Modifier,
) {
	var collapsed by rememberSaveable { mutableStateOf(false) }
	val pantryQuickPicks = pantryIngredients - keyIngredients

	Column(modifier = modifier.testTag(FILTER_KEY_INGREDIENTS_SECTION_TAG)) {
		FilterSectionHeader(
			title = "Key ingredients",
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
				Text(
					text = "Recipes must include all selected ingredients.",
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier
						.testTag(FILTER_KEY_INGREDIENTS_INTRO_TAG)
						.padding(horizontal = PurecipesTheme.space.xl),
				)
				if (keyIngredients.isNotEmpty()) {
					FilterClearActionChip(
						onClearAll = { onKeyIngredientsChange(emptySet()) },
						clearAllTestTag = FILTER_KEY_INGREDIENTS_CLEAR_ALL_TAG,
					)
					FlowRow(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = PurecipesTheme.space.xl),
						horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
						verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
					) {
						keyIngredients.sorted().forEach { item ->
							KeyIngredientChip(
								item = item,
								onRemove = { onKeyIngredientsChange(keyIngredients - item) },
								modifier = Modifier.testTag(keyIngredientChipTag(item)),
							)
						}
					}
				}
				if (pantryQuickPicks.isNotEmpty()) {
					Column(
						modifier = Modifier
							.testTag(FILTER_KEY_INGREDIENTS_PANTRY_QUICK_PICKS_TAG)
							.padding(horizontal = PurecipesTheme.space.xl),
						verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
					) {
						Text(
							text = "From your pantry",
							style = PurecipesTheme.typography.labelLarge,
						)
						FlowRow(
							horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
							verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
						) {
							pantryQuickPicks.sorted().forEach { item ->
								FilterChip(
									selected = false,
									onClick = { onKeyIngredientsChange(keyIngredients + item) },
									label = { Text(item) },
									modifier = Modifier.testTag(keyIngredientPantryQuickPickTag(item)),
								)
							}
						}
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun KeyIngredientsSectionPreview() {
	PurecipesTheme {
		KeyIngredientsSection(
			keyIngredients = persistentSetOf("Tomato", "Chicken"),
			pantryIngredients = persistentSetOf("Chicken", "Rice", "Tomato"),
			onKeyIngredientsChange = {},
		)
	}
}
