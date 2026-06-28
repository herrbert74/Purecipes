package app.purecipes.feature.settings.ui.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import purecipes.feature.settings.ui.generated.resources.Res

@Composable
actual fun LibrariesList(modifier: Modifier) {
	val libraries by produceLibraries {
		Res.readBytes("files/aboutlibraries.json").decodeToString()
	}
	LibrariesContainer(
		libraries = libraries,
		modifier = modifier,
	)
}
