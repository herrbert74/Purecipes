package app.purecipes.feature.featurerequests.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val FEATURE_REQUEST_SORT_CHIP_TAG_PREFIX = "featureRequestSortChip:"

internal const val FEATURE_REQUEST_STATUS_CHIP_TAG_PREFIX = "featureRequestStatusChip:"

internal const val FEATURE_REQUEST_STATUS_CHIP_ALL_TAG = "featureRequestStatusChip:ALL"

@Composable
internal fun FeatureRequestFilters(
	sort: FeatureRequestSort,
	statusFilter: FeatureRequestStatus?,
	onSortSelect: (FeatureRequestSort) -> Unit,
	onStatusFilterSelect: (FeatureRequestStatus?) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.horizontalScroll(rememberScrollState()),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			FeatureRequestSort.entries.forEach { entry ->
				FilterChip(
					selected = entry == sort,
					onClick = { onSortSelect(entry) },
					label = { Text(text = entry.label()) },
					modifier = Modifier.testTag("$FEATURE_REQUEST_SORT_CHIP_TAG_PREFIX${entry.name}"),
				)
			}
		}
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.horizontalScroll(rememberScrollState()),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			FilterChip(
				selected = statusFilter == null,
				onClick = { onStatusFilterSelect(null) },
				label = { Text(text = "All") },
				modifier = Modifier.testTag(FEATURE_REQUEST_STATUS_CHIP_ALL_TAG),
			)
			FeatureRequestStatus.entries.forEach { entry ->
				FilterChip(
					selected = entry == statusFilter,
					onClick = { onStatusFilterSelect(entry) },
					label = { Text(text = entry.label()) },
					modifier = Modifier.testTag("$FEATURE_REQUEST_STATUS_CHIP_TAG_PREFIX${entry.name}"),
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun FeatureRequestFiltersPreview() {
	PurecipesPreviewScaffold {
		FeatureRequestFilters(
			sort = FeatureRequestSort.TOP_VOTES,
			statusFilter = FeatureRequestStatus.OPEN,
			onSortSelect = {},
			onStatusFilterSelect = {},
		)
	}
}
