package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val libraryBooksIcon: ImageVector
	get() {
		val cached = cachedLibraryBooks
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "LibraryBooks",
				defaultWidth = 24.dp,
				defaultHeight = 24.dp,
				viewportWidth = 24f,
				viewportHeight = 24f,
				autoMirror = true,
			)
				.apply {
					path(
						fill = SolidColor(Color.Black),
						fillAlpha = 1f,
						stroke = null,
						strokeAlpha = 1f,
						strokeLineWidth = 1f,
						strokeLineCap = StrokeCap.Butt,
						strokeLineJoin = StrokeJoin.Bevel,
						strokeLineMiter = 1f,
						pathFillType = PathFillType.NonZero,
					) {
						moveTo(10f, 14f)
						horizontalLineToRelative(4f)
						verticalLineTo(12f)
						horizontalLineTo(10f)
						verticalLineToRelative(2f)
						close()
						moveToRelative(0f, -3f)
						horizontalLineToRelative(8f)
						verticalLineTo(9f)
						horizontalLineTo(10f)
						verticalLineToRelative(2f)
						close()
						moveTo(10f, 8f)
						horizontalLineToRelative(8f)
						verticalLineTo(6f)
						horizontalLineTo(10f)
						verticalLineTo(8f)
						close()
						moveTo(8f, 18f)
						quadTo(7.18f, 18f, 6.59f, 17.41f)
						reflectiveQuadTo(6f, 16f)
						verticalLineTo(4f)
						quadTo(6f, 3.17f, 6.59f, 2.59f)
						reflectiveQuadTo(8f, 2f)
						horizontalLineTo(20f)
						quadToRelative(0.83f, 0f, 1.41f, 0.59f)
						reflectiveQuadTo(22f, 4f)
						verticalLineTo(16f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(20f, 18f)
						horizontalLineTo(8f)
						close()
						moveTo(4f, 22f)
						quadTo(3.18f, 22f, 2.59f, 21.41f)
						reflectiveQuadTo(2f, 20f)
						verticalLineTo(6f)
						horizontalLineTo(4f)
						verticalLineTo(20f)
						horizontalLineTo(18f)
						verticalLineToRelative(2f)
						horizontalLineTo(4f)
						close()
					}
				}
				.build()
		cachedLibraryBooks = icon
		return icon
	}

private var cachedLibraryBooks: ImageVector? = null
