package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val expandMoreIcon: ImageVector
	get() {
		val cached = cachedExpandMore
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "ExpandMore",
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
						moveTo(12f, 15.38f)
						lineToRelative(-6f, -6f)
						lineTo(7.4f, 7.97f)
						lineToRelative(4.6f, 4.6f)
						lineToRelative(4.6f, -4.6f)
						lineTo(18f, 9.38f)
						lineToRelative(-6f, 6f)
						close()
					}
				}
				.build()
		cachedExpandMore = icon
		return icon
	}

private var cachedExpandMore: ImageVector? = null
