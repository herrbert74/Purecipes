package app.purecipes.feature.favorites.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeSummary
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class CreatedRecipesViewModel(
	private val getCreatedRecipes: GetCreatedRecipesUseCase,
) : ViewModel() {

	var isLoading by mutableStateOf(true)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	val recipes = mutableStateListOf<RecipeSummary>()

	init {
		loadRecipes()
	}

	fun retry() {
		loadRecipes()
	}

	fun reload() {
		loadRecipes()
	}

	private fun loadRecipes() {
		viewModelScope.launch {
			isLoading = true
			errorMessage = null
			val outcome = getCreatedRecipes()
			recipes.clear()
			recipes.addAll(
				(outcome.get() ?: emptyList()).map(RecipeDetails::toRecipeSummary),
			)
			errorMessage = outcome.getError()?.message
			isLoading = false
		}
	}
}

internal fun RecipeDetails.toRecipeSummary(): RecipeSummary =
	RecipeSummary(
		id = id,
		title = title,
		cuisine = cuisine,
		imageUrl = imageUrl,
		totalTime = totalTime,
	)
