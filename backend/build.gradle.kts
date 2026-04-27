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
	mainClass.set("com.purecipes.backend.MainKt")
	applicationDefaultJvmArgs = googleWebClientId().orNull
		?.takeIf { it.isNotBlank() }
		?.let { listOf("-Dpurecipes.googleWebClientId=$it") }
		.orEmpty()
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
		attributes["Main-Class"] = "com.purecipes.backend.MainKt"
	}
}

tasks.register<JavaExec>("reportUnknownIngredients") {
	group = "verification"
	description = "Reports recipe ingredients that do not match app ingredient options"
	classpath = sourceSets.main.get().runtimeClasspath
	mainClass.set("com.purecipes.backend.tools.UnknownIngredientsReportKt")
	args = project.findProperty("report.output")?.toString()?.let { listOf("--output", it) }.orEmpty()
}

tasks.register<JavaExec>("reportRecipeVisibility") {
	group = "verification"
	description = "Reports recipe visibility buckets and source-domain breakdown"
	classpath = sourceSets.main.get().runtimeClasspath
	mainClass.set("com.purecipes.backend.tools.RecipeVisibilityAnalysisReportKt")
	args = project.findProperty("report.output")?.toString()?.let { listOf("--output", it) }.orEmpty()
}
