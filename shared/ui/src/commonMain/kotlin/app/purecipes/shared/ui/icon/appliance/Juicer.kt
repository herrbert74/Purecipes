package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val juicerIcon: ImageVector
	get() {
		val cached = cachedJuicer
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Juicer",
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
						moveTo(6.97f, 22f)
						quadToRelative(-0.78f, 0f, -1.34f, -0.5f)
						reflectiveQuadTo(5f, 20.23f)
						lineToRelative(-2f, -18.23f)
						horizontalLineToRelative(18f)
						lineToRelative(-2f, 18.23f)
						quadToRelative(-0.07f, 0.78f, -0.64f, 1.27f)
						reflectiveQuadTo(17.02f, 22f)
						horizontalLineTo(6.97f)
						close()
						moveToRelative(-0.2f, -4f)
						lineToRelative(0.23f, 2f)
						horizontalLineToRelative(10f)
						lineToRelative(0.23f, -2f)
						horizontalLineTo(6.78f)
						close()
						moveToRelative(-0.2f, -2f)
						horizontalLineToRelative(10.88f)
						lineToRelative(1.3f, -12f)
						horizontalLineTo(5.25f)
						lineToRelative(1.32f, 12f)
						close()
						moveToRelative(0.2f, 4f)
						horizontalLineToRelative(10.45f)
						horizontalLineToRelative(-10.45f)
						close()
					}
				}
				.build()
		cachedJuicer = icon
		return icon
	}

private var cachedJuicer: ImageVector? = null
