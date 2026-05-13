package app.purecipes.feature.search.ui.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet

private enum class IngredientChipState { NEUTRAL, SELECTED }

private data class IngredientChipGroup(
	val name: String,
	val items: List<String>,
)

private val INGREDIENT_GROUPS = listOf(
	IngredientChipGroup(
		name = "Proteins",
		items = listOf(
			"Chicken",
			"Beef",
			"Pork",
			"Lamb",
			"Fish",
			"Shrimp",
			"Eggs",
			"Tofu",
			"Feta",
			"Halloumi",
			"Sausage",
			"Salmon",
			"Bacon",
			"Chorizo",
			"Pancetta",
			"Duck",
			"Tuna",
			"Ham",
			"Crab",
			"Prawns",
			"Turkey",
			"Anchovy",
			"Tilapia",
			"Cod",
			"Haddock",
			"Mackerel",
			"Sardines",
			"Mussels",
			"Clams",
			"Scallops",
			"Turkey Mince",
			"Beef Mince",
			"Chicken Thighs",
			"Steak",
		),
	),
	IngredientChipGroup(
		name = "Vegetables",
		items = listOf(
			"Onion",
			"Garlic",
			"Tomato",
			"Potato",
			"Carrot",
			"Spinach",
			"Pepper",
			"Mushroom",
			"Broccoli",
			"Zucchini",
			"Sweetcorn",
			"Peas",
			"Cucumber",
			"Kale",
			"Cabbage",
			"Beansprouts",
			"Pumpkin",
			"Leek",
			"Shallot",
			"Celery",
			"Green Onion",
			"Mint",
			"Cauliflower",
			"Brussels Sprout",
			"Cherry Tomatoes",
			"Butternut Squash",
			"Pak Choi",
			"Aubergine",
			"Sweet Potato",
			"Asparagus",
			"Green Beans",
			"Artichoke",
			"Rocket",
			"Lettuce",
			"Romaine Lettuce",
			"Radicchio",
			"Endive",
			"Celeriac",
			"Fennel",
			"Squash",
			"Parsnip",
			"Turnip",
			"Beetroot",
			"Radish",
			"Jalapeno",
			"Tomatillo",
			"Tomatillos",
			"Anaheim Pepper",
			"Red Onion",
			"Yellow Onion",
			"White Onion",
			"Plantain",
			"Okra",
			"Collard Greens",
			"Swiss Chard",
			"Spring Greens",
		),
	),
	IngredientChipGroup(
		name = "Fruits",
		items = listOf(
			"Lemon",
			"Lime",
			"Apple",
			"Banana",
			"Avocado",
			"Orange",
			"Raisin",
			"Mango",
			"Raspberry",
			"Pineapple",
			"Pomegranate",
			"Blueberry",
			"Strawberry",
			"Blackberry",
			"Cherry",
			"Peach",
			"Pear",
			"Apricot",
			"Fig",
			"Date",
			"Grapes",
			"Cranberries",
			"Sultanas",
		),
	),
	IngredientChipGroup(
		name = "Dairy",
		items = listOf(
			"Milk",
			"Butter",
			"Cheese",
			"Cream",
			"Yoghurt",
			"Parmesan",
			"Mozzarella",
			"Crème Fraîche",
			"Mascarpone",
			"Ricotta",
			"Sour Cream",
			"Cream Cheese",
			"Pecorino Romano",
			"Paneer",
			"Cheddar",
			"Gruyere",
			"Goat Cheese",
			"Buttermilk",
			"Greek Yoghurt",
			"Double Cream",
			"Ghee",
			"Cottage Cheese",
			"Monterey Jack",
			"Sharp Cheddar",
		),
	),
	IngredientChipGroup(
		name = "Grains & Starches",
		items = listOf(
			"Rice",
			"Pasta",
			"Bread",
			"Flour",
			"Oats",
			"Noodles",
			"Lentils",
			"Chickpeas",
			"Beans",
			"Cornflour",
			"Tortilla",
			"Spaghetti",
			"Puff Pastry",
			"Panko",
			"Vegetable Stock",
			"Linguine",
			"Orzo",
			"Quinoa",
			"Couscous",
			"Ciabatta",
			"Fettuccine",
			"Tagliatelle",
			"Breadcrumbs",
			"Cornmeal",
			"Rigatoni",
			"Penne",
			"Fusilli",
			"Lasagna Sheets",
			"Udon",
			"Ramen",
			"Polenta",
			"Semolina",
			"Hash Browns",
			"Hot Dog Buns",
			"Brioche Buns",
		),
	),
	IngredientChipGroup(
		name = "Herbs & Spices",
		items = listOf(
			"Basil",
			"Parsley",
			"Cilantro",
			"Cumin",
			"Paprika",
			"Oregano",
			"Thyme",
			"Chili",
			"Star Anise",
			"Ginger",
			"Cinnamon",
			"Dill",
			"Chive",
			"Bay Leaf",
			"Turmeric",
			"Garam Masala",
			"Nigella Seed",
			"Fennel Seed",
			"Rosemary",
			"Sage",
			"Nutmeg",
			"Chinese Five-Spice",
			"Curry Powder",
			"Cardamom",
			"Allspice",
			"Lemongrass",
			"Za'atar",
			"Sichuan Peppercorns",
			"Cloves",
			"Sumac",
			"Tarragon",
			"Black Pepper",
			"White Pepper",
			"Chilli Flakes",
			"Old Bay Seasoning",
			"Cajun Seasoning",
			"Shawarma Spice",
			"Tagine Spice",
			"Clove",
			"Peppermint Extract",
			"Porcini",
		),
	),
	IngredientChipGroup(
		name = "Oils & Condiments",
		items = listOf(
			"Olive Oil",
			"Soy Sauce",
			"Vinegar",
			"Tomato Paste",
			"Coconut Milk",
			"Capers",
			"Mustard",
			"Sriracha",
			"Miso Paste",
			"Tahini",
			"Hoisin Sauce",
			"Mayonnaise",
			"Worcestershire Sauce",
			"White Wine",
			"Red Wine",
			"Maple Syrup",
			"Ketchup",
			"Hot Sauce",
			"Harissa",
			"Groundnut Oil",
			"Mirin",
			"Passata",
			"Oyster Sauce",
			"Coconut Oil",
			"Golden Syrup",
			"Cranberry Sauce",
			"Mango Chutney",
			"Peri Peri Seasoning",
			"Shaoxing Wine",
			"Marsala Wine",
			"Canola Oil",
			"Grapeseed Oil",
			"Sunflower Oil",
			"Sesame Oil",
			"BBQ Sauce",
			"Teriyaki Sauce",
			"Salsa Verde",
			"Salsa",
			"Guacamole",
			"Tzatziki",
			"Marinara Sauce",
			"Pico de Gallo",
			"Apple Cider Vinegar",
			"Red Wine Vinegar",
			"White Wine Vinegar",
			"Olive Brine",
			"Rum",
			"Brandy",
			"Vodka",
		),
	),
	IngredientChipGroup(
		name = "Nuts",
		items = listOf(
			"Almonds",
			"Peanuts",
			"Sesame",
			"Cashews",
			"Pine Nuts",
			"Walnuts",
			"Pecans",
			"Pistachios",
			"Hazelnuts",
			"Macadamia Nuts",
			"Brazil Nuts",
			"Chestnuts",
		),
	),
	IngredientChipGroup(
		name = "Baking",
		items = listOf("Sugar", "Honey", "Chocolate", "Vanilla", "Baking Powder", "Cocoa Powder", "Sourdough Starter"),
	),
)

