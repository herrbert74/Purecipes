import java.util.Locale

private val plantLineRegex = Regex(
	"""\b(?:vegan|vegetarian|veggie|plant[\s-]based|meatless|meat[\s-]free|""" +
		"""quorn|seitan|beyond\s+meat|impossible|this\s+isn't|heura|linda\s+mccartney)\b""",
	RegexOption.IGNORE_CASE,
)

private val dairyLineExemptionRegex = Regex(
	"""\b(?:vegan|plant[\s-]based|dairy[\s-]free|non[\s-]dairy)\b""",
	RegexOption.IGNORE_CASE,
)

private val eggLineExemptionRegex = Regex(
	"""\b(?:vegan|plant[\s-]based|egg[\s-]free|eggless)\b""",
	RegexOption.IGNORE_CASE,
)

private val honeyLineExemptionRegex = Regex(
	"""\b(?:vegan|plant[\s-]based)\b""",
	RegexOption.IGNORE_CASE,
)

private val glutenLineExemptionRegex = Regex(
	"""\b(?:gluten[\s-]free|coeliac|celiac)\b""",
	RegexOption.IGNORE_CASE,
)

private val meatRegex = Regex(
	"""\b(?:chicken|beef|steak|sirloin|ribeye|rib-eye|brisket|chuck|short\s+rib|short\s+ribs|""" +
		"""flanken|spare\s+ribs|spareribs|baby\s+back\s+ribs|pork\s+ribs|beef\s+ribs|lamb\s+ribs|""" +
		"""rib\s+of\s+beef|prime\s+rib|oxtail|veal|pork|suckling\s+pig|pig|bacon|pancetta|guanciale|""" +
		"""prosciutto|serrano|iberico|ham|gammon|spam|salami|pepperoni|chorizo|nduja|'nduja|mortadella|""" +
		"""sausage|sausages|bratwurst|frankfurter|merguez|chipolata|black\s+pudding|white\s+pudding|""" +
		"""haggis|lamb|mutton|goat|venison|rabbit|boar|bison|turkey|duck|goose|quail|pheasant|""" +
		"""partridge|pigeon|grouse|guinea\s+fowl|marrow|suet|lard|tallow|schmaltz|dripping|""" +
		"""gelatin|gelatine|bone\s+broth|meatball|meatballs|pigs\s+in\s+blankets|liver|livers|""" +
		"""tripe|sweetbread|foie\s+gras|pâté|pate|biltong|jerky|mince|ground\s+meat|hot\s+dog|""" +
		"""corned\s+beef|pastrami|bresaola|coppa|capicola|soppressata|kielbasa|andouille|""" +
		"""lap\s+cheong|char\s+siu|carnitas|giblets|wing\s+tips)\b""",
	RegexOption.IGNORE_CASE,
)

private val porkRegex = Regex(
	"""\b(?:pork|suckling\s+pig|pig|bacon|pancetta|guanciale|prosciutto|serrano|iberico|""" +
		"""ham|gammon|spam|salami|pepperoni|chorizo|nduja|'nduja|mortadella|bratwurst|""" +
		"""chipolata|black\s+pudding|white\s+pudding|lard|pigs\s+in\s+blankets|coppa|""" +
		"""capicola|soppressata|kielbasa|andouille|lap\s+cheong|char\s+siu|carnitas)\b""",
	RegexOption.IGNORE_CASE,
)

private val meatSafeRegex = Regex(
	"""\b(?:beefsteak\s+tomato|beef\s+tomato|beef\s+tomatoes|mincemeat|nutmeg|coconut\s+meat|""" +
		"""jackfruit|mushroom\s+mince|soy\s+mince|soya\s+mince|lentil\s+mince|quorn\s+mince|""" +
		"""meatless\s+farm|goat\s+cheese|goats\s+cheese|goat's\s+cheese|goat\s+milk|goat's\s+milk|""" +
		"""duck\s+egg|duck\s+eggs|quail\s+egg|quail\s+eggs|guinea\s+pig|""" +
		"""vegetable\s+suet|vegetarian\s+suet)\b""",
	RegexOption.IGNORE_CASE,
)

private val rennetOrGelatinRegex = Regex(
	"""\b(?:parmesan|parmigiano|pecorino|grana\s+padano|gorgonzola|gruyere|gruyère|""" +
		"""marshmallow|marshmallows)\b""",
	RegexOption.IGNORE_CASE,
)

private val fishRegex = Regex(
	"""\b(?:fish|salmon|tuna|cod|haddock|halibut|trout|mackerel|sardine|sardines|anchovy|anchovies|""" +
		"""herring|pilchard|sea\s+bass|seabass|sea\s+bream|bream|snapper|grouper|tilapia|catfish|""" +
		"""monkfish|swordfish|pollock|coley|plaice|sole|flounder|turbot|skate|kipper|sprat|""" +
		"""whitebait|whiting|hake|arctic\s+char|roe|caviar|ikura|tobiko|masago|fish\s+sauce|""" +
		"""nam\s+pla|fish\s+stock|fish\s+broth|fish\s+fillet|white\s+fish|smoked\s+fish|dashi|""" +
		"""bonito|katsuobushi|worcestershire\s+sauce|surimi)\b""",
	RegexOption.IGNORE_CASE,
)

private val fishSafeRegex = Regex(
	"""\b(?:vegan\s+fish\s+sauce|vegetarian\s+fish\s+sauce|vegan\s+worcestershire|""" +
		"""vegetarian\s+worcestershire|vegan\s+dashi|kombu\s+dashi|shiitake\s+dashi|fish\s+pepper)\b""",
	RegexOption.IGNORE_CASE,
)

private val shellfishRegex = Regex(
	"""\b(?:prawn|prawns|shrimp|shrimps|langoustine|langoustines|scampi|lobster|crab|crabmeat|""" +
		"""crayfish|crawfish|krill|mussel|mussels|clam|clams|oyster|oysters|scallop|scallops|""" +
		"""squid|calamari|octopus|cuttlefish|cockle|cockles|whelk|whelks|abalone|conch|""" +
		"""periwinkle|escargot|snail|snails|oyster\s+sauce|shrimp\s+paste|belacan|shellfish|""" +
		"""seafood\s+mix|zuppa\s+di\s+pesce)\b""",
	RegexOption.IGNORE_CASE,
)

