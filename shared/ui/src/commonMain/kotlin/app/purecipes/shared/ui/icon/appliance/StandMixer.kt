package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val standMixerIcon: ImageVector
	get() {
		val cached = cachedStandMixer
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "StandMixer",
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
						moveTo(3f, 20f)
						horizontalLineTo(21f)
						verticalLineTo(22f)
						horizontalLineTo(3f)
						close()
						moveTo(4f, 8f)
						horizontalLineTo(8f)
						verticalLineTo(20f)
						horizontalLineTo(4f)
						close()
						moveTo(4f, 5f)
						horizontalLineTo(18f)
						quadTo(20f, 5f, 20f, 7f)
						quadTo(20f, 9f, 18f, 9f)
						horizontalLineTo(4f)
						verticalLineTo(5f)
						close()
						moveTo(9f, 12f)
						horizontalLineTo(19f)
						verticalLineTo(17f)
						quadTo(19f, 20f, 14f, 20f)
						quadTo(9f, 20f, 9f, 17f)
						verticalLineTo(12f)
						close()
						moveTo(13.5f, 9f)
						horizontalLineTo(14.5f)
						verticalLineTo(14f)
						horizontalLineTo(13.5f)
						close()
					}
				}
				.build()
		cachedStandMixer = icon
		return icon
	}

private var cachedStandMixer: ImageVector? = null
