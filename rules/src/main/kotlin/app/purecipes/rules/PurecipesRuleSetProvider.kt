package app.purecipes.rules

import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

class PurecipesRuleSetProvider : RuleSetProvider {
	override val ruleSetId: RuleSetId = RuleSetId("purecipes")

	override fun instance(): RuleSet = RuleSet(
		ruleSetId,
		listOf(
			{ config -> NoSpaceIndentationRule(config) },
			{ config -> MaxLineLengthTabs(config) },
		),
	)
}
