package app.purecipes.shared.domain.model

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import kotlin.test.Test

class IngredientCatalogueTest {

	@Test
	fun groupsAreNonEmptyAndAllItemsMatchGroups() {
		IngredientCatalogue.groups.shouldNotBeEmpty()
		IngredientCatalogue.groups.forEach { group ->
			group.name.shouldNotBeBlank()
			group.items.shouldNotBeEmpty()
		}
		IngredientCatalogue.allItems shouldBe IngredientCatalogue.groups.flatMap { it.items }.toSet()
	}

	@Test
	fun includesCommonPantryIngredients() {
		listOf("Flour", "Eggs", "Olive Oil", "Onion", "Garlic", "Butter", "Milk").forEach { name ->
			IngredientCatalogue.allItems shouldContain name
		}
	}

	@Test
	fun aliasSiblingsByItemCoversCatalogueAndLinksAliases() {
		IngredientCatalogue.aliasSiblingsByItem.keys shouldBe IngredientCatalogue.allItems
		IngredientCatalogue.aliasSiblingsByItem.getValue("Shrimp") shouldContain "Prawns"
		IngredientCatalogue.aliasSiblingsByItem.getValue("Prawns") shouldContain "Shrimp"
	}
}
