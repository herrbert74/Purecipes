plugins {
	alias(libs.plugins.kotlin.jvm)
	application
}

private fun Project.runtimeConfig(name: String) = providers.gradleProperty(name)
	.orElse(providers.environmentVariable(name))
	.orElse("")

private fun Project.runtimePathConfig(name: String) = runtimeConfig(name).map { value ->
	if (value.isBlank()) {
		value
	} else {
		rootProject.file(value).absolutePath
	}
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}
}

dependencies {
	implementation(project(":shared:domain"))
	implementation(libs.kotlinx.serializationJson)
	implementation(libs.postgresql)
	implementation(libs.tensorflowJava)
}

application {
	mainClass.set("app.purecipes.enrichment.EnrichmentRunnerKt")
	applicationDefaultJvmArgs = buildList {
		runtimePathConfig("USE_MODEL_PATH").orNull
			?.takeIf { it.isNotBlank() }
			?.let { add("-DUSE_MODEL_PATH=$it") }
		runtimeConfig("PURECIPES_DB_URL").orNull
			?.takeIf { it.isNotBlank() }
			?.let { add("-DPURECIPES_DB_URL=$it") }
		runtimeConfig("PURECIPES_DB_USER").orNull
			?.takeIf { it.isNotBlank() }
			?.let { add("-DPURECIPES_DB_USER=$it") }
		runtimeConfig("PURECIPES_DB_PASSWORD").orNull
			?.takeIf { it.isNotBlank() }
			?.let { add("-DPURECIPES_DB_PASSWORD=$it") }
	}
}