private val shellfishSafeRegex = Regex(
	"""\b(?:oyster\s+mushroom|oyster\s+mushrooms|king\s+oyster|vegan\s+oyster\s+sauce|""" +
		"""vegetarian\s+oyster\s+sauce|mushroom\s+oyster\s+sauce)\b""",
	RegexOption.IGNORE_CASE,
)

private val dairyRegex = Regex(
	"""\b(?:milk|butter|cheese|cream|yoghurt|yogurt|ghee|buttermilk|whey|casein|lactose|curd|""" +
		"""paneer|halloumi|feta|parmesan|parmigiano|pecorino|grana\s+padano|cheddar|mozzarella|""" +
		"""burrata|ricotta|mascarpone|brie|camembert|gruyere|gruyère|emmental|gouda|edam|""" +
		"""manchego|provolone|gorgonzola|roquefort|stilton|goat\s+cheese|goats\s+cheese|""" +
		"""goat's\s+cheese|cottage\s+cheese|quark|fromage\s+frais|cr[eè]me\s+fra[iî]che|""" +
		"""clotted\s+cream|half\s+and\s+half|half-and-half|dulce\s+de\s+leche|kefir|labneh|skyr|""" +
		"""custard|ice\s+cream|gelato|milk\s+chocolate|white\s+chocolate|baileys|queso|cotija|""" +
		"""oaxaca|monterey\s+jack|colby|fontina|asiago|taleggio|raclette|red\s+leicester|""" +
		"""wensleydale|lancashire|caerphilly|cheshire|double\s+gloucester|vacherin|reblochon|""" +
		"""comté|comte|velveeta|romano|montrachet|raita|pesto|ranch\s+dressing)\b""",
	RegexOption.IGNORE_CASE,
)

private val dairySafeRegex = Regex(
	"""\b(?:coconut\s+milk|oat\s+milk|almond\s+milk|soy\s+milk|soya\s+milk|cashew\s+milk|""" +
		"""rice\s+milk|pea\s+milk|hemp\s+milk|macadamia\s+milk|hazelnut\s+milk|plant\s+milk|""" +
		"""plant[\s-]based\s+milk|non[\s-]dairy\s+milk|dairy[\s-]free\s+milk|vegan\s+milk|""" +
		"""peanut\s+butter|almond\s+butter|cashew\s+butter|hazelnut\s+butter|pistachio\s+butter|""" +
		"""pecan\s+butter|walnut\s+butter|macadamia\s+butter|sunflower\s+butter|""" +
		"""sunflower\s+seed\s+butter|nut\s+butter|seed\s+butter|cocoa\s+butter|coconut\s+butter|""" +
		"""apple\s+butter|pumpkin\s+butter|cookie\s+butter|biscoff\s+butter|vegan\s+butter|""" +
		"""plant\s+butter|plant[\s-]based\s+butter|dairy[\s-]free\s+butter|non[\s-]dairy\s+butter|""" +
		"""butternut|buttercup|butter\s+bean|butter\s+beans|butterbean|butterbeans|butterhead|""" +
		"""butter\s+lettuce|butterfly|butterflied|coconut\s+cream|cream\s+of\s+coconut|""" +
		"""creamed\s+coconut|cream\s+of\s+tartar|cream\s+soda|creamed\s+corn|cashew\s+cream|""" +
		"""oat\s+cream|soy\s+cream|soya\s+cream|vegan\s+cream|dairy[\s-]free\s+cream|""" +
		"""non[\s-]dairy\s+cream|plant\s+cream|plant[\s-]based\s+cream|salad\s+cream|""" +
		"""vegan\s+cheese|plant[\s-]based\s+cheese|dairy[\s-]free\s+cheese|non[\s-]dairy\s+cheese|""" +
		"""cashew\s+cheese|cheesecloth|coconut\s+yoghurt|coconut\s+yogurt|soy\s+yoghurt|""" +
		"""soya\s+yoghurt|soy\s+yogurt|soya\s+yogurt|almond\s+yoghurt|almond\s+yogurt|""" +
		"""oat\s+yoghurt|oat\s+yogurt|vegan\s+yoghurt|vegan\s+yogurt|dairy[\s-]free\s+yoghurt|""" +
		"""dairy[\s-]free\s+yogurt|plant[\s-]based\s+yoghurt|plant[\s-]based\s+yogurt|""" +
		"""bean\s+curd|tofu\s+curd|vegan\s+ice\s+cream|dairy[\s-]free\s+ice\s+cream|""" +
		"""vegan\s+custard|vegan\s+mayo|vegan\s+mayonnaise|vegan\s+chocolate|""" +
		"""vegan\s+milk\s+chocolate|vegan\s+white\s+chocolate|vegan\s+pesto|dairy[\s-]free\s+pesto)\b""",
	RegexOption.IGNORE_CASE,
)

private val eggRegex = Regex(
	"""\b(?:egg|eggs|egg\s+yolk|egg\s+yolks|egg\s+white|egg\s+whites|albumen|meringue|""" +
		"""mayonnaise|mayo|hollandaise|béarnaise|bearnaise|lemon\s+curd|egg\s+noodle|egg\s+noodles|""" +
		"""brioche|challah|advocaat|salad\s+cream|tartare\s+sauce|tartar\s+sauce|aioli)\b""",
	RegexOption.IGNORE_CASE,
)

private val eggSafeRegex = Regex(
	"""\b(?:eggplant|eggplants|aubergine|vegan\s+mayonnaise|vegan\s+mayo|egg[\s-]free\s+mayo|""" +
		"""egg[\s-]free\s+mayonnaise|vegan\s+egg|flax\s+egg|chia\s+egg|egg\s+replacer|""" +
		"""vegan\s+brioche|vegan\s+aioli|easter\s+egg)\b""",
	RegexOption.IGNORE_CASE,
)

