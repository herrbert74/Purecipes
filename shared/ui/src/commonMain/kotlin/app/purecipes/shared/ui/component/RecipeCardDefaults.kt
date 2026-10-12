package app.purecipes.shared.ui.component

import androidx.compose.ui.unit.dp

object RecipeCardDefaults {

	const val FullWidthFraction = 1f
	const val CarouselWidthFraction = 0.85f
	val ShelfCardWidth = 160.dp
	const val ShelfImageAspectRatioWidth = 4f
	const val ShelfImageAspectRatioHeight = 3f

	enum class Layout {
		GRID,
		SHELF,
	}
}
