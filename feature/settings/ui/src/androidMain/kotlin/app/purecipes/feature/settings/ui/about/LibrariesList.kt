package app.purecipes.feature.settings.ui.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries

private const val ABOUT_LIBRARIES_RAW_RESOURCE = "aboutlibraries"

@Composable
actual fun LibrariesList(modifier: Modifier) {
	val context = LocalContext.current
	val libraries by produceLibraries {
		val resourceId = context.resources.getIdentifier(
			ABOUT_LIBRARIES_RAW_RESOURCE,
			"raw",
			context.packageName,
		)
		check(resourceId != 0) {
			"Missing raw resource $ABOUT_LIBRARIES_RAW_RESOURCE"
		}
		context.resources.openRawResource(resourceId).bufferedReader().readText()
	}
	LibrariesContainer(
		libraries = libraries,
		modifier = modifier,
	)
}