private val honeyRegex = Regex(
	"""\b(?:honey|royal\s+jelly|bee\s+pollen|beeswax)\b""",
	RegexOption.IGNORE_CASE,
)

private val honeySafeRegex = Regex(
	"""\b(?:honeydew|honeynut|vegan\s+honey)\b""",
	RegexOption.IGNORE_CASE,
)

private val glutenRegex = Regex(
	"""\b(?:wheat|barley|rye|spelt|kamut|khorasan|farro|emmer|einkorn|triticale|bulgur|bulghar|""" +
		"""couscous|giant\s+couscous|pearl\s+barley|freekeh|semolina|durum|orzo|seitan|""" +
		"""vital\s+wheat\s+gluten|malt|malted|beer|ale|stout|guinness|lager|ipa|flour|""" +
		"""breadcrumbs|breadcrumb|panko|crouton|croutons|bread|baguette|baguettes|ciabatta|""" +
		"""focaccia|sourdough|brioche|pita|pitas|pitta|pittas|naan|roti|chapati|paratha|""" +
		"""tortilla|tortillas|wrap|wraps|bagel|bagels|crumpet|crumpets|croissant|croissants|""" +
		"""muffin|muffins|flatbread|flatbreads|bun|buns|roll|rolls|crostini|bruschetta|""" +
		"""matzo|matzah|sandwich|sandwiches|toastie|toasties|panini|sub|hoagie|pastry|""" +
		"""puff\s+pastry|filo|phyllo|shortcrust|pie\s+crust|pastry\s+crust|tart\s+shell|""" +
		"""biscuit|biscuits|digestive|digestives|cookie|cookies|cracker|crackers|wafer|wafers|""" +
		"""pretzel|pretzels|cake|sponge|pancake|pancakes|waffle|waffles|doughnut|doughnuts|""" +
		"""donut|donuts|biscoff|cereal|dumpling|dumplings|gyoza|wonton|wontons|bao|pierogi|""" +
		"""gnocchi|pasta|spaghetti|penne|rigatoni|fusilli|macaroni|elbows|tagliatelle|""" +
		"""fettuccine|linguine|lasagne|lasagna|pappardelle|orecchiette|conchiglie|farfalle|""" +
		"""cannelloni|ravioli|tortellini|orrecchiette|bucatini|paccheri|mafaldine|ditalini|""" +
		"""cavatelli|trofie|stelline|rotini|ziti|manicotti|cavatappi|fregola|noodle|noodles|""" +
		"""udon|ramen|chow\s+mein|lo\s+mein|vermicelli|soy\s+sauce|shoyu|hoisin|hoi\s+sin|""" +
		"""oyster\s+sauce|teriyaki|gochujang|worcestershire\s+sauce|malt\s+vinegar|suet|""" +
		"""stuffing\s+mix|gravy\s+granules|sausage|sausages|chipolata|black\s+pudding|""" +
		"""white\s+pudding|haggis|quorn)\b""",
	RegexOption.IGNORE_CASE,
)

private val glutenSafeRegex = Regex(
	"""\b(?:gluten[\s-]free|gf\s+flour|almond\s+flour|coconut\s+flour|rice\s+flour|""" +
		"""glutinous\s+rice\s+flour|cornflour|corn\s+flour|cornstarch|corn\s+starch|""" +
		"""masa\s+harina|chickpea\s+flour|gram\s+flour|besan|tapioca\s+flour|tapioca\s+starch|""" +
		"""cassava\s+flour|arrowroot\s+flour|buckwheat\s+flour|potato\s+flour|potato\s+starch|""" +
		"""sorghum\s+flour|oat\s+flour|quinoa\s+flour|teff\s+flour|chestnut\s+flour|""" +
		"""corn\s+tortilla|corn\s+tortillas|tortilla\s+chip|tortilla\s+chips|rice\s+noodle|""" +
		"""rice\s+noodles|rice\s+vermicelli|glass\s+noodle|glass\s+noodles|cellophane\s+noodle|""" +
		"""cellophane\s+noodles|mung\s+bean\s+noodle|sweet\s+potato\s+noodle|""" +
		"""sweet\s+potato\s+noodles|courgette\s+noodle|zucchini\s+noodle|rice\s+paper|""" +
		"""rice\s+cracker|rice\s+crackers|rice\s+cake|rice\s+cakes|corn\s+cake|corn\s+cakes|""" +
		"""tamari|gluten[\s-]free\s+soy\s+sauce|coconut\s+aminos|buckwheat\s+groats|rice\s+roll|""" +
		"""ginger\s+beer|ginger\s+ale|root\s+beer)\b""",
	RegexOption.IGNORE_CASE,
)

private val nutRegex = Regex(
	"""\b(?:almond|almonds|walnut|walnuts|pecan|pecans|cashew|cashews|pistachio|pistachios|""" +
		"""hazelnut|hazelnuts|cobnut|cobnuts|filbert|filberts|macadamia|macadamias|""" +
		"""brazil\s+nut|brazil\s+nuts|chestnut|chestnuts|pine\s+nut|pine\s+nuts|pinenut|""" +
		"""pinenuts|pignoli|peanut|peanuts|groundnut|groundnuts|monkey\s+nut|monkey\s+nuts|""" +
		"""praline|marzipan|frangipane|gianduja|nutella|amaretto|frangelico|dukkah|satay|""" +
		"""pesto|nut\s+butter|mixed\s+nuts)\b""",
	RegexOption.IGNORE_CASE,
)

private val nutSafeRegex = Regex(
	"""\b(?:nutmeg|butternut|coconut|water\s+chestnut|water\s+chestnuts|chestnut\s+mushroom|""" +
		"""chestnut\s+mushrooms|doughnut|doughnuts|donut|donuts|tigernut|tigernuts|""" +
		"""nutritional\s+yeast|nut[\s-]free)\b""",
	RegexOption.IGNORE_CASE,
)

private val alliumRegex = Regex(
	"""\b(?:onion|onions|garlic|shallot|shallots|scallion|scallions|spring\s+onion|""" +
		"""spring\s+onions|green\s+onion|green\s+onions|leek|leeks|chive|chives|ramps|""" +
		"""wild\s+garlic)\b""",
	RegexOption.IGNORE_CASE,
)

