package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Appliance(val displayName: String) {

	AIR_FRYER("Air Fryer"),
	BLENDER("Blender"),
	DEEP_FRYER("Deep Fryer"),
	FOOD_PROCESSOR("Food Processor"),
	GRILL("Grill"),
	HAND_MIXER("Hand Mixer"),
	IMMERSION_BLENDER("Immersion Blender"),
	JUICER("Juicer"),
	KETTLE("Kettle"),
	MICROWAVE("Microwave"),
	OVEN("Oven"),
	PASTA_MAKER("Pasta Maker"),
	PRESSURE_COOKER("Pressure Cooker"),
	SANDWICH_MAKER("Sandwich Maker"),
	SLOW_COOKER("Slow Cooker"),
	SPICE_GRINDER("Spice Grinder"),
	STAND_MIXER("Stand Mixer"),
	STEAMER("Steamer"),
	STOVETOP("Stovetop"),
	WAFFLE_MAKER("Waffle Maker"),
}
