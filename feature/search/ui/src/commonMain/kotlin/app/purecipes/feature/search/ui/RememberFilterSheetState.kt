package app.purecipes.feature.search.ui

import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable

/**
 * Android Material3 1.5 deprecated `rememberModalBottomSheetState` in favour of
 * `rememberBottomSheetState`. JetBrains Compose Material3 1.11.0-alpha07 does not ship that
 * API yet, so this stays expect/actual.
 *
 * After a `jetbrains-composeMaterial3` bump, try calling `rememberBottomSheetState` from
 * commonMain. If `:feature:search:ui:compileKotlinJvm` succeeds, delete this expect/actual
 * and use `rememberBottomSheetState` in [RecipeSearchScreen] instead.
 */
@Composable
internal expect fun rememberFilterSheetState(): SheetState