@Composable
internal fun IngredientFilterSection(
	availableIngredients: ImmutableSet<String>,
	onSelectionChange: (available: Set<String>) -> Unit,
	modifier: Modifier = Modifier,
) {
	val allItems = INGREDIENT_GROUPS.flatMap { it.items }.toSet()
	var collapsed by rememberSaveable { mutableStateOf(true) }

	Column(modifier = modifier) {
		FilterSectionHeader(
			title = "Pantry",
			onSelectAll = { onSelectionChange(allItems) },
			onClearAll = { onSelectionChange(emptySet()) },
			isCollapsed = collapsed,
			onToggleCollapse = { collapsed = !collapsed },
		)
		AnimatedVisibility(
			visible = !collapsed,
			enter = expandVertically(),
			exit = shrinkVertically(),
		) {
			Column {
				INGREDIENT_GROUPS.forEach { group ->
					IngredientGroupChips(
						group = group,
						availableIngredients = availableIngredients,
						onSelectionChange = onSelectionChange,
					)
				}
			}
		}
	}
}

@Composable
private fun IngredientGroupChips(
	group: IngredientChipGroup,
	availableIngredients: ImmutableSet<String>,
	onSelectionChange: (available: Set<String>) -> Unit,
) {
	var collapsed by rememberSaveable { mutableStateOf(true) }
	Column {
		FilterSectionHeader(
			title = group.name,
			onSelectAll = {
				onSelectionChange(availableIngredients + group.items)
			},
			onClearAll = {
				onSelectionChange(availableIngredients - group.items.toSet())
			},
			modifier = Modifier.padding(start = PurecipesTheme.space.m),
			isCollapsed = collapsed,
			onToggleCollapse = { collapsed = !collapsed },
		)
		AnimatedVisibility(
			visible = !collapsed,
			enter = expandVertically(),
			exit = shrinkVertically(),
		) {
			FlowRow(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = PurecipesTheme.space.xl),
				horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			) {
				group.items.forEach { item ->
					val state = when (item) {
						in availableIngredients -> IngredientChipState.SELECTED
						else -> IngredientChipState.NEUTRAL
					}
					IngredientTriStateChip(
						item = item,
						state = state,
						onToggle = {
							val newAvailable = when (state) {
								IngredientChipState.NEUTRAL -> availableIngredients + item
								IngredientChipState.SELECTED -> availableIngredients - item
							}
							onSelectionChange(newAvailable)
						},
					)
				}
			}
		}
	}
}

@Composable
private fun IngredientTriStateChip(
	item: String,
	state: IngredientChipState,
	onToggle: () -> Unit,
) {
	FilterChip(
		selected = state != IngredientChipState.NEUTRAL,
		onClick = onToggle,
		label = { Text(item) },
	)
}
