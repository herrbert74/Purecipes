package app.purecipes.shared.domain.appliance

import app.purecipes.shared.domain.model.Appliance
import app.purecipes.shared.domain.model.CookingMethod
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ApplianceExtractorTest {

	@Test
	fun extractsOvenFromPreheat() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("Preheat oven to 180°C", "Bake for 20 minutes"),
		)
		appliances shouldContain Appliance.OVEN
	}

	@Test
	fun excludesOvenWhenOnlyDutchOvenOnStovetop() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("Heat olive oil in a Dutch oven over medium heat on the stove"),
		)
		appliances shouldNotContain Appliance.OVEN
		appliances shouldContain Appliance.STOVETOP
	}

	@Test
	fun extractsAirFryer() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("Set air fryer to 200°C and cook for 15 minutes"),
		)
		appliances shouldContain Appliance.AIR_FRYER
	}

	@Test
	fun extractsImmersionBlenderWithoutRegularBlender() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("Puree the soup using an immersion blender until creamy"),
		)
		appliances shouldContain Appliance.IMMERSION_BLENDER
		appliances shouldNotContain Appliance.BLENDER
	}

	@Test
	fun extractsBlender() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("Transfer sauce to a blender and blend until completely smooth"),
		)
		appliances shouldContain Appliance.BLENDER
	}

	@Test
	fun extractsFoodProcessor() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("Pulse the flour, butter and sugar in a food processor"),
		)
		appliances shouldContain Appliance.FOOD_PROCESSOR
	}

	@Test
	fun extractsStandMixer() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("In the bowl of a stand mixer fitted with the paddle attachment, cream butter"),
		)
		appliances shouldContain Appliance.STAND_MIXER
	}

	@Test
	fun extractsHandMixer() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("Beat egg whites with an electric hand mixer until stiff peaks form"),
		)
		appliances shouldContain Appliance.HAND_MIXER
	}

	@Test
	fun extractsSlowCooker() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("Place all ingredients in a slow cooker and cook on low for 8 hours"),
		)
		appliances shouldContain Appliance.SLOW_COOKER
	}

	@Test
	fun extractsPressureCooker() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("Lock lid of Instant Pot and pressure cook on high for 12 minutes"),
		)
		appliances shouldContain Appliance.PRESSURE_COOKER
	}

	@Test
	fun extractsFromCookingMethod() {
		val appliances = ApplianceExtractor.extractAppliances(
			cookingMethod = CookingMethod.BAKE,
		)
		appliances shouldContain Appliance.OVEN
	}

	@Test
	fun returnsEmptyWhenNoAppliancesDetected() {
		val appliances = ApplianceExtractor.extractAppliances(
			instructions = listOf("Toss greens with vinaigrette in a salad bowl and serve chilled"),
		)
		appliances shouldBe emptySet()
	}
}
