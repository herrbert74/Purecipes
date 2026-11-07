# Culinary & Parser Review Rules

These rules were distilled from reviewing and pinning all 2,417 ingredient names across the Purecipes recipe corpus. Read and follow them strictly whenever reviewing `suggestions.tsv`, pinning rows in `names.tsv`, or editing `ScrapedIngredientLines.kt` / `IngredientLineParser.kt`.

---

## 1. Rules for Pinning (`names.tsv` & `suggestions.tsv`)

### 1.1 When to Leave `pantry_item` Empty vs Set It
- **Set `pantry_item`** only when an item in [`IngredientCatalogue.kt`](../../../../shared/domain/src/commonMain/kotlin/app/purecipes/shared/domain/ingredient/IngredientCatalogue.kt) genuinely represents the ingredient (or its direct culinary category, such as `Cheddar` or `Cheese` for a regional cheese).
- **Leave `pantry_item` empty (`""`)** when:
  - The user explicitly says *"leave empty"* or *"no pantry item"*.
  - The ingredient is water, ice, or a niche specialty item with no reasonable `IngredientCatalogue` match.
- **Never guess a misleading `pantry_item`** just to fill the column (for example, do not map `wine` to `Vinegar` or `lardons` to `Lard`).

### 1.2 When to Leave `fdc_source` / `fdc_id` Empty
- Almost every culinary ingredient has a valid USDA FoodData Central match in `fdc_foundation` or `fdc_sr_legacy`.
- Only zero-calorie non-nutritive items (`ice`, `crushed ice`, `ice cubes`) or non-food decorations should have empty `fdc_source` and `fdc_id`.

### 1.3 Common USDA Food Traps to Avoid

| Ingredient | Correct USDA Target | Trap to Avoid |
|------------|---------------------|---------------|
| `half-and-half`, `half and half` | `Cream, fluid, half and half` (`fdc_sr_legacy` `170859`) | Sour cream (`Cream, sour, cultured`) |
| `double cream`, `heavy cream`, `whipping cream` | `Cream, fluid, heavy whipping` (`fdc_foundation` `746768` or `fdc_sr_legacy` `170856`) | Light cream or whipped topping |
| `single cream`, `light cream`, `pouring cream` | `Cream, fluid, light (coffee cream or table cream)` (`fdc_sr_legacy` `170858`) | Heavy cream or sour cream |
| `clotted cream` | `Cream, fluid, heavy whipping` (`fdc_sr_legacy` `170856`), `pantry_item = Clotted Cream` | Sour cream |
| `aleppo pepper`, `urfa biber`, `kashmiri chili`, `dried red chiles` | `Spices, pepper, red or cayenne` (`fdc_sr_legacy` `170932`) | Sweet red bell pepper (`Peppers, sweet, red, raw`) |
| `thai chiles`, `bird's eye chili`, `serrano`, `scotch bonnet`, `habanero` | `Peppers, hot chili, red, raw` (`fdc_sr_legacy` `168575`) or `green, raw` (`170497`) | Sweet bell pepper or black peppercorn |
| `lardons`, `pancetta`, `guanciale`, `streaky bacon` | Raw/cured pork belly or bacon (`Pork, cured, bacon, uncooked`), `pantry_item = Bacon` / `Pancetta` | Rendered fat (`Lard`) |
| `liquid chicken/beef/vegetable stock or broth` | Prepared stock (`Soup, stock, chicken/beef/vegetable, home-prepared`) | Dry bouillon cubes/powder (which has 10× the calories per 100 g!) |
| `stock cube`, `bouillon cube`, `bouillon granules` | Dry bouillon (`Soup, bouillon, cubes and granules, dry`) | Liquid stock |
| `caster sugar`, `superfine sugar`, `golden caster sugar` | `Sugars, granulated` (`fdc_foundation` `746784`), `pantry_item = Caster Sugar` | Powdered/icing sugar |
| `icing sugar`, `confectioners sugar` | `Sugars, powdered` (`fdc_sr_legacy` `169656`), `pantry_item = Icing Sugar` | Granulated sugar |
| `spring onions`, `scallions`, `salad onions` | `Onions, spring or scallions (includes tops and bulb), raw` (`fdc_sr_legacy` `170005`), `pantry_item = Scallions` | Mature bulb onions |
| `mangetout`, `snow peas`, `sugar snap peas` | `Peas, edible-podded, raw` (`fdc_sr_legacy` `170009`) | Dried split peas |
| `passata`, `sieved tomatoes`, `crushed tomatoes` | `Tomato products, canned, puree, without salt added` (`fdc_sr_legacy` `170460`), `pantry_item = Passata` / `Canned Tomatoes` | Raw whole tomatoes or ketchup |
| `fresh yeast` vs `instant/active dry yeast` | Both map to `Leavening agents, yeast, baker's, active dry` (`175043`) or compressed (`175044`), `pantry_item = Yeast` | Nutritional yeast extract (`Marmite`) |
| `coconut milk (canned)` vs `coconut water` | Canned milk: `Nuts, coconut milk, raw (liquid expressed from grated meat and water)` (`170172`); Coconut water: `Nuts, coconut water` (`170174`) | Mixing up high-fat coconut milk with low-calorie coconut water |

