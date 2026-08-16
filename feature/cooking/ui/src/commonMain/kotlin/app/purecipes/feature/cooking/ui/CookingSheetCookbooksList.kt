package app.purecipes.feature.cooking.ui

import androidx.compose.runtime.Immutable
import app.purecipes.shared.domain.model.CookbookSummary

@Immutable
internal data class CookingSheetCookbooksList(val items: List<CookbookSummary>)
