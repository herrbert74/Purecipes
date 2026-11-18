package app.purecipes.feature.search.ui.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.ingredient_state_neutral
import purecipes.shared.ui.generated.resources.key_ingredient_state_selected
import purecipes.shared.ui.generated.resources.key_ingredients_cleared

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
	onGoToPantry: () -> Unit,
	modifier: Modifier = Modifier,
	isLocked: Boolean = false,
	onLockedClick: () -> Unit = {},
) {
	var collapsed by rememberSaveable { mutableStateOf(keyIngredients.isEmpty()) }
	val allIngredients = (pantryIngredients + keyIngredients).sorted()
	var announcement by remember { mutableStateOf<String?>(null) }
	val clearedAnnouncement = stringResource(Res.string.key_ingredients_cleared)
	val selectedState = stringResource(Res.string.key_ingredient_state_selected)
	val neutralState = stringResource(Res.string.ingredient_state_neutral)

	Column(modifier = modifier.testTag(FILTER_KEY_INGREDIENTS_SECTION_TAG)) {
		announcement?.let { message ->
			Box(
				modifier = Modifier
					.size(1.dp)
					.semantics {
						liveRegion = LiveRegionMode.Polite
						contentDescription = message
					},
			)
		}
		FilterSectionHeader(
			title = "Key ingredients",
			isCollapsed = collapsed,
			isLocked = isLocked,
			titleColor = PurecipesTheme.colorScheme.primary,
			subtitle = formatFilterSectionSelectionSubtitle(keyIngredients.sorted()),
			onToggleCollapse = {
				if (isLocked) {
					onLockedClick()
				} else {
					collapsed = !collapsed
				}
			},
			modifier = Modifier.testTag(filterSectionToggleTag("Key ingredients")),
		)
		AnimatedVisibility(
			visible = !collapsed && !isLocked,
			enter = expandVertically(),
			exit = shrinkVertically(),
		) {
			Column(
				modifier = Modifier.padding(bottom = PurecipesTheme.space.s),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				Text(
					text = "You can select ingredients from your pantry that must be included in the results.",
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier
						.testTag(FILTER_KEY_INGREDIENTS_INTRO_TAG)
						.padding(horizontal = PurecipesTheme.space.xl),
				)
				if (keyIngredients.isNotEmpty()) {
					FilterClearActionChip(
						onClearAll = {
							announcement = clearedAnnouncement
							onKeyIngredientsChange(emptySet())
						},
						clearAllTestTag = FILTER_KEY_INGREDIENTS_CLEAR_ALL_TAG,
					)
				}
				if (allIngredients.isNotEmpty()) {
					FlowRow(
						modifier = Modifier
							.fillMaxWidth()
							.testTag(FILTER_KEY_INGREDIENTS_PANTRY_QUICK_PICKS_TAG)
							.padding(horizontal = PurecipesTheme.space.xl),
						horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
						verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
					) {
						allIngredients.forEach { item ->
							key(item) {
								val isSelected = item in keyIngredients
								FilterChip(
									selected = isSelected,
									onClick = {
										val updated = if (isSelected) {
											keyIngredients - item
										} else {
											keyIngredients + item
										}
										onKeyIngredientsChange(updated)
									},
									label = { Text(item) },
									trailingIcon = if (isSelected) {
										{
											Icon(
												imageVector = AppIcons.Close,
												contentDescription = null,
												modifier = Modifier
													.size(FilterChipDefaults.IconSize)
													.testTag(keyIngredientRemoveTag(item)),
											)
										}
									} else {
										null
									},
									modifier = Modifier
										.semantics {
											stateDescription = if (isSelected) {
												selectedState
											} else {
												neutralState
											}
										}
										.testTag(
											if (isSelected) {
												keyIngredientChipTag(item)
											} else {
												keyIngredientPantryQuickPickTag(item)
											},
										),
								)
							}
						}
					}
				}
				if (pantryIngredients.isEmpty()) {
					EmptyPantryCallout(onGoToPantry = onGoToPantry)
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
			onGoToPantry = {},
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun KeyIngredientsSectionEmptyPantryPreview() {
	PurecipesTheme {
		KeyIngredientsSection(
			keyIngredients = persistentSetOf("Tomato"),
			pantryIngredients = persistentSetOf(),
			onKeyIngredientsChange = {},
			onGoToPantry = {},
		)
	}
}