---

## 2. Rules for Scraper & Parser Normalization (`ScrapedIngredientLines.kt` / `IngredientLineParser.kt`)

### 2.1 Never Strip `"leaves"` Globally
- **Bad rule:** Stripping `"leaves"` from the end of any ingredient name turns `"salad leaves"` → `"salad"`, `"mixed leaves"` → `"mixed"`, `"baby leaves"` → `"baby"`, and `"curry leaves"` → `"curry"` (which then falsely matches curry powder!).
- **Good rule:** Only strip `"leaves"` when preceded by a specific culinary herb (`basil`, `mint`, `parsley`, `cilantro`, `coriander`, `thyme`, `oregano`, `rosemary`, `sage`, `tarragon`, `chervil`). Keep `"salad leaves"`, `"mixed leaves"`, `"baby leaves"`, `"curry leaves"`, `"bay leaves"`, `"kaffir lime leaves"`, and `"vine leaves"` intact.

### 2.2 Never Strip Primary Food-Form Nouns
- Words like `steak`, `steaks`, `sausage`, `sausages`, `mince`, `ground`, `broth`, `stock`, `flour`, `oil`, `paste`, `powder`, `seeds`, and `flakes` define **what the food is**, not how the cook prepped it.
- Stripping `steak` from `beef steak` or `sausage` from `pork sausage` corrupts both the pantry match and the USDA nutrition profile.

### 2.3 Order Phrase Rewrites from Longest to Shortest
- When stripping preparation phrases (in `IngredientLinePhraseRewrites.kt` or `ScrapedIngredientLines.kt`), **always list longer multi-word phrases before shorter suffixes**:
  - Good order: `"very finely chopped"`, `"finely chopped"`, `"roughly chopped"`, `"chopped"`.
  - Bad order: If `"chopped"` runs first on `"1 onion, very finely chopped"`, it leaves `"1 onion, very finely"` with a dangling adverb (`lookup_name = "onion very finely"`).
- Always strip preparation adverbs alongside their participles (`"thinly sliced"`, `"thickly sliced"`, `"cut into julienne"`, `"cut into matchsticks"`, `"cut into wedges"`, `"halved lengthways"`, `"peeled and grated"`).

### 2.4 Contextual Group Headings vs Ingredients
- Short composite words (`Chimichurri`, `Pesto`, `Salsa`, `Dressing`, `Marinade`, `Glaze`, `Aioli`, `Sauce`, `Dough`, `Pastry`, `Filling`, `Topping`, `Frosting`, `Icing`, `Garnish`) are **headings** only when they have **no quantity/unit** and introduce a sub-list of ingredients.
- When quantified (`"2 tbsp chimichurri"`, `"150 g pesto"`), they are **ingredients** and must never be dropped as headings.
- If a recipe uses an unusual one-off heading title (for example `"The crispy topping"` or `"For assembly"`), prefer adding a `heading` row to `backend/data/ingredients/line-overrides.tsv` over broadening global heading regexes.
