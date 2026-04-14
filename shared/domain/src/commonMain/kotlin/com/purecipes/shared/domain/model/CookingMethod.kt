package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CookingMethod(val displayName: String) {
	BAKE("Bake"),
	GRILL("Grill"),
	FRY("Fry"),
	STIR_FRY("Stir-Fry"),
	SLOW_COOK("Slow Cook"),
	STEAM("Steam"),
	BOIL("Boil"),
	ROAST("Roast"),
	PRESSURE_COOK("Pressure Cook"),
	AIR_FRY("Air Fry"),
	SMOKE("Smoke"),
	MICROWAVE("Microwave"),
	RAW("Raw / No Cook"),
}
