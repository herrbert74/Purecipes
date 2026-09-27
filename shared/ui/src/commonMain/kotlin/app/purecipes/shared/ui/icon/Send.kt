package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val sendIcon: ImageVector
	get() {
		val cached = cachedSend
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Send",
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
						moveTo(3f, 20f)
						verticalLineTo(14f)
						lineToRelative(8f, -2f)
						lineTo(3f, 10f)
						verticalLineTo(4f)
						lineToRelative(19f, 8f)
						lineTo(3f, 20f)
						close()
					}
				}
				.build()
		cachedSend = icon
		return icon
	}

private var cachedSend: ImageVector? = null
