package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val immersionBlenderIcon: ImageVector
	get() {
		val cached = cachedImmersionBlender
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "ImmersionBlender",
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
						moveTo(10f, 2f)
						horizontalLineTo(14f)
						verticalLineTo(10f)
						horizontalLineTo(10f)
						close()
						moveTo(11.5f, 5f)
						horizontalLineTo(12.5f)
						verticalLineTo(7f)
						horizontalLineTo(11.5f)
						close()
						moveTo(11f, 10f)
						horizontalLineTo(13f)
						verticalLineTo(17f)
						horizontalLineTo(11f)
						close()
						moveTo(11f, 17f)
						lineTo(7f, 21f)
						verticalLineTo(22f)
						horizontalLineTo(17f)
						verticalLineTo(21f)
						lineTo(13f, 17f)
						close()
						moveTo(9f, 21f)
						horizontalLineTo(15f)
						lineTo(13f, 19f)
						horizontalLineTo(11f)
						close()
					}
				}
				.build()
		cachedImmersionBlender = icon
		return icon
	}

private var cachedImmersionBlender: ImageVector? = null
