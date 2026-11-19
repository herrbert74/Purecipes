package app.purecipes.feature.search.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.clear_all
import purecipes.shared.ui.generated.resources.clear_all_description
import purecipes.shared.ui.generated.resources.select_all
import purecipes.shared.ui.generated.resources.select_all_description

internal const val MIN_SELECTED_COUNT_FOR_CLEAR_ALL = 2

@Composable
internal fun FilterBulkActionChips(
	onSelectAll: () -> Unit,
	onClearAll: () -> Unit,
	modifier: Modifier = Modifier,
	showClearAll: Boolean = true,
	selectAllTestTag: String? = null,
	clearAllTestTag: String? = null,
) {
	val selectAllDescription = stringResource(Res.string.select_all_description)
	val clearAllDescription = stringResource(Res.string.clear_all_description)
	FlowRow(
		modifier = modifier
			.fillMaxWidth()
			.padding(
				start = PurecipesTheme.space.m,
				end = PurecipesTheme.space.m,
				bottom = PurecipesTheme.space.xs,
			),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		AssistChip(
			onClick = onSelectAll,
			label = { Text(text = stringResource(Res.string.select_all)) },
			leadingIcon = {
				Icon(
					imageVector = AppIcons.DoneAll,
					contentDescription = null,
				)
			},
			modifier = Modifier
				.semantics { contentDescription = selectAllDescription }
				.then(selectAllTestTag?.let { Modifier.testTag(it) } ?: Modifier),
		)
		if (showClearAll) {
			AssistChip(
				onClick = onClearAll,
				label = { Text(text = stringResource(Res.string.clear_all)) },
				leadingIcon = {
					Icon(
						imageVector = AppIcons.Clear,
						contentDescription = null,
					)
				},
				modifier = Modifier
					.semantics { contentDescription = clearAllDescription }
					.then(clearAllTestTag?.let { Modifier.testTag(it) } ?: Modifier),
			)
		}
	}
}

@Composable
internal fun FilterClearActionChip(
	onClearAll: () -> Unit,
	modifier: Modifier = Modifier,
	clearAllTestTag: String? = null,
) {
	val clearAllDescription = stringResource(Res.string.clear_all_description)
	FlowRow(
		modifier = modifier
			.fillMaxWidth()
			.padding(
				start = PurecipesTheme.space.m,
				end = PurecipesTheme.space.m,
				bottom = PurecipesTheme.space.xs,
			),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		AssistChip(
			onClick = onClearAll,
			label = { Text(text = stringResource(Res.string.clear_all)) },
			leadingIcon = {
				Icon(
					imageVector = AppIcons.Clear,
					contentDescription = null,
				)
			},
			modifier = Modifier
				.semantics { contentDescription = clearAllDescription }
				.then(clearAllTestTag?.let { Modifier.testTag(it) } ?: Modifier),
		)
	}
}
