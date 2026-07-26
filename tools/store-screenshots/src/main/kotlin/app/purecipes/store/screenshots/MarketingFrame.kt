package app.purecipes.store.screenshots

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val FEATURE_COPY_WEIGHT = 1.1f
private const val FEATURE_DEVICE_WEIGHT = 0.9f
private const val DEVICE_FRAME_WIDTH_FRACTION = 0.82f
private const val DEVICE_INNER_CORNER_INSET_DP = 6
private val DeviceInnerCornerInset = DEVICE_INNER_CORNER_INSET_DP.dp

@Composable
fun MarketingFrame(
	title: String,
	subtitle: String?,
	screenshot: ImageBitmap,
	outputSize: StoreOutputSize,
	fontFamily: FontFamily,
	theme: MarketingTheme,
	modifier: Modifier = Modifier,
) {
	when (outputSize) {
		StoreOutputSize.FEATURE_GRAPHIC -> FeatureGraphicFrame(
			title = title,
			subtitle = subtitle,
			screenshot = screenshot,
			fontFamily = fontFamily,
			theme = theme,
			modifier = modifier,
		)

		StoreOutputSize.PHONE,
		StoreOutputSize.TABLET_7,
		StoreOutputSize.TABLET_10,
			-> PortraitMarketingFrame(
			title = title,
			subtitle = subtitle,
			screenshot = screenshot,
			fontFamily = fontFamily,
			theme = theme,
			modifier = modifier,
		)
	}
}

@Composable
private fun PortraitMarketingFrame(
	title: String,
	subtitle: String?,
	screenshot: ImageBitmap,
	fontFamily: FontFamily,
	theme: MarketingTheme,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.background(BrandColors.portraitBackground(theme))
			.padding(horizontal = 48.dp, vertical = 56.dp),
	) {
		Column(
			modifier = Modifier.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Text(
				text = title,
				color = BrandColors.onPrimary,
				fontFamily = fontFamily,
				fontWeight = FontWeight.Bold,
				fontSize = 54.sp,
				lineHeight = 62.sp,
				textAlign = TextAlign.Center,
				modifier = Modifier.fillMaxWidth(),
			)
			if (subtitle != null) {
				Spacer(modifier = Modifier.height(16.dp))
				Text(
					text = subtitle,
					color = BrandColors.subtitleColor(theme),
					fontFamily = fontFamily,
					fontWeight = FontWeight.Normal,
					fontSize = 28.sp,
					lineHeight = 36.sp,
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth(),
				)
			}
			Spacer(modifier = Modifier.height(40.dp))
			DeviceFrame(
				screenshot = screenshot,
				cornerRadius = 36.dp,
				bezelPadding = 14.dp,
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f),
			)
		}
	}
}

@Composable
private fun FeatureGraphicFrame(
	title: String,
	subtitle: String?,
	screenshot: ImageBitmap,
	fontFamily: FontFamily,
	theme: MarketingTheme,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.fillMaxSize()
			.background(BrandColors.featureBackground(theme))
			.padding(horizontal = 48.dp, vertical = 36.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(36.dp),
	) {
		Column(
			modifier = Modifier.weight(FEATURE_COPY_WEIGHT),
			verticalArrangement = Arrangement.Center,
		) {
			Text(
				text = title,
				color = BrandColors.onPrimary,
				fontFamily = fontFamily,
				fontWeight = FontWeight.Bold,
				fontSize = 42.sp,
				lineHeight = 48.sp,
			)
			if (subtitle != null) {
				Spacer(modifier = Modifier.height(12.dp))
				Text(
					text = subtitle,
					color = BrandColors.subtitleColor(theme),
					fontFamily = fontFamily,
					fontWeight = FontWeight.Normal,
					fontSize = 22.sp,
					lineHeight = 28.sp,
				)
			}
		}
		DeviceFrame(
			screenshot = screenshot,
			cornerRadius = 28.dp,
			bezelPadding = 10.dp,
			modifier = Modifier
				.weight(FEATURE_DEVICE_WEIGHT)
				.fillMaxHeight(),
		)
	}
}

@Composable
private fun DeviceFrame(
	screenshot: ImageBitmap,
	cornerRadius: Dp,
	bezelPadding: Dp,
	modifier: Modifier = Modifier,
) {
	val outerShape = RoundedCornerShape(cornerRadius)
	val innerShape = RoundedCornerShape(cornerRadius - DeviceInnerCornerInset)
	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center,
	) {
		Box(
			modifier = Modifier
				.fillMaxHeight()
				.fillMaxWidth(DEVICE_FRAME_WIDTH_FRACTION)
				.shadow(elevation = 28.dp, shape = outerShape, clip = false)
				.clip(outerShape)
				.background(
					Brush.verticalGradient(
						colors = listOf(BrandColors.deviceShine, BrandColors.deviceBezel),
					),
				)
				.padding(bezelPadding),
		) {
			Image(
				bitmap = screenshot,
				contentDescription = null,
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.fillMaxSize()
					.clip(innerShape),
			)
		}
	}
}
