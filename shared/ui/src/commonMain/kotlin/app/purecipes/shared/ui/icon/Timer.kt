package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val timerIcon: ImageVector
	get() {
		val cached = cachedTimer
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Timer",
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
						moveTo(9f, 3f)
						verticalLineTo(1f)
						horizontalLineToRelative(6f)
						verticalLineTo(3f)
						horizontalLineTo(9f)
						close()
						moveToRelative(2f, 11f)
						horizontalLineToRelative(2f)
						verticalLineTo(8f)
						horizontalLineTo(11f)
						verticalLineToRelative(6f)
						close()
						moveTo(8.51f, 21.29f)
						quadTo(6.88f, 20.58f, 5.65f, 19.35f)
						reflectiveQuadTo(3.71f, 16.49f)
						reflectiveQuadTo(3f, 13f)
						reflectiveQuadTo(3.71f, 9.51f)
						reflectiveQuadTo(5.65f, 6.65f)
						quadTo(6.88f, 5.43f, 8.51f, 4.71f)
						reflectiveQuadTo(12f, 4f)
						quadToRelative(1.55f, 0f, 2.98f, 0.5f)
						reflectiveQuadToRelative(2.68f, 1.45f)
						lineToRelative(1.4f, -1.4f)
						lineToRelative(1.4f, 1.4f)
						lineToRelative(-1.4f, 1.4f)
						quadTo(20f, 8.6f, 20.5f, 10.02f)
						reflectiveQuadTo(21f, 13f)
						quadToRelative(0f, 1.85f, -0.71f, 3.49f)
						reflectiveQuadToRelative(-1.94f, 2.86f)
						reflectiveQuadToRelative(-2.86f, 1.94f)
						reflectiveQuadTo(12f, 22f)
						reflectiveQuadTo(8.51f, 21.29f)
						close()
					}
				}
				.build()
		cachedTimer = icon
		return icon
	}

private var cachedTimer: ImageVector? = null
