package app.purecipes.shared.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

@Serializable(with = RecipeIngredientSerializer::class)
data class RecipeIngredient(
	val text: String,
	val requirement: IngredientRequirement = IngredientRequirement.REQUIRED,
)

object RecipeIngredientSerializer : KSerializer<RecipeIngredient> {

	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RecipeIngredient") {
		element<String>("text")
		element<String>("requirement")
	}

	override fun deserialize(decoder: Decoder): RecipeIngredient {
		val jsonDecoder = decoder as? JsonDecoder
			?: error("RecipeIngredient can only be deserialized by JSON")
		return when (val element = jsonDecoder.decodeJsonElement()) {
			is JsonPrimitive -> RecipeIngredient(text = element.content)
			is JsonObject -> {
				val text = element["text"]?.jsonPrimitive?.content.orEmpty()
				val requirement = element["requirement"]?.jsonPrimitive?.content
					?.let { value -> runCatching { IngredientRequirement.valueOf(value) }.getOrNull() }
					?: IngredientRequirement.REQUIRED
				RecipeIngredient(text = text, requirement = requirement)
			}
			else -> error("Unsupported ingredient JSON element")
		}
	}

	override fun serialize(encoder: Encoder, value: RecipeIngredient) {
		val jsonEncoder = encoder as? JsonEncoder
			?: error("RecipeIngredient can only be serialized to JSON")
		jsonEncoder.encodeJsonElement(
			buildJsonObject {
				put("text", value.text)
				put("requirement", value.requirement.name)
			},
		)
	}
}
