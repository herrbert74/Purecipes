package app.purecipes.backend.feature.recipe

import app.purecipes.shared.domain.model.Appliance
import app.purecipes.shared.domain.model.MeasurementSystem
import java.sql.ResultSet

internal fun ResultSet.toRecipeRecord(): RecipeRecord {
	return RecipeRecord(
		id = getInt("id"),
		title = getString("title"),
		description = getNullableString("description"),
		instructions = getNullableString("instructions"),
		totalTime = getObject("total_time") as? Int,
		yields = getNullableString("yields"),
		imageUrl = getNullableString("image_url"),
		cuisine = getNullableString("cuisine"),
		mealType = getNullableString("meal_type"),
		measurementSystem = getNullableMeasurementSystem("measurement_system"),
		difficulty = getNullableString("difficulty"),
		cookingMethod = getNullableString("cooking_method"),
		calorieRange = getNullableString("calorie_range"),
		dietaryPreferences = getStringArray("dietary_preferences"),
		tags = getStringArray("tags"),
		appliances = getStringArray("appliances"),
		isPrivate = getBoolean("is_private"),
		createdByUserId = getNullableLong("created_by_user_id"),
	)
}

internal fun ResultSet.getNullableString(columnLabel: String): String? =
	getString(columnLabel)?.trim()?.takeIf { it.isNotEmpty() }

internal fun ResultSet.getNullableLong(columnLabel: String): Long? {
	val value = getObject(columnLabel) ?: return null
	return when (value) {
		is Long -> value
		is Number -> value.toLong()
		else -> null
	}
}

internal fun ResultSet.getNullableMeasurementSystem(columnLabel: String): MeasurementSystem? =
	getNullableString(columnLabel)?.let(MeasurementSystem::valueOf)

internal fun ResultSet.getStringArray(columnLabel: String): List<String> =
	(getArray(columnLabel)?.array as? Array<*>)
		?.filterIsInstance<String>()
		?: emptyList()

internal fun ResultSet.hasColumn(columnLabel: String): Boolean {
	val count = metaData.columnCount
	for (i in 1..count) {
		if (metaData.getColumnLabel(i).equals(columnLabel, ignoreCase = true)) {
			return true
		}
	}
	return false
}

internal fun ResultSet.readAppliances(): Set<Appliance> {
	if (!hasColumn("appliances")) return emptySet()
	return getStringArray("appliances").mapNotNull { raw ->
		Appliance.entries.firstOrNull { it.name.equals(raw, ignoreCase = true) }
	}.toSet()
}
