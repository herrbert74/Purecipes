package com.purecipes.shared.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = CuisineSerializer::class)
enum class Cuisine(
	val displayName: String,
) {
	AMERICAN("American"),
	ARGENTINE("Argentine"),
	BANGLADESHI("Bangladeshi"),
	BRAZILIAN("Brazilian"),
	BRITISH("British"),
	CARIBBEAN("Caribbean"),
	CHINESE("Chinese"),
	EASTERN_EUROPEAN("Eastern European"),
	ETHIOPIAN("Ethiopian"),
	FILIPINO("Filipino"),
	FRENCH("French"),
	GERMAN("German"),
	GREEK("Greek"),
	INDIAN("Indian"),
	INDONESIAN("Indonesian"),
	ITALIAN("Italian"),
	JAPANESE("Japanese"),
	KOREAN("Korean"),
	MALAYSIAN("Malaysian"),
	MEDITERRANEAN("Mediterranean"),
	MEXICAN("Mexican"),
	MIDDLE_EASTERN("Middle Eastern"),
	NORTH_AFRICAN("North African"),
	PAKISTANI("Pakistani"),
	PERUVIAN("Peruvian"),
	PORTUGUESE("Portuguese"),
	RUSSIAN("Russian"),
	SPANISH("Spanish"),
	THAI("Thai"),
	TURKISH("Turkish"),
	VIETNAMESE("Vietnamese"),
	WEST_AFRICAN("West African");

	override fun toString(): String = displayName

	companion object {
		fun fromRawValue(value: String?): Cuisine? {
			if (value == null) return null
			val normalizedValue = value.trim().normalizeCuisineToken()
			if (normalizedValue.isEmpty()) return null

			return entries.firstOrNull { cuisine ->
				cuisine.name.normalizeCuisineToken() == normalizedValue ||
					cuisine.displayName.normalizeCuisineToken() == normalizedValue
			}
		}
	}
}

object CuisineSerializer : KSerializer<Cuisine> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Cuisine", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: Cuisine) {
		encoder.encodeString(value.displayName)
	}

	override fun deserialize(decoder: Decoder): Cuisine {
		return Cuisine.fromRawValue(decoder.decodeString())
			?: throw SerializationException("Unknown cuisine value")
	}
}

private fun String.normalizeCuisineToken(): String {
	return lowercase().filter(Char::isLetterOrDigit)
}
