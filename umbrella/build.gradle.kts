import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.buildKonfig)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
	alias(libs.plugins.metro)
}

private val supportedBuildTypes = setOf("debug", "staging", "release")

private fun Project.currentPurecipesBuildType(): String {
	val requestedBuildType = androidBuildTypeFromTasks()
		?: providers.gradleProperty("purecipes.buildType").orNull
		?: System.getenv("PURECIPES_BUILD_TYPE")

	return requestedBuildType
		?.lowercase()
		?.takeIf { it in supportedBuildTypes }
		?: "debug"
}

private fun Project.androidBuildTypeFromTasks(): String? {
	val taskRequests = gradle.startParameter.taskRequests.toString()
	val match = Regex("(?:assemble|bundle|install|compile|test|lint|connected)\\w*(Debug|Staging|Release)")
		.find(taskRequests)
		?: Regex("\\b(Debug|Staging|Release)\\b").find(taskRequests)

	return match?.groupValues?.last()?.lowercase()
}

buildkonfig {
	packageName = "com.purecipes.umbrella"

	defaultConfigs {
		buildConfigField(STRING, "purecipesBuildType", currentPurecipesBuildType())
	}
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
	applyDefaultHierarchyTemplate()
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}
	androidTarget {
		compilerOptions {
			jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
		}
	}

	wasmJs {
		browser {
			commonWebpackConfig {
				outputFileName = "umbrella.js"
			}
		}
		binaries.executable()
	}

	listOf(
		iosArm64(),
		iosSimulatorArm64()
	).forEach {
		it.binaries.framework {
			baseName = "umbrella"
			isStatic = true
			export(project(":feature:main"))
			export(project(":feature:search:domain"))
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:main"))
				api(project(":feature:search:domain"))
				implementation(project(":feature:search:data"))
				api(project(":shared:data"))
				implementation(libs.jetbrains.composeFoundation)
				implementation(libs.jetbrains.composeMaterial3)
				implementation(libs.jetbrains.composeMaterialIconsExtended)
				implementation(libs.jetbrains.composeRuntime)

				implementation(libs.kotlinx.coroutinesCore)
				implementation(libs.kotlinx.serializationJson)
			}
		}
		commonTest {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		androidMain {
			dependencies {
				implementation(libs.androidx.core)
				implementation(libs.kotlinx.coroutinesAndroid)
			}
		}
		iosMain {
			dependencies {
				implementation(libs.ktor.clientCore)
				implementation(libs.ktor.clientDarwin)
				implementation(libs.ktor.clientContentNegotiation)
				implementation(libs.ktor.serializationKotlinxJson)
			}
		}
		// iosArm64Main
		// iosSimulatorArm64Main
		wasmJsMain {
			dependencies {
				implementation(libs.ktor.clientCore)
				implementation(libs.ktor.clientContentNegotiation)
				implementation(libs.ktor.serializationKotlinxJson)
			}
		}
	}
}

android {
	namespace = "com.purecipes.umbrella"
	compileSdk = 36
	defaultConfig {
		minSdk = 24
	}
}
