plugins {
	alias(libs.plugins.kotlin.jvm)
	application
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
	mainClass.set("com.purecipes.enrichment.EnrichmentRunnerKt")
}
