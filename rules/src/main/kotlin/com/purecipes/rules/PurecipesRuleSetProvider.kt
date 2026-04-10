package com.purecipes.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class PurecipesRuleSetProvider : RuleSetProvider {
	override val ruleSetId: String = "purecipes"

	override fun instance(config: Config): RuleSet {
		return RuleSet(
			ruleSetId,
			listOf(
				NoSpaceIndentationRule(config),
				MaxLineLengthTabs(config),
			)
		)
	}
}
