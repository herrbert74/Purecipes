package app.purecipes.feature.settings.ui.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.icon.AppIcons

@Composable
fun LicensesScreen(
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Open Source Licenses") },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = AppIcons.ArrowBack,
							contentDescription = "Back",
						)
					}
				},
			)
		},
	) { innerPadding ->
		LibrariesList(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding),
		)
	}
}