private val alliumSafeRegex = Regex(
	"""\b(?:allium[\s-]free|no\s+onion\s+no\s+garlic)\b""",
	RegexOption.IGNORE_CASE,
)

private val alcoholRegex = Regex(
	"""\b(?:wine|beer|ale|stout|guinness|lager|ipa|cider|sherry|port|vermouth|prosecco|""" +
		"""champagne|vodka|gin|rum|whisky|whiskey|bourbon|brandy|cognac|tequila|mezcal|""" +
		"""liqueur|amaretto|baileys|kirsch|mirin|shaoxing|sake|aperol|amaro|cointreau|""" +
		"""triple\s+sec|marsala|kahlua|frangelico|grand\s+marnier|limoncello|advocaat)\b""",
	RegexOption.IGNORE_CASE,
)

private val alcoholSafeRegex = Regex(
	"""\b(?:red\s+wine\s+vinegar|white\s+wine\s+vinegar|rice\s+wine\s+vinegar|""" +
		"""apple\s+cider\s+vinegar|cider\s+vinegar|sherry\s+vinegar|champagne\s+vinegar|""" +
		"""non[\s-]alcoholic|alcohol[\s-]free|ginger\s+beer|ginger\s+ale|root\s+beer)\b""",
	RegexOption.IGNORE_CASE,
)

private val paleoExcludedRegex = Regex(
	"""\b(?:rice|risotto|arborio|basmati|paella|pilaf|polenta|cornmeal|grits|hominy|masa|""" +
		"""corn|sweetcorn|popcorn|cornflour|cornstarch|oats|oat|oatmeal|porridge|pinhead|""" +
		"""quinoa|buckwheat|amaranth|millet|sorghum|teff|taco\s+shell|taco\s+shells|""" +
		"""tortilla\s+chip|tortilla\s+chips|bean|beans|lentil|lentils|dal|dhal|chickpea|""" +
		"""chickpeas|garbanzo|hummus|houmous|pea|peas|mange\s+tout|mangetout|sugarsnap|""" +
		"""edamame|peanut|peanuts|groundnut|monkey\s+nut|monkey\s+nuts|satay|soy|soya|""" +
		"""tofu|tempeh|miso|tamari|shoyu|natto|sugar|caster|granulated|muscovado|demerara|""" +
		"""turbinado|icing|confectioners|golden\s+syrup|corn\s+syrup|simple\s+syrup|glucose|""" +
		"""fructose|agave|treacle|molasses|caramel|butterscotch|toffee|fudge|marshmallow|""" +
		"""marshmallows|jam|jelly|marmalade|chutney|nutella|praline|marzipan|condensed|""" +
		"""evaporated|chocolate|cordial|tonic\s+water|soda|lemonade|sweet\s+and\s+sour|""" +
		"""vegetable\s+oil|sunflower\s+oil|canola\s+oil|canola|rapeseed\s+oil|rapeseed|""" +
		"""corn\s+oil|soybean\s+oil|grapeseed\s+oil|groundnut\s+oil|peanut\s+oil|neutral\s+oil|""" +
		"""neutral[\s-]flavored\s+oil|cooking\s+spray|margarine|shortening|wine|beer|ale|stout|""" +
		"""guinness|lager|cider|sherry|port|vermouth|prosecco|champagne|vodka|gin|rum|""" +
		"""whisky|whiskey|bourbon|brandy|cognac|tequila|mezcal|liqueur|amaretto|baileys|""" +
		"""kirsch|mirin|shaoxing|sake|aperol|amaro|cointreau|triple\s+sec|ketchup|""" +
		"""worcestershire|hoisin|hoi\s+sin|oyster\s+sauce|teriyaki|sriracha|sweet\s+chilli|""" +
		"""brown\s+sauce|hp\s+sauce|bbq\s+sauce|barbecue\s+sauce|msg|monosodium\s+glutamate|""" +
		"""stock\s+cube|bouillon\s+cube|gravy\s+granules|baking\s+powder|quorn|plant[\s-]based|""" +
		"""meatless|vegan\s+cheese|vegan\s+butter|vegan\s+mayonnaise|vegan\s+mayo|mayonnaise|""" +
		"""mayo|potato|potatoes|maris\s+piper|king\s+edward|desiree|russet|yukon\s+gold|""" +
		"""jersey\s+royal|fries|chips)\b""",
	RegexOption.IGNORE_CASE,
)

private val paleoSafeRegex = Regex(
	"""\b(?:sweet\s+potato|sweet\s+potatoes|almond\s+flour|coconut\s+flour|cassava\s+flour|""" +
		"""tapioca\s+flour|tapioca\s+starch|arrowroot\s+flour|arrowroot\s+starch|chestnut\s+flour|""" +
		"""tigernut\s+flour|coconut\s+milk|almond\s+milk|cashew\s+milk|hazelnut\s+milk|""" +
		"""macadamia\s+milk|coconut\s+cream|cream\s+of\s+coconut|creamed\s+coconut|""" +
		"""cream\s+of\s+tartar|coconut\s+yoghurt|coconut\s+yogurt|almond\s+yoghurt|almond\s+yogurt|""" +
		"""almond\s+butter|cashew\s+butter|hazelnut\s+butter|pecan\s+butter|walnut\s+butter|""" +
		"""pistachio\s+butter|macadamia\s+butter|sunflower\s+seed\s+butter|sunflower\s+butter|""" +
		"""tahini|cocoa\s+butter|coconut\s+butter|apple\s+butter|pumpkin\s+butter|nut\s+butter|""" +
		"""seed\s+butter|clarified\s+butter|ghee|butternut|buttercup|butterhead|butter\s+lettuce|""" +
		"""butterfly|butterflied|coconut\s+aminos|coconut\s+sugar|palm\s+sugar|date\s+sugar|""" +
		"""date\s+syrup|maple\s+syrup|cocoa\s+powder|cacao\s+powder|cacao\s+nibs|raw\s+cacao|""" +
		"""100%\s+dark\s+chocolate|red\s+wine\s+vinegar|white\s+wine\s+vinegar|""" +
		"""rice\s+wine\s+vinegar|rice\s+vinegar|apple\s+cider\s+vinegar|cider\s+vinegar|""" +
		"""sherry\s+vinegar|champagne\s+vinegar|balsamic\s+vinegar|vanilla\s+bean|""" +
		"""vanilla\s+beans|coffee\s+bean|coffee\s+beans|cocoa\s+bean|cacao\s+bean|tonka\s+bean|""" +
		"""green\s+bean|green\s+beans|french\s+bean|french\s+beans|runner\s+bean|runner\s+beans|""" +
		"""pink\s+peppercorn|black\s+peppercorn|white\s+peppercorn|sichuan\s+peppercorn|""" +
		"""green\s+peppercorn|peppercorns|courgette\s+noodle|courgette\s+noodles|""" +
		"""zucchini\s+noodle|zucchini\s+noodles|butternut\s+squash\s+noodle|cauliflower\s+rice|""" +
		"""baking\s+soda|bicarbonate\s+of\s+soda|paleo\s+mayonnaise|paleo\s+mayo|""" +
		"""avocado\s+oil\s+mayonnaise)\b""",
	RegexOption.IGNORE_CASE,
)

