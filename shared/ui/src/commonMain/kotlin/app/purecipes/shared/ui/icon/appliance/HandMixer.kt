package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val handMixerIcon: ImageVector
	get() {
		val cached = cachedHandMixer
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "HandMixer",
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
						moveTo(3f, 7f)
						horizontalLineTo(17f)
						quadTo(19f, 7f, 19f, 9f)
						verticalLineTo(12f)
						horizontalLineTo(3f)
						verticalLineTo(7f)
						close()
						moveTo(5f, 7f)
						verticalLineTo(4f)
						quadTo(5f, 3f, 6f, 3f)
						horizontalLineTo(14f)
						quadTo(15f, 3f, 15f, 4f)
						verticalLineTo(7f)
						horizontalLineTo(13f)
						verticalLineTo(5f)
						horizontalLineTo(7f)
						verticalLineTo(7f)
						close()
						moveTo(6f, 12f)
						horizontalLineTo(8f)
						verticalLineTo(15f)
						lineTo(9f, 18f)
						verticalLineTo(21f)
						horizontalLineTo(5f)
						verticalLineTo(18f)
						lineTo(6f, 15f)
						close()
						moveTo(12f, 12f)
						horizontalLineTo(14f)
						verticalLineTo(15f)
						lineTo(15f, 18f)
						verticalLineTo(21f)
						horizontalLineTo(11f)
						verticalLineTo(18f)
						lineTo(12f, 15f)
						close()
					}
				}
				.build()
		cachedHandMixer = icon
		return icon
	}

private var cachedHandMixer: ImageVector? = null
