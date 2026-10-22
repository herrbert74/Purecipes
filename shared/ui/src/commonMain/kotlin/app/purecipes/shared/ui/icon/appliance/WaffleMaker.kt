package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val waffleMakerIcon: ImageVector
	get() {
		val cached = cachedWaffleMaker
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "WaffleMaker",
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
						moveTo(4f, 5f)
						horizontalLineTo(20f)
						quadTo(21f, 5f, 21f, 6f)
						verticalLineTo(17f)
						quadTo(21f, 18f, 20f, 18f)
						horizontalLineTo(4f)
						quadTo(3f, 18f, 3f, 17f)
						verticalLineTo(6f)
						quadTo(3f, 5f, 4f, 5f)
						close()
						moveTo(9f, 19f)
						horizontalLineTo(15f)
						verticalLineTo(21f)
						horizontalLineTo(9f)
						close()
						moveTo(6f, 7f)
						horizontalLineTo(10f)
						verticalLineTo(10f)
						horizontalLineTo(6f)
						close()
						moveTo(14f, 7f)
						horizontalLineTo(18f)
						verticalLineTo(10f)
						horizontalLineTo(14f)
						close()
						moveTo(6f, 12f)
						horizontalLineTo(10f)
						verticalLineTo(15f)
						horizontalLineTo(6f)
						close()
						moveTo(14f, 12f)
						horizontalLineTo(18f)
						verticalLineTo(15f)
						horizontalLineTo(14f)
						close()
					}
				}
				.build()
		cachedWaffleMaker = icon
		return icon
	}

private var cachedWaffleMaker: ImageVector? = null
