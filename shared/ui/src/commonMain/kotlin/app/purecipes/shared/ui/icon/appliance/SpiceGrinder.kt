package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val spiceGrinderIcon: ImageVector
	get() {
		val cached = cachedSpiceGrinder
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "SpiceGrinder",
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
						moveTo(6f, 22f)
						quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
						reflectiveQuadTo(4f, 20f)
						verticalLineToRelative(-16f)
						quadToRelative(0f, -0.82f, 0.59f, -1.41f)
						reflectiveQuadTo(6f, 2f)
						horizontalLineToRelative(14f)
						verticalLineToRelative(2f)
						horizontalLineToRelative(-2f)
						verticalLineToRelative(2f)
						quadToRelative(0f, 0.42f, -0.29f, 0.71f)
						reflectiveQuadTo(17f, 7f)
						horizontalLineTo(9f)
						quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
						reflectiveQuadTo(8f, 6f)
						verticalLineToRelative(-2f)
						horizontalLineToRelative(-2f)
						verticalLineToRelative(16f)
						horizontalLineToRelative(4.05f)
						quadToRelative(-0.95f, -0.68f, -1.5f, -1.71f)
						reflectiveQuadTo(8f, 16f)
						verticalLineToRelative(-5f)
						horizontalLineToRelative(10f)
						verticalLineToRelative(5f)
						quadToRelative(0f, 1.25f, -0.55f, 2.29f)
						reflectiveQuadTo(15.95f, 20f)
						horizontalLineToRelative(4.05f)
						verticalLineToRelative(2f)
						horizontalLineTo(6f)
						close()
						moveToRelative(7f, -3f)
						quadToRelative(1.25f, 0f, 2.12f, -0.88f)
						reflectiveQuadToRelative(0.88f, -2.12f)
						verticalLineToRelative(-3f)
						horizontalLineTo(10f)
						verticalLineToRelative(3f)
						quadToRelative(0f, 1.25f, 0.88f, 2.12f)
						reflectiveQuadToRelative(2.12f, 0.88f)
						close()
						moveToRelative(0f, -9f)
						quadToRelative(0.42f, 0f, 0.71f, -0.29f)
						reflectiveQuadTo(14f, 9f)
						quadToRelative(0f, -0.42f, -0.29f, -0.71f)
						reflectiveQuadTo(13f, 8f)
						quadToRelative(-0.42f, 0f, -0.71f, 0.29f)
						reflectiveQuadTo(12f, 9f)
						quadToRelative(0f, 0.42f, 0.29f, 0.71f)
						reflectiveQuadTo(13f, 10f)
						close()
						moveToRelative(0f, 3f)
						close()
					}
				}
				.build()
		cachedSpiceGrinder = icon
		return icon
	}

private var cachedSpiceGrinder: ImageVector? = null
