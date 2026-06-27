package app.purecipes.feature.settings.ui.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.component.SectionHeader
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch

internal const val ABOUT_VERSION_ROW_TAG = "about_version_row"
internal const val ABOUT_TERMS_ROW_TAG = "about_terms_row"
internal const val ABOUT_PRIVACY_ROW_TAG = "about_privacy_row"
internal const val ABOUT_OSS_ROW_TAG = "about_oss_row"

@Composable
fun AboutScreen(
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AboutViewModel = metroViewModel(),
) {
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "About") },
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
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
	) { innerPadding ->
		AboutScreenContent(
			versionText = viewModel.versionText,
			onPlaceholderClick = {
				scope.launch {
					snackbarHostState.showSnackbar(COMING_SOON_MESSAGE)
				}
			},
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.m),
		)
	}
}

@Composable
internal fun AboutScreenContent(
	versionText: String,
	onPlaceholderClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		Surface(
			modifier = Modifier.fillMaxWidth(),
			shape = PurecipesTheme.shapes.large,
			tonalElevation = PurecipesTheme.space.quark,
		) {
			Column(
				modifier = Modifier.padding(PurecipesTheme.space.m),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				SectionHeader(
					title = "App information",
					subtitle = "Version and legal information about Purecipes.",
				)
				Text(
					text = versionText,
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = PurecipesTheme.space.xs)
						.testTag(ABOUT_VERSION_ROW_TAG),
				)
				HorizontalDivider()
				AboutRow(
					label = "Terms & Conditions",
					showChevron = true,
					onClick = onPlaceholderClick,
					modifier = Modifier.testTag(ABOUT_TERMS_ROW_TAG),
				)
				HorizontalDivider()
				AboutRow(
					label = "Privacy Policy",
					showChevron = true,
					onClick = onPlaceholderClick,
					modifier = Modifier.testTag(ABOUT_PRIVACY_ROW_TAG),
				)
				HorizontalDivider()
				AboutRow(
					label = "Open Source Licenses",
					showChevron = true,
					onClick = onPlaceholderClick,
					modifier = Modifier.testTag(ABOUT_OSS_ROW_TAG),
				)
			}
		}
	}
}

@Composable
private fun AboutRow(
	label: String,
	modifier: Modifier = Modifier,
	value: String? = null,
	showChevron: Boolean = false,
	onClick: (() -> Unit)? = null,
) {
	val rowModifier = if (onClick != null) {
		modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(vertical = PurecipesTheme.space.xs)
	} else {
		modifier
			.fillMaxWidth()
			.padding(vertical = PurecipesTheme.space.xs)
	}

	Row(
		modifier = rowModifier,
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Text(
			text = label,
			style = PurecipesTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			modifier = Modifier.weight(1f),
		)
		if (value != null) {
			Text(
				text = value,
				style = PurecipesTheme.typography.bodySmall,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(end = PurecipesTheme.space.s),
			)
		}
		if (showChevron) {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
				contentDescription = null,
				tint = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

private const val COMING_SOON_MESSAGE = "Coming soon"

@Preview(
	name = "About screen light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun AboutScreenLightPreview() {
	PurecipesTheme(darkTheme = false) {
		AboutScreenContent(
			versionText = "Version 0.4.0 (7)",
			onPlaceholderClick = {},
			modifier = Modifier
				.fillMaxSize()
				.padding(PurecipesTheme.space.m),
		)
	}
}

@Preview(
	name = "About screen dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun AboutScreenDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		AboutScreenContent(
			versionText = "Version 0.4.0 (7)",
			onPlaceholderClick = {},
			modifier = Modifier
				.fillMaxSize()
				.padding(PurecipesTheme.space.m),
		)
	}
}
