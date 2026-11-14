package app.purecipes.feature.search.ui

import androidx.compose.runtime.Composable
import app.purecipes.shared.ui.component.molecule.CenteredMessageContent

@Composable
fun SearchListDetailPlaceholder() {
	CenteredMessageContent(message = "Select a recipe to view details")
}
