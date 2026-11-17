package app.purecipes.shared.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalReducedMotion = staticCompositionLocalOf { false }

@Composable
expect fun rememberReducedMotion(): Boolean
