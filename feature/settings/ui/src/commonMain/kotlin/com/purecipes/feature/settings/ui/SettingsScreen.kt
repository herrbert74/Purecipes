package com.purecipes.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ResetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.SaveMeasurementPreferencesUseCase
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeFormatHandling
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
	observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	resetMeasurementPreferences: ResetMeasurementPreferencesUseCase,
	saveMeasurementPreferences: SaveMeasurementPreferencesUseCase,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Settings") },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back",
						)
					}
				},
			)
		},
	) { innerPadding ->
		MeasurementPreferencesSection(
			observeMeasurementPreferences = observeMeasurementPreferences,
			resetMeasurementPreferences = resetMeasurementPreferences,
			saveMeasurementPreferences = saveMeasurementPreferences,
			modifier = Modifier.padding(innerPadding),
		)
	}
}

@Composable
private fun MeasurementPreferencesSection(
	observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	resetMeasurementPreferences: ResetMeasurementPreferencesUseCase,
	saveMeasurementPreferences: SaveMeasurementPreferencesUseCase,
	modifier: Modifier = Modifier,
) {
	val preferences by observeMeasurementPreferences().collectAsState(initial = null)
	val scope = rememberCoroutineScope()
	val currentPreferences = preferences ?: return

	Column(
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 20.dp, vertical = 16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		Surface(
			modifier = Modifier.fillMaxWidth(),
			shape = MaterialTheme.shapes.large,
			tonalElevation = 2.dp,
		) {
			Column(
				modifier = Modifier.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(14.dp),
			) {
				Text(
					text = "Measurements",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold,
				)
				Text(
					text = currentPreferences.measurementSummary(),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				MeasurementSystemChooser(
					preferences = currentPreferences,
					onPreferencesChange = { updatedPreferences ->
						scope.launch { saveMeasurementPreferences(updatedPreferences) }
					},
				)
				HorizontalDivider()
				RecipeFormatHandlingChooser(
					preferences = currentPreferences,
					onPreferencesChange = { updatedPreferences ->
						scope.launch { saveMeasurementPreferences(updatedPreferences) }
					},
				)
				OutlinedButton(
					modifier = Modifier.fillMaxWidth(),
					onClick = { scope.launch { resetMeasurementPreferences() } },
					contentPadding = PaddingValues(vertical = 12.dp),
				) {
					Text(
						text = "Reset to detected default",
						style = MaterialTheme.typography.bodyMedium,
					)
				}
			}
		}
	}
}

@Composable
private fun MeasurementSystemChooser(
	preferences: MeasurementPreferences,
	onPreferencesChange: (MeasurementPreferences) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		Text(
			text = "Preferred system",
			style = MaterialTheme.typography.titleSmall,
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
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		Text(
			text = "Recipes in another system",
			style = MaterialTheme.typography.titleSmall,
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
		shape = RoundedCornerShape(16.dp),
		tonalElevation = if (selected) 2.dp else 0.dp,
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.selectable(
					selected = selected,
					onClick = onClick,
					role = Role.RadioButton,
				)
				.padding(horizontal = 12.dp, vertical = 10.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			RadioButton(
				selected = selected,
				onClick = null,
			)
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp),
			) {
				Text(
					text = label,
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Medium,
				)
				Text(
					text = description,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
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
	}
	return "Detected region: $region. Current preference: $systemLabel."
}
