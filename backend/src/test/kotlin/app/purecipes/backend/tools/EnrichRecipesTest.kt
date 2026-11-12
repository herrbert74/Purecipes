package app.purecipes.backend.tools

import app.purecipes.shared.domain.model.CalorieRange
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import kotlin.test.Test

class EnrichRecipesTest {

	@Test
	fun calorieRangeFromNutritionMapsThresholdsCorrectly() {
		calorieRangeFromNutrition(null) shouldBe null
		calorieRangeFromNutrition(299.9) shouldBe CalorieRange.LOW
		calorieRangeFromNutrition(300.0) shouldBe CalorieRange.MEDIUM
		calorieRangeFromNutrition(600.0) shouldBe CalorieRange.MEDIUM
		calorieRangeFromNutrition(600.1) shouldBe CalorieRange.HIGH
	}

	@Test
	fun resolveUseModelPathAcceptsDirectAndNestedSavedModelDirectories() {
		val directDir = Files.createTempDirectory("use_model_direct")
		Files.writeString(directDir.resolve("saved_model.pb"), "stub")

		resolveUseModelPath(directDir.toString()) shouldBe directDir.toAbsolutePath().normalize().toString()

		val parentDir = Files.createTempDirectory("use_model_parent")
		val nestedDir = Files.createDirectories(parentDir.resolve("universal-sentence-encoder_4"))
		Files.writeString(nestedDir.resolve("saved_model.pb"), "stub")

		resolveUseModelPath(parentDir.toString()) shouldBe nestedDir.toAbsolutePath().normalize().toString()
	}
}
