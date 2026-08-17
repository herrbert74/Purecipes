package app.purecipes.feature.library.ui.myrecipes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.newrecipe.domain.usecase.DeleteCreatedRecipeUseCase
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
class MyRecipesViewModel(
	private val getCreatedRecipes: GetCreatedRecipesUseCase,
	private val deleteCreatedRecipe: DeleteCreatedRecipeUseCase,
	private val trackEvent: TrackEventUseCase,
) : ViewModel() {

	var isLoading by mutableStateOf(true)
		private set
	var errorMessage by mutableStateOf<String?>(null)
		private set
	var recipes by mutableStateOf<List<RecipeSummary>>(emptyList())
		private set
	private var deleteInFlight = false

	init {
		loadRecipes()
	}

	fun retry() {
		loadRecipes()
	}

	fun reload() {
		loadRecipes()
	}

	fun deleteRecipe(recipe: RecipeSummary, onDone: (Boolean) -> Unit = {}) {
		if (deleteInFlight) {
			onDone(false)
			return
		}
		viewModelScope.launch {
			deleteInFlight = true
			errorMessage = null
			val outcome = deleteCreatedRecipe(recipe.id)
			val ok = outcome.getError() == null
			if (ok) {
				recipes = recipes.filterNot { it.id == recipe.id }
				trackEvent(
					AnalyticsEvent.RecipeDeleted(
						recipeId = recipe.id,
						recipeName = recipe.title,
						isPrivate = recipe.isPrivate,
					),
				)
			} else {
				errorMessage = outcome.getError()?.message
			}
			deleteInFlight = false
			onDone(ok)
		}
	}

	private fun loadRecipes() {
		viewModelScope.launch {
			if (recipes.isEmpty()) {
				isLoading = true
			}
			errorMessage = null
			val outcome = getCreatedRecipes()
			recipes = (outcome.get() ?: emptyList()).map(RecipeDetails::toRecipeSummary)
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
		isPrivate = isPrivate,
	)
