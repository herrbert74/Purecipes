package app.purecipes.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeFormatHandling
import app.purecipes.shared.ui.component.PurecipesOutlinedButton
import app.purecipes.shared.ui.component.SectionHeader
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun MeasurementPreferencesPanel(
	preferences: MeasurementPreferences,
	onPreferencesChange: (MeasurementPreferences) -> Unit,
	onReset: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		shape = PurecipesTheme.shapes.large,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			SectionHeader(
				title = "Measurements",
				subtitle = preferences.measurementSummary(),
			)
			MeasurementSystemChooser(
				preferences = preferences,
				onPreferencesChange = onPreferencesChange,
			)
			HorizontalDivider()
			RecipeFormatHandlingChooser(
				preferences = preferences,
				onPreferencesChange = onPreferencesChange,
			)
			PurecipesOutlinedButton(
				text = "Reset to detected default",
				onClick = onReset,
			)
		}
	}
}

@Composable
private fun MeasurementSystemChooser(
	preferences: MeasurementPreferences,
	onPreferencesChange: (MeasurementPreferences) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		Text(
			text = "Preferred system",
			style = PurecipesTheme.typography.titleSmall,
			fontWeight = FontWeight.Medium,
		)
		MeasurementOptionRow(
			selected = preferences.preferredSystem == MeasurementSystem.METRIC,
			label = "Metric",
			description = "Grams, liters, Celsius",
			onClick = {
				onPreferencesChange(preferences.copy(preferredSystem = MeasurementSystem.METRIC))
			},
		)
		MeasurementOptionRow(
			selected = preferences.preferredSystem == MeasurementSystem.IMPERIAL,
			label = "Imperial",
			description = "Cups, ounces, Fahrenheit",
			onClick = {
				onPreferencesChange(preferences.copy(preferredSystem = MeasurementSystem.IMPERIAL))
			},
		)
	}
}

@Composable
private fun RecipeFormatHandlingChooser(
	preferences: MeasurementPreferences,
	onPreferencesChange: (MeasurementPreferences) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		Text(
			text = "Recipes in another system",
			style = PurecipesTheme.typography.titleSmall,
			fontWeight = FontWeight.Medium,
		)
		MeasurementOptionRow(
			selected = preferences.formatHandling == RecipeFormatHandling.KEEP_AS_IS,
			label = "Keep as is",
			description = "Show the recipe in its original measurements",
			onClick = {
				onPreferencesChange(preferences.copy(formatHandling = RecipeFormatHandling.KEEP_AS_IS))
			},
		)
		MeasurementOptionRow(
			selected = preferences.formatHandling == RecipeFormatHandling.FILTER_OUT,
			label = "Filter out",
			description = "Hide recipes that use another measurement system",
			onClick = {
				onPreferencesChange(preferences.copy(formatHandling = RecipeFormatHandling.FILTER_OUT))
			},
		)
		MeasurementOptionRow(
			selected = preferences.formatHandling == RecipeFormatHandling.CONVERT_TO_PREFERRED,
			label = "Convert to my system",
			description = "Convert common units and oven temperatures automatically",
			onClick = {
				onPreferencesChange(preferences.copy(formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED))
			},
		)
	}
}

@Composable
private fun MeasurementOptionRow(
	selected: Boolean,
	label: String,
	description: String,
	onClick: () -> Unit,
) {
	Surface(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(PurecipesTheme.space.m),
		tonalElevation = if (selected) {
			PurecipesTheme.space.quark
		} else {
			PurecipesTheme.space.none
		},
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.selectable(
					selected = selected,
					onClick = onClick,
					role = Role.RadioButton,
				)
				.padding(horizontal = PurecipesTheme.space.s, vertical = PurecipesTheme.space.s),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			RadioButton(
				selected = selected,
				onClick = null,
			)
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.quark),
			) {
				Text(
					text = label,
					style = PurecipesTheme.typography.bodyMedium,
					fontWeight = FontWeight.Medium,
				)
				Text(
					text = description,
					style = PurecipesTheme.typography.bodySmall,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}

private fun MeasurementPreferences.measurementSummary(): String {
	val region = detectedCountryCode ?: "Unknown region"
	val systemLabel = when (preferredSystem) {
		MeasurementSystem.METRIC -> "Metric"
		MeasurementSystem.IMPERIAL -> "Imperial"
		MeasurementSystem.MIXED -> "Mixed"
	}
	return "Detected region: $region. Current preference: $systemLabel."
}

private val previewMeasurementPreferences = MeasurementPreferences(
	preferredSystem = MeasurementSystem.METRIC,
	formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED,
	detectedCountryCode = "HU",
)

@Preview(
	name = "Measurement preferences light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun MeasurementPreferencesPanelLightPreview() {
	PurecipesTheme(darkTheme = false) {
		MeasurementPreferencesPanel(
			preferences = previewMeasurementPreferences,
			onPreferencesChange = {},
			onReset = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

@Preview(
	name = "Measurement preferences dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun MeasurementPreferencesPanelDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		MeasurementPreferencesPanel(
			preferences = previewMeasurementPreferences,
			onPreferencesChange = {},
			onReset = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}
