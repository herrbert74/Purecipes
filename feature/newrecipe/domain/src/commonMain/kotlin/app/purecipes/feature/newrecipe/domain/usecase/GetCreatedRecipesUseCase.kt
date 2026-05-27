package app.purecipes.feature.newrecipe.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import app.purecipes.shared.domain.model.RecipeDetails
import dev.zacsweers.metro.Inject

@Inject
class GetCreatedRecipesUseCase(
	private val repository: CreatedRecipeRepository,
) {

	suspend operator fun invoke(): Outcome<List<RecipeDetails>> {
		return repository.getCreatedRecipes()
	}
}
