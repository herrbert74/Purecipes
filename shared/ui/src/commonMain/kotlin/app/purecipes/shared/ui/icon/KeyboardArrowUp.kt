package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val keyboardArrowUpIcon: ImageVector
	get() {
		val cached = cachedKeyboardArrowUp
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "KeyboardArrowUp",
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
						moveTo(12f, 10.8f)
						lineTo(7.4f, 15.4f)
						lineTo(6f, 14f)
						lineTo(12f, 8f)
						lineToRelative(6f, 6f)
						lineToRelative(-1.4f, 1.4f)
						lineTo(12f, 10.8f)
						close()
					}
				}
				.build()
		cachedKeyboardArrowUp = icon
		return icon
	}

private var cachedKeyboardArrowUp: ImageVector? = null
