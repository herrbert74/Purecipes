package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val homeIcon: ImageVector
	get() {
		val cached = cachedHome
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Home",
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
						moveTo(4f, 21f)
						verticalLineTo(9f)
						lineTo(12f, 3f)
						lineToRelative(8f, 6f)
						verticalLineTo(21f)
						horizontalLineTo(14f)
						verticalLineTo(14f)
						horizontalLineTo(10f)
						verticalLineToRelative(7f)
						horizontalLineTo(4f)
						close()
					}
				}
				.build()
		cachedHome = icon
		return icon
	}

private var cachedHome: ImageVector? = null
