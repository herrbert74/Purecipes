import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("convention.kmp")
	alias(libs.plugins.androidKotlinMultiPlatformLibrary)
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
	android {
        namespace = "com.purecipes.umbrella"
        compileSdk = 36
        minSdk = 24
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(libs.versions.jdk.get()))
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
			export(project(":feature:recipedetails:domain"))
			export(project(":feature:search:domain"))
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:main"))
				api(project(":feature:recipedetails:domain"))
				api(project(":feature:recipedetails:data"))
				api(project(":feature:search:domain"))
				api(project(":feature:search:data"))
				api(project(":shared:data"))
				implementation(libs.jetbrains.composeFoundation)
				implementation(libs.jetbrains.composeMaterial3)
				implementation(libs.jetbrains.composeMaterialIconsExtended)
				implementation(libs.jetbrains.composeRuntime)

				implementation(libs.kotlinx.coroutinesCore)
				implementation(libs.kotlinx.serializationJson)
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

configurations.configureEach {
	if (name == "COMPOSE_SKIKO_JS_WASM_RUNTIME") {
		resolutionStrategy.eachDependency {
			if (requested.group == "org.jetbrains.skiko") {
				useVersion(libs.versions.skiko.get())
			}
		}
	}
}
