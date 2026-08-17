package app.purecipes.backend.feature.recipe

import java.sql.PreparedStatement

internal fun addRecipeVisibilityCondition(
	conditions: MutableList<String>,
	params: MutableList<Any>,
	userId: Long?,
	tableAlias: String? = "r",
) {
	val prefix = tableAlias?.let { "$it." }.orEmpty()
	if (userId == null) {
		conditions.add("${prefix}is_private = FALSE")
	} else {
		conditions.add("(${prefix}is_private = FALSE OR ${prefix}created_by_user_id = ?)")
		params.add(userId)
	}
}

internal fun bindSearchParams(ps: PreparedStatement, params: List<Any>) {
	params.forEachIndexed { index, param ->
		when (param) {
			is String -> ps.setString(index + 1, param)
			is Int -> ps.setInt(index + 1, param)
			is Long -> ps.setLong(index + 1, param)
			else -> error("Unsupported parameter type: ${param::class}")
		}
	}
}