private val ketoExcludedRegex = Regex(
	"""\b(?:rice|risotto|arborio|basmati|paella|pilaf|polenta|cornmeal|grits|hominy|masa|""" +
		"""corn|sweetcorn|popcorn|cornflour|cornstarch|oats|oat|oatmeal|porridge|pinhead|""" +
		"""quinoa|buckwheat|amaranth|millet|sorghum|teff|taco\s+shell|taco\s+shells|""" +
		"""tortilla\s+chip|tortilla\s+chips|bean|beans|lentil|lentils|dal|dhal|chickpea|""" +
		"""chickpeas|garbanzo|hummus|houmous|pea|peas|miso|potato|potatoes|maris\s+piper|""" +
		"""king\s+edward|desiree|russet|yukon\s+gold|jersey\s+royal|sweet\s+potato|""" +
		"""sweet\s+potatoes|yam|yams|cassava|tapioca|arrowroot|plantain|plantains|parsnip|""" +
		"""parsnips|butternut\s+squash|pumpkin|swede|rutabaga|beetroot|beet|beets|carrot|""" +
		"""carrots|taro|fries|chips|sugar|caster|granulated|muscovado|demerara|turbinado|""" +
		"""icing|confectioners|honey|maple\s+syrup|golden\s+syrup|corn\s+syrup|simple\s+syrup|""" +
		"""glucose|fructose|agave|treacle|molasses|caramel|butterscotch|toffee|fudge|""" +
		"""marshmallow|marshmallows|jam|jelly|marmalade|chutney|nutella|praline|marzipan|""" +
		"""condensed\s+milk|evaporated\s+milk|dulce\s+de\s+leche|milk\s+chocolate|""" +
		"""white\s+chocolate|dark\s+chocolate|chocolate\s+chips|banana|bananas|mango|mangoes|""" +
		"""pineapple|apple|apples|pear|pears|grape|grapes|raisin|raisins|sultana|sultanas|""" +
		"""currant|currants|date|dates|fig|figs|prune|prunes|apricot|apricots|orange|oranges|""" +
		"""clementine|clementines|tangerine|mandarin|peach|peaches|nectarine|plum|plums|""" +
		"""cherry|cherries|pomegranate|watermelon|melon|cantaloupe|honeydew|papaya|lychee|""" +
		"""persimmon|jackfruit|cranberry\s+sauce|apple\s+juice|orange\s+juice|pineapple\s+juice|""" +
		"""sun[\s-]dried\s+tomato|sun[\s-]dried\s+tomatoes|ketchup|hoisin|hoi\s+sin|teriyaki|""" +
		"""sweet\s+chilli|balsamic\s+glaze|balsamic\s+vinegar|brown\s+sauce|hp\s+sauce|""" +
		"""bbq\s+sauce|barbecue\s+sauce|satay|curry\s+base|marinade|mirin|tonic\s+water|""" +
		"""cordial|soda|lemonade|sweet\s+and\s+sour|whole\s+milk|skimmed\s+milk|""" +
		"""semi[\s-]skimmed\s+milk|oat\s+milk|rice\s+milk|buttermilk|ice\s+cream|custard|""" +
		"""wine|beer|ale|stout|guinness|lager|cider|port|sherry|vermouth|prosecco|champagne|""" +
		"""vodka|gin|rum|whisky|whiskey|bourbon|brandy|cognac|tequila|mezcal|liqueur|""" +
		"""amaretto|baileys|aperol|amaro|cointreau|triple\s+sec)\b""",
	RegexOption.IGNORE_CASE,
)

private val ketoSafeRegex = Regex(
	"""\b(?:almond\s+flour|coconut\s+flour|flaxseed\s+meal|golden\s+flax|psyllium|""" +
		"""coconut\s+milk|almond\s+milk|soy\s+milk|soya\s+milk|cashew\s+milk|macadamia\s+milk|""" +
		"""coconut\s+cream|cream\s+of\s+coconut|creamed\s+coconut|cream\s+of\s+tartar|""" +
		"""sugar[\s-]free|erythritol|stevia|monk\s+fruit|xylitol|allulose|swerve|""" +
		"""apple\s+cider\s+vinegar|cider\s+vinegar|red\s+wine\s+vinegar|white\s+wine\s+vinegar|""" +
		"""rice\s+wine\s+vinegar|rice\s+vinegar|sherry\s+vinegar|vanilla\s+bean|""" +
		"""vanilla\s+beans|coffee\s+bean|coffee\s+beans|cocoa\s+bean|cacao\s+bean|green\s+bean|""" +
		"""green\s+beans|french\s+bean|french\s+beans|runner\s+bean|runner\s+beans|""" +
		"""pink\s+peppercorn|black\s+peppercorn|white\s+peppercorn|sichuan\s+peppercorn|""" +
		"""green\s+peppercorn|peppercorns|courgette\s+noodle|courgette\s+noodles|""" +
		"""zucchini\s+noodle|zucchini\s+noodles|shirataki\s+noodle|konjac\s+noodle|""" +
		"""cauliflower\s+rice|orange\s+zest|zest\s+of\s+(?:an?\s+|1\s+)?orange|lemon\s+zest|""" +
		"""lime\s+zest|grapefruit\s+zest|100%\s+dark\s+chocolate|cocoa\s+powder|cacao\s+powder|""" +
		"""cacao\s+nibs)\b""",
	RegexOption.IGNORE_CASE,
)

