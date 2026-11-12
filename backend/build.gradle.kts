plugins {
	kotlin("jvm")
	alias(libs.plugins.kotlin.serialization)
	application
	alias(libs.plugins.shadow)
}

private fun Project.googleWebClientId() = providers.gradleProperty("purecipes.googleWebClientId")
	.orElse(providers.gradleProperty("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
	.orElse(providers.environmentVariable("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
	.orElse("")

private fun Project.firebaseProjectId(buildType: String): String {
	val buildTypeSpecific = providers.gradleProperty("purecipes.firebaseProjectId.$buildType")
		.orNull
		?.takeIf { it.isNotBlank() }
	val legacy = if (buildType == "debug") {
		null
	} else {
		providers.gradleProperty("purecipes.firebaseProjectId")
			.orElse(providers.gradleProperty("PURECIPES_FIREBASE_PROJECT_ID"))
			.orElse(providers.environmentVariable("PURECIPES_FIREBASE_PROJECT_ID"))
			.orNull
			?.takeIf { it.isNotBlank() }
	}
	return buildTypeSpecific ?: legacy ?: defaultFirebaseProjectId(buildType)
}

private fun defaultFirebaseProjectId(buildType: String): String {
	return when (buildType) {
		"debug" -> "purecipes-debug"
		else -> "purecipes-50e5c"
	}
}

private fun Project.firebaseProjectNumber(buildType: String): String {
	val buildTypeSpecific = providers.gradleProperty("purecipes.firebaseProjectNumber.$buildType")
		.orNull
		?.takeIf { it.isNotBlank() }
	val legacy = if (buildType == "debug") {
		null
	} else {
		providers.gradleProperty("purecipes.firebaseProjectNumber")
			.orElse(providers.gradleProperty("PURECIPES_FIREBASE_PROJECT_NUMBER"))
			.orElse(providers.environmentVariable("PURECIPES_FIREBASE_PROJECT_NUMBER"))
			.orNull
			?.takeIf { it.isNotBlank() }
	}
	return buildTypeSpecific ?: legacy ?: defaultFirebaseProjectNumber(buildType)
}

private fun defaultFirebaseProjectNumber(buildType: String): String {
	return when (buildType) {
		"debug" -> "740437012648"
		else -> "922845075790"
	}
}

private fun Project.localFirebaseProjectId(): String {
	return listOf("debug", "release", "staging")
		.map { firebaseProjectId(it) }
		.distinct()
		.joinToString(",")
}

private fun Project.localFirebaseProjectNumber(): String {
	return listOf("debug", "release", "staging")
		.map { firebaseProjectNumber(it) }
		.distinct()
		.joinToString(",")
}

val generatedBackendResourcesDir = layout.buildDirectory.dir("generated/resources/backend")

val toolsSourceSet = sourceSets.create("tools") {
	kotlin.srcDir("src/tools/kotlin")
}

sourceSets {
	main {
		kotlin.srcDir(rootProject.file("scripts/scraping"))
		kotlin.exclude("**/*.kts")
		resources.srcDir(generatedBackendResourcesDir)
	}
}

val generateBackendRuntimeConfig = tasks.register<WriteProperties>("generateBackendRuntimeConfig") {
	description = "Writes backend runtime config properties into generated resources"
	val outputFile = generatedBackendResourcesDir.map { it.file("purecipes-backend.properties").asFile }
	destinationFile = outputFile.get()
	encoding = "UTF-8"
	property("purecipes.googleWebClientId", googleWebClientId())
	property("purecipes.firebaseProjectId", firebaseProjectId("release"))
	property("purecipes.firebaseProjectNumber", firebaseProjectNumber("release"))
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}
	target.compilations.getByName("tools").associateWith(target.compilations.getByName("main"))
	target.compilations.getByName("test").associateWith(target.compilations.getByName("tools"))
}

dependencies {
	implementation(project(":shared:domain"))

	implementation(libs.hikariCp)
	implementation(libs.kotlinx.coroutinesCore)
	implementation(libs.kotlinx.serializationJson)
	implementation(libs.ktor.clientCio)
	implementation(libs.ktor.clientContentNegotiation)
	implementation(libs.ktor.clientCore)
	implementation(libs.ktor.clientLogging)
	implementation(libs.ktor.serializationKotlinxJson)

	implementation(libs.ktor.serverCallLogging)
	implementation(libs.ktor.serverContentNegotiation)
	implementation(libs.ktor.serverCore)
	implementation(libs.ktor.serverCors)
	implementation(libs.ktor.serverNetty)
	implementation(libs.ktor.serverStatusPages)

	implementation(libs.postgresql)
	"toolsImplementation"(libs.tensorflowJava)

	testImplementation(kotlin("test"))
	testImplementation(libs.h2)
	testImplementation(libs.kotest.assertionsCore)
	testImplementation(libs.ktor.serverTestHost)
	testImplementation(libs.testcontainers)
	testImplementation(libs.testcontainers.postgresql)
}

application {
	mainClass.set("app.purecipes.backend.MainKt")
	applicationDefaultJvmArgs = buildList {
		googleWebClientId().orNull
			?.takeIf { it.isNotBlank() }
			?.let { add("-Dpurecipes.googleWebClientId=$it") }
		localFirebaseProjectId()
			.takeIf { it.isNotBlank() }
			?.let { add("-Dpurecipes.firebaseProjectId=$it") }
		localFirebaseProjectNumber()
			.takeIf { it.isNotBlank() }
			?.let { add("-Dpurecipes.firebaseProjectNumber=$it") }
	}
}

tasks.processResources {
	dependsOn(generateBackendRuntimeConfig)
}

tasks.shadowJar {
	archiveClassifier.set("")
	mergeServiceFiles()
}

tasks.jar {
	archiveClassifier.set("original")
	manifest {
		attributes["Main-Class"] = "app.purecipes.backend.MainKt"
	}
}

tasks.register("jvmTest") {
	group = "verification"
	description = "Runs JVM unit tests."
	dependsOn(tasks.test)
}

tasks.register<JavaExec>("reportUnknownIngredients") {
	group = "verification"
	description = "Reports recipe ingredients that do not match app ingredient options"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.UnknownIngredientsReportKt")
	args = project.findProperty("report.output")?.toString()?.let { listOf("--output", it) }.orEmpty()
}

tasks.register<JavaExec>("reportRecipeVisibility") {
	group = "verification"
	description = "Reports recipe visibility buckets and source-domain breakdown"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.RecipeVisibilityAnalysisReportKt")
	args = project.findProperty("report.output")?.toString()?.let { listOf("--output", it) }.orEmpty()
}

tasks.register<JavaExec>("reportIngredientFoodMatches") {
	group = "verification"
	description = "Reports ingredient lines missing food-table matches or gram weights"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.IngredientFoodMatchReportKt")
	val extraArgs = buildList {
		project.findProperty("report.output")?.toString()?.let {
			add("--output")
			add(it)
		}
		if (project.findProperty("report.includeScraped")?.toString() == "true") {
			add("--include-scraped")
		}
	}
	args = extraArgs
}

tasks.register<JavaExec>("snapshotIngredients") {
	group = "verification"
	description = "Exports names.tsv and lines.tsv ingredient snapshots from the database"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.IngredientSnapshotMainKt")
	val snapshotDir = project.findProperty("snapshot.dir")?.toString().orEmpty()
	val snapshotMode = project.findProperty("snapshot.mode")?.toString().orEmpty().ifBlank { "write" }
	val extraArgs = buildList {
		add("--mode=$snapshotMode")
		if (snapshotDir.isNotBlank()) {
			add("--dir=$snapshotDir")
		}
		if (project.findProperty("snapshot.failOnDiff")?.toString() == "true") {
			add("--fail-on-diff")
		}
		if (project.findProperty("snapshot.seedPins")?.toString() == "true") {
			add("--seed-pins")
		}
	}
	args = extraArgs
}

tasks.register<JavaExec>("diffIngredientSnapshot") {
	group = "verification"
	description = "Diffs the database ingredient snapshot against committed names.tsv and lines.tsv"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.IngredientSnapshotMainKt")
	val snapshotDir = project.findProperty("snapshot.dir")?.toString().orEmpty()
	val failOnDiff = project.findProperty("snapshot.failOnDiff")?.toString() != "false"
	val extraArgs = buildList {
		add("--mode=diff")
		if (snapshotDir.isNotBlank()) {
			add("--dir=$snapshotDir")
		}
		if (failOnDiff) {
			add("--fail-on-diff")
		}
		if (project.findProperty("snapshot.seedPins")?.toString() == "true") {
			add("--seed-pins")
		}
	}
	args = extraArgs
}

tasks.register<JavaExec>("suggestIngredientMatches") {
	group = "verification"
	description = "Generates offline lexical/trigram and optional local ML-embedding suggestions into suggestions.tsv"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.IngredientSuggestionMainKt")
	val suggestDir = project.findProperty("suggest.dir")?.toString().orEmpty()
	val suggestScope = project.findProperty("suggest.scope")?.toString().orEmpty()
	val suggestQuery = project.findProperty("suggest.query")?.toString().orEmpty()
	val suggestLimit = project.findProperty("suggest.limit")?.toString().orEmpty()
	val modelPath = project.findProperty("suggest.modelPath")?.toString().orEmpty()
		.ifBlank {
			val rootModel = rootProject.file(".use_v4")
			if (rootModel.isDirectory) rootModel.absolutePath else ""
		}
	val extraArgs = buildList {
		if (suggestDir.isNotBlank()) {
			add("--dir=$suggestDir")
		}
		if (suggestScope.isNotBlank()) {
			add("--scope=$suggestScope")
		}
		if (suggestQuery.isNotBlank()) {
			add("--query=$suggestQuery")
		}
		if (suggestLimit.isNotBlank()) {
			add("--limit=$suggestLimit")
		}
		if (modelPath.isNotBlank()) {
			add("--model-path=$modelPath")
		}
		if (project.findProperty("suggest.accept")?.toString() == "true") {
			add("--accept")
		}
	}
	args = extraArgs
}

tasks.register<JavaExec>("rebuildIngredientData") {
	group = "application"
	description = "Recomputes scraped ingredient groups, lines, measurements, and nutrition from DB and JSON archive"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.RebuildIngredientDataMainKt")
	val jsonDir = project.findProperty("rebuild.jsonDir")?.toString().orEmpty()
	val restoreLinesTsv = project.findProperty("rebuild.restoreLinesTsv")?.toString().orEmpty()
	val extraArgs = buildList {
		if (jsonDir.isNotBlank()) {
			add("--json-dir=$jsonDir")
		}
		if (restoreLinesTsv.isNotBlank()) {
			add("--restore-lines-tsv=$restoreLinesTsv")
		}
		if (project.findProperty("rebuild.fromJson")?.toString() == "true") {
			add("--from-json")
		}
		if (project.findProperty("rebuild.noSyncJson")?.toString() == "true") {
			add("--no-sync-json")
		}
		if (project.findProperty("rebuild.noJson")?.toString() == "true") {
			add("--no-json")
		}
		if (project.findProperty("rebuild.onlyChangedLines")?.toString() == "true") {
			add("--only-changed-lines")
		}
	}
	args = extraArgs
}

tasks.register<JavaExec>("calculateRecipeNutrition") {
	group = "application"
	description = "Calculates and stores recipe nutrition from parsed ingredients"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.CalculateRecipeNutritionMainKt")
	val recipeId = project.findProperty("nutrition.recipeId")?.toString().orEmpty()
	val recipeIds = project.findProperty("nutrition.recipeIds")?.toString().orEmpty()
	val extraArgs = buildList {
		if (recipeId.isNotBlank()) {
			add("--recipe-id=$recipeId")
		} else if (recipeIds.isNotBlank()) {
			add("--recipe-ids=$recipeIds")
		}
		if (project.findProperty("nutrition.allRecipes")?.toString() == "true") {
			add("--all-recipes")
		}
		if (project.findProperty("nutrition.verbose")?.toString() == "true") {
			add("--verbose")
		}
		if (project.findProperty("nutrition.reportUnmatched")?.toString() == "true") {
			add("--report-unmatched")
		}
	}
	args = extraArgs
}

tasks.register<JavaExec>("importNutritionSeed") {
	group = "application"
	description = "Imports USDA FoodData Central foundation foods into nutrition tables"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.NutritionSeedImporterMainKt")
	val fdcJsonPath = project.findProperty("nutrition.fdcJson")?.toString().orEmpty()
	val extraArgs = buildList {
		if (fdcJsonPath.isNotBlank()) {
			add("--fdc-json=$fdcJsonPath")
		}
		if (project.findProperty("nutrition.dryRun")?.toString() == "true") {
			add("--dry-run")
		}
		if (project.findProperty("nutrition.replace")?.toString() == "true") {
			add("--replace")
		}
		if (project.findProperty("nutrition.skipAliases")?.toString() == "true") {
			add("--skip-aliases")
		}
	}
	args = extraArgs
}

tasks.register<JavaExec>("syncNutritionCatalogue") {
	group = "application"
	description = "Syncs nutrition_foods, nutrition_food_aliases, and nutrition_food_measures from source to target DB"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.SyncNutritionCatalogueMainKt")
	val sourceUrl = project.findProperty("nutritionSync.sourceUrl")?.toString().orEmpty()
	val sourceUser = project.findProperty("nutritionSync.sourceUser")?.toString().orEmpty()
	val sourcePassword = project.findProperty("nutritionSync.sourcePassword")?.toString().orEmpty()
	val extraArgs = buildList {
		if (sourceUrl.isNotBlank()) {
			add("--source-url=$sourceUrl")
		}
		if (sourceUser.isNotBlank()) {
			add("--source-user=$sourceUser")
		}
		if (sourcePassword.isNotBlank()) {
			add("--source-password=$sourcePassword")
		}
		if (project.findProperty("nutritionSync.dryRun")?.toString() == "true") {
			add("--dry-run")
		}
	}
	args = extraArgs
}

tasks.register<JavaExec>("enrichRecipes") {
	group = "application"
	description =
		"Enriches missing recipe attributes (cuisine, meal_type, difficulty, cooking_method, dietary) via USE v4"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.EnrichRecipesMainKt")
	val modelPath = project.findProperty("enrich.modelPath")?.toString().orEmpty()
		.ifBlank {
			System.getenv("USE_MODEL_PATH")?.takeIf(String::isNotBlank)
				?: rootProject.file(".use_v4").takeIf { it.isDirectory }?.absolutePath.orEmpty()
		}
	val extraArgs = buildList {
		if (modelPath.isNotBlank()) {
			add("--model-path=$modelPath")
		}
	}
	args = extraArgs
}

tasks.register<JavaExec>("deleteAccount") {
	group = "application"
	description = "Deletes a user account for a verified email deletion request, dry run unless deleteAccount" +
		".execute=true"
	classpath = toolsSourceSet.runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.DeleteAccountMainKt")
	val userId = project.findProperty("deleteAccount.userId")?.toString().orEmpty()
	val email = project.findProperty("deleteAccount.email")?.toString().orEmpty()
	val provider = project.findProperty("deleteAccount.provider")?.toString().orEmpty()
	val extraArgs = buildList {
		if (userId.isNotBlank()) {
			add("--user-id=$userId")
		}
		if (email.isNotBlank()) {
			add("--email=$email")
		}
		if (provider.isNotBlank()) {
			add("--provider=$provider")
		}
		if (project.findProperty("deleteAccount.execute")?.toString() == "true") {
			add("--execute")
		}
	}
	args = extraArgs
}
