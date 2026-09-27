package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val lightbulbIcon: ImageVector
	get() {
		val cached = cachedLightbulb
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Lightbulb",
				defaultWidth = 24.dp,
				defaultHeight = 24.dp,
				viewportWidth = 24f,
				viewportHeight = 24f,
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
						moveTo(10.59f, 21.41f)
						quadTo(10f, 20.83f, 10f, 20f)
						horizontalLineToRelative(4f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(12f, 22f)
						reflectiveQuadTo(10.59f, 21.41f)
						close()
						moveTo(8f, 19f)
						verticalLineTo(17f)
						horizontalLineToRelative(8f)
						verticalLineToRelative(2f)
						horizontalLineTo(8f)
						close()
						moveTo(8.25f, 16f)
						quadTo(6.53f, 14.98f, 5.51f, 13.25f)
						quadTo(4.5f, 11.52f, 4.5f, 9.5f)
						quadTo(4.5f, 6.38f, 6.69f, 4.19f)
						reflectiveQuadTo(12f, 2f)
						reflectiveQuadToRelative(5.31f, 2.19f)
						reflectiveQuadTo(19.5f, 9.5f)
						quadToRelative(0f, 2.02f, -1.01f, 3.75f)
						reflectiveQuadTo(15.75f, 16f)
						horizontalLineTo(8.25f)
						close()
					}
				}
				.build()
		cachedLightbulb = icon
		return icon
	}

private var cachedLightbulb: ImageVector? = null