private val lowFodmapExcludedRegex = Regex(
	"""\b(?:onion|onions|garlic|shallot|shallots|scallion|scallions|spring\s+onion|""" +
		"""spring\s+onions|green\s+onion|green\s+onions|leek|leeks|ramps|wild\s+garlic|""" +
		"""bean|beans|lentil|lentils|dal|dhal|chickpea|chickpeas|garbanzo|hummus|houmous|""" +
		"""pea|peas|mange\s+tout|mangetout|sugarsnap|pistachio|pistachios|cashew|cashews|""" +
		"""almond\s+butter|ground\s+almond|ground\s+almonds|hazelnut|hazelnuts|mixed\s+nuts|""" +
		"""milk|buttermilk|whey|lactose|curd|paneer|halloumi|ricotta|mascarpone|""" +
		"""cottage\s+cheese|quark|fromage\s+frais|cr[eè]me\s+fra[iî]che|cream|yoghurt|yogurt|""" +
		"""kefir|labneh|skyr|custard|ice\s+cream|gelato|condensed|evaporated|""" +
		"""dulce\s+de\s+leche|milk\s+chocolate|white\s+chocolate|raita|honey|agave|""" +
		"""golden\s+syrup|corn\s+syrup|fructose|molasses|treacle|jam|marmalade|chutney|""" +
		"""sweet\s+and\s+sour|cordial|tonic\s+water|soda|apple|apples|pear|pears|mango|""" +
		"""mangoes|watermelon|peach|peaches|plum|plums|nectarine|cherry|cherries|apricot|""" +
		"""apricots|blackberry|blackberries|fig|figs|date|dates|prune|prunes|raisin|raisins|""" +
		"""sultana|sultanas|currant|currants|avocado|persimmon|lychee|passion\s+fruit|""" +
		"""cauliflower|mushroom|mushrooms|asparagus|artichoke|artichokes|beetroot|beet|beets|""" +
		"""brussels\s+sprout|savoy\s+cabbage|celery|butternut\s+squash|sweetcorn|""" +
		"""corn\s+on\s+the\s+cob|sweet\s+potato|sweet\s+potatoes|yam|yams|cassava|kimchi|""" +
		"""curry\s+paste|tikka\s+masala|chilli\s+con\s+carne|chimichurri|harissa|""" +
		"""chipotle\s+paste|miso|sambar|tajin|tajín|rum|sherry|port|vermouth|liqueur|""" +
		"""brandy|cognac|triple\s+sec|cointreau|amaro|aperol|mezcal|tequila|whiskey|whisky|""" +
		"""bourbon)\b""",
	RegexOption.IGNORE_CASE,
)

private val lowFodmapSafeRegex = Regex(
	"""\b(?:almond\s+milk|macadamia\s+milk|rice\s+milk|quinoa\s+milk|hemp\s+milk|""" +
		"""lactose[\s-]free\s+milk|lactose[\s-]free\s+yoghurt|lactose[\s-]free\s+yogurt|""" +
		"""cream\s+of\s+tartar|garlic[\s-]infused\s+oil|chive|chives|vanilla\s+bean|""" +
		"""vanilla\s+beans|coffee\s+bean|coffee\s+beans|cocoa\s+bean|cacao\s+bean|green\s+bean|""" +
		"""green\s+beans|french\s+bean|french\s+beans|apple\s+cider\s+vinegar|cider\s+vinegar|""" +
		"""oyster\s+mushroom|oyster\s+mushrooms|baking\s+soda|bicarbonate\s+of\s+soda)\b""",
	RegexOption.IGNORE_CASE,
)

private val ketoMacroSourceRegex = Regex(
	"""\b(?:chicken|beef|steak|sirloin|ribeye|brisket|chuck|pork|bacon|pancetta|prosciutto|""" +
		"""ham|gammon|salami|chorizo|sausage|lamb|turkey|duck|goose|venison|pigeon|salmon|""" +
		"""tuna|cod|haddock|halibut|trout|mackerel|sardine|anchovy|sea\s+bass|seabass|snapper|""" +
		"""monkfish|pollock|prawn|prawns|shrimp|lobster|crab|scallop|squid|calamari|mussel|""" +
		"""clam|egg|eggs|cheese|halloumi|feta|parmesan|pecorino|cheddar|mozzarella|burrata|""" +
		"""ricotta|mascarpone|brie|gruyere|gruyère|gouda|manchego|gorgonzola|stilton|""" +
		"""goat\s+cheese|goats\s+cheese|goat's\s+cheese|cream\s+cheese|fontina|taleggio|""" +
		"""butter|ghee|double\s+cream|heavy\s+cream|whipping\s+cream|cr[eè]me\s+fra[iî]che|""" +
		"""sour\s+cream|mayonnaise|mayo|olive\s+oil|coconut\s+oil|avocado\s+oil|walnut\s+oil|""" +
		"""sesame\s+oil|coconut\s+milk|coconut\s+cream|avocado|almond|almonds|walnut|walnuts|""" +
		"""pecan|pecans|macadamia|pine\s+nut|tahini|tofu)\b""",
	RegexOption.IGNORE_CASE,
)

private data class ExplicitDietaryPattern(
	val preferenceName: String,
	val regex: Regex,
)

