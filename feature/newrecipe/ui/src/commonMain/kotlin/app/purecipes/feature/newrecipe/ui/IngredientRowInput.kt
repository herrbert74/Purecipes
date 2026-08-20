package app.purecipes.feature.newrecipe.ui

import androidx.compose.runtime.Immutable

@Immutable
data class IngredientPartInput(
	val amount: String = "",
	val unit: String = "",
	val name: String = "",
)

@Immutable
data class IngredientRowInput(
	val primary: IngredientPartInput = IngredientPartInput(),
	val isOptional: Boolean = false,
	val alternatives: List<IngredientPartInput> = emptyList(),
)

@Immutable
data class IngredientRowsState(
	val items: List<IngredientRowInput>,
)

@Immutable
data class SuggestedIngredientUnits(
	val items: List<String>,
)
