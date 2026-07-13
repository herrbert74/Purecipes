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

sourceSets {
	main {
		resources.srcDir(generatedBackendResourcesDir)
	}
}

val generateBackendRuntimeConfig by tasks.registering(WriteProperties::class) {
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
	classpath = sourceSets.main.get().runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.UnknownIngredientsReportKt")
	args = project.findProperty("report.output")?.toString()?.let { listOf("--output", it) }.orEmpty()
}

tasks.register<JavaExec>("reportRecipeVisibility") {
	group = "verification"
	description = "Reports recipe visibility buckets and source-domain breakdown"
	classpath = sourceSets.main.get().runtimeClasspath
	mainClass.set("app.purecipes.backend.tools.RecipeVisibilityAnalysisReportKt")
	args = project.findProperty("report.output")?.toString()?.let { listOf("--output", it) }.orEmpty()
}

tasks.register<JavaExec>("calculateRecipeNutrition") {
	group = "application"
	description = "Calculates and stores recipe nutrition from parsed ingredients"
	classpath = sourceSets.main.get().runtimeClasspath
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
	classpath = sourceSets.main.get().runtimeClasspath
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
