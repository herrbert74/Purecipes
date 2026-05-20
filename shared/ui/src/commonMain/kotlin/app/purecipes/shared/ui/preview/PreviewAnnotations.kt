package app.purecipes.shared.ui.preview

import androidx.compose.ui.tooling.preview.Preview

const val PREVIEW_SCREEN_WIDTH_NORMAL_DP = 430
const val PREVIEW_SCREEN_WIDTH_SMALL_DP = 320
const val PREVIEW_SCREEN_HEIGHT_DP = 200

@Preview(
	name = "Normal",
	widthDp = PREVIEW_SCREEN_WIDTH_NORMAL_DP,
	heightDp = PREVIEW_SCREEN_HEIGHT_DP,
	showBackground = true,
)
@Preview(
	name = "Small",
	widthDp = PREVIEW_SCREEN_WIDTH_SMALL_DP,
	heightDp = PREVIEW_SCREEN_HEIGHT_DP,
	showBackground = true,
)
annotation class PreviewScreenSizes
