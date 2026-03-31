import org.gradle.api.tasks.WriteProperties

plugins {
	kotlin("jvm")
	alias(libs.plugins.kotlin.serialization)
	application
	id("com.gradleup.shadow") version "9.4.1"
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
	implementation(libs.kotlinx.coroutinesCore)
	implementation(libs.kotlinx.serializationJson)
	implementation(project(":shared:domain"))

	implementation("io.ktor:ktor-server-core-jvm:${libs.versions.ktor.get()}")
	implementation("io.ktor:ktor-server-netty-jvm:${libs.versions.ktor.get()}")
	implementation("io.ktor:ktor-server-content-negotiation-jvm:${libs.versions.ktor.get()}")
	implementation("io.ktor:ktor-server-cors-jvm:${libs.versions.ktor.get()}")
	implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:${libs.versions.ktor.get()}")
	implementation("io.ktor:ktor-server-call-logging-jvm:${libs.versions.ktor.get()}")
	implementation("io.ktor:ktor-server-status-pages-jvm:${libs.versions.ktor.get()}")
	implementation("io.ktor:ktor-client-cio-jvm:${libs.versions.ktor.get()}")
	implementation("io.ktor:ktor-client-content-negotiation-jvm:${libs.versions.ktor.get()}")
	implementation("io.ktor:ktor-client-core-jvm:${libs.versions.ktor.get()}")
	implementation("io.ktor:ktor-client-logging-jvm:${libs.versions.ktor.get()}")

	implementation("com.zaxxer:HikariCP:7.0.2")
	implementation("org.postgresql:postgresql:42.7.10")

	testImplementation(kotlin("test"))
	testImplementation("io.ktor:ktor-server-test-host-jvm:${libs.versions.ktor.get()}")
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
