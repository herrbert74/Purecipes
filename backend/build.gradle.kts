plugins {
	kotlin("jvm")
	alias(libs.plugins.kotlin.serialization)
	application
	id("com.gradleup.shadow") version "9.3.2"
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

	implementation("com.zaxxer:HikariCP:6.3.0")
	implementation("org.postgresql:postgresql:42.7.5")

	testImplementation(kotlin("test"))
	testImplementation("io.ktor:ktor-server-test-host-jvm:${libs.versions.ktor.get()}")
}

application {
	mainClass.set("com.purecipes.backend.MainKt")
}

tasks.shadowJar {
	archiveClassifier.set("")
	mergeServiceFiles()
}

tasks.jar {
	manifest {
		attributes["Main-Class"] = "com.purecipes.backend.MainKt"
	}
}
