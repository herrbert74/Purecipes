package app.purecipes.feature.recipedetails.ui

import androidx.compose.runtime.Immutable
import app.purecipes.shared.domain.model.CookbookSummary

@Immutable
internal data class RecipeSheetCookbooksList(val items: List<CookbookSummary>)
