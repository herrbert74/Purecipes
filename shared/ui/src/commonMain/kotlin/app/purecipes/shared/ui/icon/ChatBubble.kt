package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val chatBubbleIcon: ImageVector
	get() {
		val cached = cachedChatBubble
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "ChatBubble",
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
						moveTo(2f, 22f)
						verticalLineTo(4f)
						quadTo(2f, 3.17f, 2.59f, 2.59f)
						reflectiveQuadTo(4f, 2f)
						horizontalLineTo(20f)
						quadToRelative(0.83f, 0f, 1.41f, 0.59f)
						reflectiveQuadTo(22f, 4f)
						verticalLineTo(16f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(20f, 18f)
						horizontalLineTo(6f)
						lineTo(2f, 22f)
						close()
						moveTo(5.15f, 16f)
						horizontalLineTo(20f)
						verticalLineTo(4f)
						horizontalLineTo(4f)
						verticalLineTo(17.13f)
						lineTo(5.15f, 16f)
						close()
						moveTo(4f, 16f)
						verticalLineTo(4f)
						verticalLineTo(16f)
						close()
					}
				}
				.build()
		cachedChatBubble = icon
		return icon
	}

private var cachedChatBubble: ImageVector? = null
