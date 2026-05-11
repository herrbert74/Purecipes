package app.purecipes.feature.newrecipe.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.newrecipe.data.datasource.CreatedRecipeRemoteDataSource
import app.purecipes.feature.newrecipe.data.image.RecipeImagePathLoader
import app.purecipes.feature.newrecipe.data.image.RecipeImageUpload
import app.purecipes.feature.newrecipe.data.image.RecipeImageUploader
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.Cuisine
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CreatedRecipeAccessorTest {

	@Test
	fun `saving a recipe uploads it`() = runTest {
		val api = FakePurecipesApi()
		val accessor = CreatedRecipeAccessor(
			remoteDataSource = CreatedRecipeRemoteDataSource(
				api = api,
				imagePathLoader = FakeRecipeImagePathLoader(),
				imageUploader = FakeRecipeImageUploader(),
			),
		)

		val savedRecipe = accessor.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				title = "Tomato Pasta",
				description = "Quick weeknight dinner.",
				ingredients = listOf("200 g pasta", "2 tomatoes"),
				steps = listOf("Boil the pasta", "Finish with the tomatoes"),
				totalTime = 20,
				yields = "2 servings",
				cuisine = Cuisine.ITALIAN,
			),
		).get().shouldNotBeNull()

		val storedRecipes = accessor.getCreatedRecipes().get()

		savedRecipe.id shouldBe 1
		storedRecipes shouldBe listOf(savedRecipe)
	}

	@Test
	fun `saving with an existing id updates the uploaded recipe`() = runTest {
		val api = FakePurecipesApi()
		val accessor = CreatedRecipeAccessor(
			remoteDataSource = CreatedRecipeRemoteDataSource(
				api = api,
				imagePathLoader = FakeRecipeImagePathLoader(),
				imageUploader = FakeRecipeImageUploader(),
			),
		)
		val firstSave = accessor.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				title = "Tomato Pasta",
				description = "Quick weeknight dinner.",
				steps = listOf("Boil the pasta"),
			),
		).get()

		val updatedRecipe = accessor.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				recipeId = firstSave?.id,
				title = "Creamy Tomato Pasta",
				description = "Updated version.",
				steps = listOf("Boil the pasta", "Stir in cream"),
			),
		).get()

		val storedRecipes = accessor.getCreatedRecipes().get().orEmpty()

		updatedRecipe?.id shouldBe firstSave?.id
		storedRecipes.size shouldBe 1
		storedRecipes.single().title shouldBe "Creamy Tomato Pasta"
	}

	@Test
	fun `saving with local image path uploads image before recipe`() = runTest {
		val api = FakePurecipesApi()
		val imagePathLoader = FakeRecipeImagePathLoader()
		val imageUploader = FakeRecipeImageUploader()
		val accessor = CreatedRecipeAccessor(
			remoteDataSource = CreatedRecipeRemoteDataSource(
				api = api,
				imagePathLoader = imagePathLoader,
				imageUploader = imageUploader,
			),
		)

		val savedRecipe = accessor.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				title = "Tomato Pasta",
				description = "Quick weeknight dinner.",
				imageUrl = "/tmp/tomato.jpg",
				steps = listOf("Boil the pasta"),
			),
		).get()

		imagePathLoader.loadedPath shouldBe "/tmp/tomato.jpg"
		savedRecipe?.imageUrl shouldBe "https://cdn.purecipes.test/recipe-image.jpg"
	}

	@Test
	fun `saving fails when local image path cannot be read`() = runTest {
		val api = FakePurecipesApi()
		val accessor = CreatedRecipeAccessor(
			remoteDataSource = CreatedRecipeRemoteDataSource(
				api = api,
				imagePathLoader = FakeRecipeImagePathLoader(
					result = Err(Failure.ServerError("Could not read the image file from the provided local path.")),
				),
				imageUploader = FakeRecipeImageUploader(),
			),
		)

		val outcome = accessor.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				title = "Tomato Pasta",
				description = "Quick weeknight dinner.",
				imageUrl = "/tmp/missing.jpg",
				steps = listOf("Boil the pasta"),
			),
		)

		outcome.getError()?.message shouldBe "Could not read the image file from the provided local path."
	}

	private class FakeRecipeImagePathLoader(
		private val result: Outcome<RecipeImageUpload> = Ok(
			RecipeImageUpload(
				bytes = byteArrayOf(1, 2, 3),
				fileName = "recipe-image.jpg",
				contentType = "image/jpeg",
			),
		),
	) : RecipeImagePathLoader {

		var loadedPath: String? = null
			private set

		override suspend fun load(path: String): Outcome<RecipeImageUpload> {
			loadedPath = path
			return result
		}
	}

	private class FakeRecipeImageUploader(
		private val uploadedUrl: String = "https://cdn.purecipes.test/recipe-image.jpg",
	) : RecipeImageUploader {

		override suspend fun upload(image: RecipeImageUpload) = Ok(uploadedUrl)
	}
}
