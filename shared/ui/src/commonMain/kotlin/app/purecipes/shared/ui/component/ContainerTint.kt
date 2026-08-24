package app.purecipes.shared.ui.component

import androidx.compose.runtime.Composable
import app.purecipes.shared.ui.theme.ColorFamily
import app.purecipes.shared.ui.theme.PurecipesTheme

enum class ContainerTint {
	Primary,
	Secondary,
	Tertiary,
	;

	companion object {

		fun forIndex(index: Int): ContainerTint {
			val values = entries
			return values[index.mod(values.size)]
		}
	}
}

@Composable
fun ContainerTint.colorFamily(): ColorFamily {
	val scheme = PurecipesTheme.colorScheme
	return when (this) {
		ContainerTint.Primary -> ColorFamily(
			color = scheme.primary,
			onColor = scheme.onPrimary,
			colorContainer = scheme.primaryContainer,
			onColorContainer = scheme.onPrimaryContainer,
		)

		ContainerTint.Secondary -> ColorFamily(
			color = scheme.secondary,
			onColor = scheme.onSecondary,
			colorContainer = scheme.secondaryContainer,
			onColorContainer = scheme.onSecondaryContainer,
		)

		ContainerTint.Tertiary -> ColorFamily(
			color = scheme.tertiary,
			onColor = scheme.onTertiary,
			colorContainer = scheme.tertiaryContainer,
			onColorContainer = scheme.onTertiaryContainer,
		)
	}
}
