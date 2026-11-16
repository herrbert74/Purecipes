package app.purecipes.shared.ui.component.atom

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics

fun Modifier.headingSemantics(): Modifier = this.semantics { heading() }