private val explicitDietaryPatterns = listOf(
	ExplicitDietaryPattern("VEGAN", Regex("""\b(?:vegan|vegan\s*diet|plant[\s-]based)\b""", RegexOption.IGNORE_CASE)),
	ExplicitDietaryPattern(
		"VEGETARIAN",
		Regex(
			"""\b(?:vegetarian|vegetarian\s*diet|veggie|meat[\s-]free|meatless|lacto[\s-]ovo)\b""",
			RegexOption.IGNORE_CASE,
		),
	),
	ExplicitDietaryPattern("PESCATARIAN", Regex("""\b(?:pescatarian|pescetarian)\b""", RegexOption.IGNORE_CASE)),
	ExplicitDietaryPattern(
		"GLUTEN_FREE",
		Regex("""\b(?:gluten[\s-]?free(?:\s*diet)?|coeliac|celiac)\b""", RegexOption.IGNORE_CASE),
	),
	ExplicitDietaryPattern(
		"DAIRY_FREE",
		Regex(
			"""\b(?:dairy[\s-]?free(?:\s*diet)?|lactose[\s-]?free|low\s*lactose(?:\s*diet)?|non[\s-]dairy)\b""",
			RegexOption.IGNORE_CASE,
		),
	),
	ExplicitDietaryPattern(
		"NUT_FREE",
		Regex("""\b(?:nut[\s-]?free|peanut[\s-]?free|tree[\s-]nut[\s-]?free|no[\s-]nuts)\b""", RegexOption.IGNORE_CASE),
	),
	ExplicitDietaryPattern("EGG_FREE", Regex("""\b(?:egg[\s-]?free|eggless)\b""", RegexOption.IGNORE_CASE)),
	ExplicitDietaryPattern("SHELLFISH_FREE", Regex("""\b(?:shellfish[\s-]?free)\b""", RegexOption.IGNORE_CASE)),
	ExplicitDietaryPattern(
		"ALLIUM_FREE",
		Regex("""\b(?:allium[\s-]?free|no\s+onion\s+no\s+garlic)\b""", RegexOption.IGNORE_CASE),
	),
	ExplicitDietaryPattern("HALAL", Regex("""\b(?:halal(?:\s*diet)?)\b""", RegexOption.IGNORE_CASE)),
	ExplicitDietaryPattern("KOSHER", Regex("""\b(?:kosher(?:\s*diet)?)\b""", RegexOption.IGNORE_CASE)),
	ExplicitDietaryPattern("LOW_FODMAP", Regex("""\b(?:low[\s-]?fodmap)\b""", RegexOption.IGNORE_CASE)),
	ExplicitDietaryPattern(
		"PALEO",
		Regex(
			"""\b(?:paleo(?:\s*diet)?|paleolithic|caveman\s+diet|primal|whole\s*30)\b""",
			RegexOption.IGNORE_CASE,
		),
	),
	ExplicitDietaryPattern(
		"KETO",
		Regex("""\b(?:keto|ketogenic(?:\s*diet)?|keto[\s-]friendly)\b""", RegexOption.IGNORE_CASE),
	),
)

private val knownDietaryPreferenceNames = explicitDietaryPatterns.map { it.preferenceName }.toSet()

private data class ScrapedIngredientProfile(
	val hasMeat: Boolean,
	val hasPork: Boolean,
	val hasAlcohol: Boolean,
	val hasRennetOrGelatin: Boolean,
	val hasFish: Boolean,
	val hasShellfish: Boolean,
	val hasDairy: Boolean,
	val hasEgg: Boolean,
	val hasHoney: Boolean,
	val hasGluten: Boolean,
	val hasNut: Boolean,
	val hasAllium: Boolean,
	val hasPaleoExcluded: Boolean,
	val hasKetoExcluded: Boolean,
	val hasLowFodmapExcluded: Boolean,
	val hasKetoMacroSource: Boolean,
) {
	val isMeatAndSeafoodFree: Boolean = !hasMeat && !hasFish && !hasShellfish
	val isAnimalProductFree: Boolean = !hasDairy && !hasEgg && !hasHoney
	val isVegan: Boolean = isMeatAndSeafoodFree && isAnimalProductFree

	fun isVegetarian(explicit: Set<String>): Boolean {
		val rennetAllowed = !hasRennetOrGelatin || "VEGETARIAN" in explicit || isVegan
		return isMeatAndSeafoodFree && rennetAllowed
	}

	fun isPescatarian(explicit: Set<String>, isVegetarian: Boolean): Boolean {
		val rennetAllowed = !hasRennetOrGelatin || "PESCATARIAN" in explicit || isVegetarian
		return !hasMeat && rennetAllowed
	}

	val isPaleo: Boolean = !hasGluten && !hasDairy && !hasPaleoExcluded

	fun isKeto(explicit: Set<String>): Boolean {
		val hasKetoSignal = hasKetoMacroSource || "KETO" in explicit
		return !hasGluten && !hasKetoExcluded && hasKetoSignal
	}

	val isLowFodmap: Boolean = !hasGluten && !hasAllium && !hasLowFodmapExcluded

	fun isHalal(explicit: Set<String>): Boolean = "HALAL" in explicit && !hasPork && !hasAlcohol

	fun isKosher(explicit: Set<String>): Boolean = "KOSHER" in explicit && !hasPork && !hasShellfish
}

fun extractScrapedDietaryPreferences(
	title: String? = null,
	category: String? = null,
	ingredients: List<String> = emptyList(),
	explicitLabels: List<String> = emptyList(),
): List<String> {
	val explicit = collectExplicitDietaryPreferences(title, category, explicitLabels)
	val nonEmptyIngredients = ingredients.map { it.trim() }.filter { it.isNotEmpty() }
	if (nonEmptyIngredients.isEmpty()) {
		return expandExplicitOnlyPreferences(explicit)
	}
	val profile = buildScrapedIngredientProfile(title.orEmpty(), nonEmptyIngredients)
	return classifyDietaryPreferences(profile, explicit)
}

