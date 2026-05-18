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
		name = "Poultry & Eggs",
		items = listOf(
			"Chicken",
			"Chicken Thighs",
			"Duck",
			"Eggs",
			"Turkey",
			"Turkey Mince",
		),
	),
	IngredientChipGroup(
		name = "Meats",
		items = listOf(
			"Bacon",
			"Beef",
			"Beef Mince",
			"Chorizo",
			"Ham",
			"Lamb",
			"Pancetta",
			"Pork",
			"Sausage",
			"Steak",
		),
	),
	IngredientChipGroup(
		name = "Fish",
		items = listOf(
			"Anchovy",
			"Cod",
			"Fish",
			"Haddock",
			"Mackerel",
			"Salmon",
			"Sardines",
			"Tilapia",
			"Tuna",
		),
	),
	IngredientChipGroup(
		name = "Seafood",
		items = listOf(
			"Clams",
			"Crab",
			"Mussels",
			"Prawns",
			"Scallops",
			"Shrimp",
		),
	),
	IngredientChipGroup(
		name = "Dairy-Free and Meat Substitutes",
		items = listOf(
			"Jackfruit",
			"Plant-Based Mince",
			"Seitan",
			"Tempeh",
			"Tofu",
		),
	),
	IngredientChipGroup(
		name = "Vegetables",
		items = listOf(
			"Anaheim Pepper",
			"Artichoke",
			"Asparagus",
			"Aubergine",
			"Beansprouts",
			"Beetroot",
			"Broccoli",
			"Brussels Sprout",
			"Butternut Squash",
			"Cabbage",
			"Carrot",
			"Cauliflower",
			"Celeriac",
			"Celery",
			"Cherry Tomatoes",
			"Collard Greens",
			"Cucumber",
			"Endive",
			"Fennel",
			"Garlic",
			"Green Onion",
			"Jalapeno",
			"Kale",
			"Leek",
			"Lettuce",
			"Okra",
			"Onion",
			"Pak Choi",
			"Parsnip",
			"Pepper",
			"Potato",
			"Pumpkin",
			"Radicchio",
			"Radish",
			"Red Onion",
			"Rocket",
			"Romaine Lettuce",
			"Shallot",
			"Spinach",
			"Spring Greens",
			"Squash",
			"Sweet Potato",
			"Sweetcorn",
			"Swiss Chard",
			"Tomatillo",
			"Tomatillos",
			"Tomato",
			"Turnip",
			"White Onion",
			"Yellow Onion",
			"Zucchini",
		),
	),
	IngredientChipGroup(
		name = "Mushrooms",
		items = listOf(
			"Mushroom",
			"Oyster Mushroom",
			"Porcini",
			"Portobello",
			"Shiitake",
		),
	),
	IngredientChipGroup(
		name = "Legumes",
		items = listOf(
			"Beans",
			"Chickpeas",
			"Green Beans",
			"Lentils",
			"Peas",
		),
	),
	IngredientChipGroup(
		name = "Fruits",
		items = listOf(
			"Apple",
			"Apricot",
			"Avocado",
			"Banana",
			"Date",
			"Fig",
			"Grapes",
			"Lemon",
			"Lime",
			"Mango",
			"Orange",
			"Peach",
			"Pear",
			"Pineapple",
			"Plantain",
			"Pomegranate",
			"Raisin",
			"Sultanas",
		),
	),
	IngredientChipGroup(
		name = "Berries",
		items = listOf(
			"Blackberry",
			"Blueberry",
			"Cherry",
			"Cranberries",
			"Raspberry",
			"Strawberry",
		),
	),
	IngredientChipGroup(
		name = "Dairy",
		items = listOf(
			"Butter",
			"Buttermilk",
			"Cottage Cheese",
			"Cream",
			"Cream Cheese",
			"Crème Fraîche",
			"Double Cream",
			"Ghee",
			"Greek Yoghurt",
			"Mascarpone",
			"Milk",
			"Ricotta",
			"Sour Cream",
			"Yoghurt",
		),
	),
	IngredientChipGroup(
		name = "Cheeses",
		items = listOf(
			"Cheddar",
			"Cheese",
			"Feta",
			"Goat Cheese",
			"Gruyere",
			"Halloumi",
			"Monterey Jack",
			"Mozzarella",
			"Paneer",
			"Parmesan",
			"Pecorino Romano",
			"Sharp Cheddar",
		),
	),
	IngredientChipGroup(
		name = "Grains & Cereals",
		items = listOf(
			"Cornmeal",
			"Couscous",
			"Oats",
			"Polenta",
			"Quinoa",
			"Rice",
			"Semolina",
		),
	),
	IngredientChipGroup(
		name = "Pasta",
		items = listOf(
			"Fettuccine",
			"Fusilli",
			"Lasagna Sheets",
			"Linguine",
			"Noodles",
			"Orzo",
			"Pasta",
			"Penne",
			"Ramen",
			"Rigatoni",
			"Spaghetti",
			"Tagliatelle",
			"Udon",
		),
	),
	IngredientChipGroup(
		name = "Bread & Salty Snacks",
		items = listOf(
			"Bread",
			"Breadcrumbs",
			"Brioche Buns",
			"Ciabatta",
			"Hash Browns",
			"Hot Dog Buns",
			"Panko",
		),
	),
	IngredientChipGroup(
		name = "Baking",
		items = listOf(
			"Baking Powder",
			"Cornflour",
			"Flour",
			"Sourdough Starter",
			"Vanilla",
		),
	),
	IngredientChipGroup(
		name = "Pre-Made Doughs & Wrappers",
		items = listOf(
			"Filo Pastry",
			"Pizza Dough",
			"Puff Pastry",
			"Spring Roll Wrappers",
			"Tortilla",
		),
	),
	IngredientChipGroup(
		name = "Herbs & Spices",
		items = listOf(
			"Allspice",
			"Basil",
			"Bay Leaf",
			"Black Pepper",
			"Cajun Seasoning",
			"Cardamom",
			"Chili",
			"Chilli Flakes",
			"Chinese Five-Spice",
			"Chive",
			"Cilantro",
			"Cinnamon",
			"Clove",
			"Cloves",
			"Cumin",
			"Curry Powder",
			"Dill",
			"Fennel Seed",
			"Garam Masala",
			"Ginger",
			"Lemongrass",
			"Mint",
			"Nigella Seed",
			"Nutmeg",
			"Old Bay Seasoning",
			"Oregano",
			"Paprika",
			"Parsley",
			"Rosemary",
			"Sage",
			"Shawarma Spice",
			"Sichuan Peppercorns",
			"Star Anise",
			"Sumac",
			"Tagine Spice",
			"Tarragon",
			"Thyme",
			"Turmeric",
			"White Pepper",
			"Za'atar",
		),
	),
	IngredientChipGroup(
		name = "Oils & Fats",
		items = listOf(
			"Canola Oil",
			"Coconut Oil",
			"Grapeseed Oil",
			"Groundnut Oil",
			"Olive Oil",
			"Sesame Oil",
			"Sunflower Oil",
		),
	),
	IngredientChipGroup(
		name = "Condiments",
		items = listOf(
			"BBQ Sauce",
			"Cranberry Sauce",
			"Harissa",
			"Hoisin Sauce",
			"Hot Sauce",
			"Ketchup",
			"Mango Chutney",
			"Marinara Sauce",
			"Mayonnaise",
			"Miso Paste",
			"Mustard",
			"Olive Brine",
			"Oyster Sauce",
			"Peri Peri Seasoning",
			"Soy Sauce",
			"Sriracha",
			"Tahini",
			"Teriyaki Sauce",
			"Worcestershire Sauce",
		),
	),
	IngredientChipGroup(
		name = "Wine, Beer & Spirits",
		items = listOf(
			"Beer",
			"Brandy",
			"Marsala Wine",
			"Mirin",
			"Red Wine",
			"Rum",
			"Shaoxing Wine",
			"Vodka",
			"White Wine",
		),
	),
	IngredientChipGroup(
		name = "Beverages",
		items = listOf(
			"Apple Juice",
			"Coffee",
			"Lemonade",
			"Orange Juice",
			"Tea",
		),
	),
	IngredientChipGroup(
		name = "Dressings & Vinegars",
		items = listOf(
			"Apple Cider Vinegar",
			"Guacamole",
			"Pico de Gallo",
			"Red Wine Vinegar",
			"Salsa",
			"Salsa Verde",
			"Tzatziki",
			"Vinegar",
			"White Wine Vinegar",
		),
	),
	IngredientChipGroup(
		name = "Nuts & Seeds",
		items = listOf(
			"Almonds",
			"Brazil Nuts",
			"Cashews",
			"Chestnuts",
			"Hazelnuts",
			"Macadamia Nuts",
			"Peanuts",
			"Pecans",
			"Pine Nuts",
			"Pistachios",
			"Sesame",
			"Walnuts",
		),
	),
	IngredientChipGroup(
		name = "Canned Food",
		items = listOf(
			"Capers",
			"Coconut Milk",
			"Diced Tomatoes",
			"Passata",
			"Tomato Paste",
		),
	),
	IngredientChipGroup(
		name = "Supplements & Extracts",
		items = listOf(
			"Almond Extract",
			"Lemon Extract",
			"Nutritional Yeast",
			"Peppermint Extract",
			"Vanilla Extract",
		),
	),
	IngredientChipGroup(
		name = "Soups, Stews & Stocks",
		items = listOf(
			"Beef Stock",
			"Bone Broth",
			"Chicken Stock",
			"Fish Stock",
			"Vegetable Stock",
		),
	),
	IngredientChipGroup(
		name = "Sugar & Sweeteners",
		items = listOf(
			"Brown Sugar",
			"Golden Syrup",
			"Honey",
			"Maple Syrup",
			"Sugar",
		),
	),
	IngredientChipGroup(
		name = "Dessert & Sweets",
		items = listOf(
			"Caramel",
			"Chocolate",
			"Cocoa Powder",
			"Icing Sugar",
			"Marshmallows",
		),
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
