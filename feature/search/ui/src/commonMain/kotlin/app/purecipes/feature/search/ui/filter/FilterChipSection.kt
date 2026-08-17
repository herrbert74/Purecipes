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
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

internal fun filterSectionToggleTag(title: String): String =
	"filterSection${title.filter(Char::isLetterOrDigit)}"

internal fun filterChipTag(sectionTitle: String, itemLabel: String): String =
	"filterChip${sectionTitle.filter(Char::isLetterOrDigit)}${itemLabel.filter(Char::isLetterOrDigit)}"

internal fun filterRecipeClearAllTag(sectionTitle: String): String =
	"filterRecipeClearAll${sectionTitle.filter(Char::isLetterOrDigit)}"

@Composable
internal fun <T : Any> FilterChipSection(
	title: String,
	items: ImmutableList<T>,
	selected: ImmutableSet<T>,
	itemLabel: (T) -> String,
	onSelectionChange: (Set<T>) -> Unit,
	modifier: Modifier = Modifier,
	isLocked: Boolean = false,
	onLockedClick: () -> Unit = {},
) {
	var collapsed by rememberSaveable { mutableStateOf(true) }
	val selectedLabels = items.filter { it in selected }.map(itemLabel)
	Column(modifier = modifier) {
		FilterSectionHeader(
			title = title,
			isCollapsed = collapsed,
			isLocked = isLocked,
			subtitle = formatFilterSectionSelectionSubtitle(selectedLabels),
			onToggleCollapse = {
				if (isLocked) {
					onLockedClick()
				} else {
					collapsed = !collapsed
				}
			},
			modifier = Modifier.testTag(filterSectionToggleTag(title)),
		)
		AnimatedVisibility(
			visible = !collapsed && !isLocked,
			enter = expandVertically(),
			exit = shrinkVertically(),
		) {
			Column {
				AnimatedVisibility(
					visible = selected.size >= MIN_SELECTED_COUNT_FOR_CLEAR_ALL,
					enter = expandVertically(),
					exit = shrinkVertically(),
				) {
					FilterClearActionChip(
						onClearAll = { onSelectionChange(emptySet()) },
						clearAllTestTag = filterRecipeClearAllTag(title),
					)
				}
				FlowRow(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = PurecipesTheme.space.m),
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
					verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
				) {
					items.forEach { item ->
						FilterChip(
							selected = item in selected,
							onClick = {
								val updated = if (item in selected) selected - item else selected + item
								onSelectionChange(updated)
							},
							label = { Text(itemLabel(item)) },
							modifier = Modifier.testTag(filterChipTag(title, itemLabel(item))),
						)
					}
				}
			}
		}
	}
}
