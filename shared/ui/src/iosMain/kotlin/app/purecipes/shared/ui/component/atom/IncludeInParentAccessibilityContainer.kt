package app.purecipes.shared.ui.component.atom

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics

actual fun Modifier.includeInParentAccessibilityContainer(): Modifier =
	Modifier.semantics { isTraversalGroup = false }.then(this)
