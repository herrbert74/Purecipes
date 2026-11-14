package app.purecipes.shared.ui.component.organism

import androidx.compose.ui.unit.dp

object RecipeCardDefaults {

	const val FullWidthFraction = 1f
	const val CarouselWidthFraction = 0.85f
	const val GridImageAspectRatioWidth = 16f
	const val GridImageAspectRatioHeight = 9f
	val ShelfCardWidth = 160.dp
	const val ShelfImageAspectRatioWidth = 4f
	const val ShelfImageAspectRatioHeight = 3f
	const val ShelfTitleLines = 2

	enum class Layout {
		GRID,
		SHELF,
	}
}
