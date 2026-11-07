package app.purecipes.backend.feature.search

import isSharedSectionHeading
import isSharedSectionHeadingOrNote

internal object IngredientHeadingRules {

	fun isSectionHeading(line: String): Boolean = isSharedSectionHeading(line)

	fun isSectionHeadingOrNote(line: String): Boolean = isSharedSectionHeadingOrNote(line)
}
