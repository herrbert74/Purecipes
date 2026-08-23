@file:Suppress("MagicNumber")

package app.purecipes.store.screenshots

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object BrandColors {

	val primary = Color(0xFF7E0046)
	val primaryContainer = Color(0xFFFFD9E3)
	val secondary = Color(0xFF745B0B)
	val secondaryContainer = Color(0xFFFFDF92)
	val onPrimary = Color(0xFFFFFFFF)
	val onPrimaryMuted = Color(0xFFFFD9E3)
	val onGoldMuted = Color(0xFFFFDF92)
	val deviceBezel = Color(0xFF1A1214)
	val deviceShine = Color(0xFF2A2123)

	fun portraitBackground(theme: MarketingTheme): Brush {
		return when (theme) {
			MarketingTheme.ROSE -> Brush.verticalGradient(
				colors = listOf(Color(0xFF5C0033), Color(0xFF7E0046)),
			)

			MarketingTheme.GOLD -> Brush.verticalGradient(
				colors = listOf(Color(0xFF3D2E05), Color(0xFF745B0B)),
			)

			MarketingTheme.DEEP -> Brush.verticalGradient(
				colors = listOf(Color(0xFF191113), Color(0xFF5C0033)),
			)
		}
	}

	fun featureBackground(theme: MarketingTheme): Brush {
		return when (theme) {
			MarketingTheme.ROSE -> Brush.horizontalGradient(
				colors = listOf(Color(0xFF3A001F), Color(0xFF7E0046)),
			)

			MarketingTheme.GOLD -> Brush.horizontalGradient(
				colors = listOf(Color(0xFF2A1F04), Color(0xFF745B0B)),
			)

			MarketingTheme.DEEP -> Brush.horizontalGradient(
				colors = listOf(Color(0xFF191113), Color(0xFF7E0046)),
			)
		}
	}

	fun subtitleColor(theme: MarketingTheme): Color {
		return when (theme) {
			MarketingTheme.GOLD -> onGoldMuted
			MarketingTheme.ROSE,
			MarketingTheme.DEEP,
				-> onPrimaryMuted
		}
	}
}