private fun collectExplicitDietaryPreferences(
	title: String?,
	category: String?,
	explicitLabels: List<String>,
): Set<String> {
	val result = linkedSetOf<String>()
	val rawTokens = buildList {
		if (!category.isNullOrBlank()) {
			addAll(category.split(',', ';', '|'))
		}
		explicitLabels.forEach { label ->
			if (label.isNotBlank()) {
				addAll(label.split(',', ';', '|'))
			}
		}
	}
	rawTokens.forEach { token ->
		matchExplicitToken(token, result)
	}
	if (!title.isNullOrBlank()) {
		matchExplicitPatterns(title, result)
	}
	return result
}

private fun matchExplicitToken(token: String, target: MutableSet<String>) {
	val trimmed = token.trim().substringAfterLast('/')
	if (trimmed.isEmpty()) return
	val upper = trimmed.uppercase(Locale.ROOT).replace('-', '_').replace(' ', '_')
	if (upper in knownDietaryPreferenceNames) {
		target += upper
		return
	}
	matchExplicitPatterns(trimmed, target)
}

private fun matchExplicitPatterns(text: String, target: MutableSet<String>) {
	val decamelized = text.replace(Regex("([a-z])([A-Z])"), "$1 $2")
	for (pattern in explicitDietaryPatterns) {
		if (pattern.regex.containsMatchIn(decamelized)) {
			target += pattern.preferenceName
		}
	}
}

private fun expandExplicitOnlyPreferences(explicit: Set<String>): List<String> {
	val expanded = linkedSetOf<String>()
	expanded.addAll(explicit)
	if ("VEGAN" in expanded) {
		expanded += "VEGETARIAN"
		expanded += "PESCATARIAN"
		expanded += "DAIRY_FREE"
		expanded += "EGG_FREE"
		expanded += "SHELLFISH_FREE"
	} else if ("VEGETARIAN" in expanded) {
		expanded += "PESCATARIAN"
		expanded += "SHELLFISH_FREE"
	}
	if ("PALEO" in expanded) {
		expanded += "GLUTEN_FREE"
		expanded += "DAIRY_FREE"
	}
	return expanded.toList()
}

private fun buildScrapedIngredientProfile(
	title: String,
	ingredients: List<String>,
): ScrapedIngredientProfile = ScrapedIngredientProfile(
	hasMeat = recipeHasMatch(title, ingredients, meatRegex, meatSafeRegex, plantLineRegex),
	hasPork = recipeHasMatch(title, ingredients, porkRegex, meatSafeRegex, plantLineRegex),
	hasAlcohol = recipeHasMatch(title, ingredients, alcoholRegex, alcoholSafeRegex),
	hasRennetOrGelatin = recipeHasMatch(title, ingredients, rennetOrGelatinRegex, null, plantLineRegex),
	hasFish = recipeHasMatch(title, ingredients, fishRegex, fishSafeRegex, plantLineRegex),
	hasShellfish = recipeHasMatch(title, ingredients, shellfishRegex, shellfishSafeRegex, plantLineRegex),
	hasDairy = recipeHasMatch(title, ingredients, dairyRegex, dairySafeRegex, dairyLineExemptionRegex),
	hasEgg = recipeHasMatch(title, ingredients, eggRegex, eggSafeRegex, eggLineExemptionRegex),
	hasHoney = recipeHasMatch(title, ingredients, honeyRegex, honeySafeRegex, honeyLineExemptionRegex),
	hasGluten = recipeHasMatch(title, ingredients, glutenRegex, glutenSafeRegex, glutenLineExemptionRegex),
	hasNut = recipeHasMatch(title, ingredients, nutRegex, nutSafeRegex),
	hasAllium = recipeHasMatch(title, ingredients, alliumRegex, alliumSafeRegex),
	hasPaleoExcluded = recipeHasMatch(title, ingredients, paleoExcludedRegex, paleoSafeRegex),
	hasKetoExcluded = recipeHasMatch(title, ingredients, ketoExcludedRegex, ketoSafeRegex),
	hasLowFodmapExcluded = recipeHasMatch(title, ingredients, lowFodmapExcludedRegex, lowFodmapSafeRegex),
	hasKetoMacroSource = ingredients.any { ketoMacroSourceRegex.containsMatchIn(it) },
)

private fun classifyDietaryPreferences(
	profile: ScrapedIngredientProfile,
	explicit: Set<String>,
): List<String> {
	val isVegetarian = profile.isVegetarian(explicit)
	val isPescatarian = profile.isPescatarian(explicit, isVegetarian)
	val checks = listOf(
		"VEGAN" to profile.isVegan,
		"VEGETARIAN" to isVegetarian,
		"PESCATARIAN" to isPescatarian,
		"GLUTEN_FREE" to !profile.hasGluten,
		"DAIRY_FREE" to !profile.hasDairy,
		"NUT_FREE" to !profile.hasNut,
		"EGG_FREE" to !profile.hasEgg,
		"SHELLFISH_FREE" to !profile.hasShellfish,
		"ALLIUM_FREE" to !profile.hasAllium,
		"HALAL" to profile.isHalal(explicit),
		"KOSHER" to profile.isKosher(explicit),
		"LOW_FODMAP" to profile.isLowFodmap,
		"PALEO" to profile.isPaleo,
		"KETO" to profile.isKeto(explicit),
	)
	return checks.mapNotNull { (name, matches) -> name.takeIf { matches } }
}

private fun recipeHasMatch(
	title: String,
	ingredients: List<String>,
	excludedRegex: Regex,
	safeRegex: Regex? = null,
	lineExemptionRegex: Regex? = null,
): Boolean {
	val titleMatches = title.isNotBlank() && lineHasMatch(title, excludedRegex, safeRegex, lineExemptionRegex)
	return titleMatches || ingredients.any { lineHasMatch(it, excludedRegex, safeRegex, lineExemptionRegex) }
}

private fun lineHasMatch(
	text: String,
	excludedRegex: Regex,
	safeRegex: Regex? = null,
	lineExemptionRegex: Regex? = null,
): Boolean {
	if (lineExemptionRegex != null && lineExemptionRegex.containsMatchIn(text)) {
		return false
	}
	val cleaned = if (safeRegex != null) text.replace(safeRegex, " ") else text
	return excludedRegex.containsMatchIn(cleaned)
}
