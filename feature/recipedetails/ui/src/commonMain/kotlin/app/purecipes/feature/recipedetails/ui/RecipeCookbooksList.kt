package app.purecipes.feature.recipedetails.ui

import androidx.compose.runtime.Immutable
import app.purecipes.shared.domain.model.CookbookRef

@Immutable
internal data class RecipeCookbooksList(val items: List<CookbookRef>)
