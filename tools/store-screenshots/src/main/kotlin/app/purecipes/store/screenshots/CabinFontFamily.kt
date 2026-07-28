package app.purecipes.store.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import java.io.File

@Composable
fun rememberCabinFontFamily(fontsDirectory: File): FontFamily {
	return remember(fontsDirectory) {
		val regular = File(fontsDirectory, "cabin_regular.ttf")
		val italic = File(fontsDirectory, "cabin_italic.ttf")
		require(regular.isFile) { "Missing Cabin regular font at ${regular.absolutePath}" }
		require(italic.isFile) { "Missing Cabin italic font at ${italic.absolutePath}" }
		FontFamily(
			Font(file = regular, weight = FontWeight.Normal, style = FontStyle.Normal),
			Font(file = italic, weight = FontWeight.Normal, style = FontStyle.Italic),
			Font(file = regular, weight = FontWeight.Bold, style = FontStyle.Normal),
			Font(file = regular, weight = FontWeight.SemiBold, style = FontStyle.Normal),
		)
	}
}
