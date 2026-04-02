import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmJsTargetDsl

plugins {
	id("convention.kmp")
	id("convention.compose")
	alias(libs.plugins.buildKonfig)
	alias(libs.plugins.kotlin.serialization)
	id("dev.zacsweers.metro")
}

private val supportedBuildTypes = setOf("debug", "staging", "release")

private fun Project.googleWebClientId(): String {
	return providers.gradleProperty("purecipes.googleWebClientId")
		.orElse(providers.gradleProperty("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
		.orNull
		.orEmpty()
}

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
		buildConfigField(STRING, "purecipesGoogleWebClientId", googleWebClientId())
	}
}

kotlin {
	android {
		namespace = "com.purecipes.umbrella"
		compilerOptions {
			jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
		}
	}

	targets.named<KotlinWasmJsTargetDsl>("wasmJs") {
		browser {
			commonWebpackConfig {
				outputFileName = "umbrella.js"
			}
		}
	}

	listOf("iosArm64", "iosSimulatorArm64").forEach { targetName ->
		targets.named<KotlinNativeTarget>(targetName) {
			binaries.framework {
				baseName = "umbrella"
				isStatic = true
				export(project(":feature:main"))
				export(project(":feature:auth:domain"))
				export(project(":feature:favorites:domain"))
				export(project(":feature:newrecipe:domain"))
				export(project(":feature:recipedetails:domain"))
				export(project(":feature:search:domain"))
			}
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:auth:domain"))
				api(project(":feature:auth:data"))
				api(project(":feature:main"))
				api(project(":feature:favorites:domain"))
				api(project(":feature:favorites:data"))
				api(project(":feature:newrecipe:domain"))
				api(project(":feature:newrecipe:data"))
				api(project(":feature:recipedetails:domain"))
				api(project(":feature:recipedetails:data"))
				api(project(":feature:search:domain"))
				api(project(":feature:search:data"))
				api(project(":shared:data"))

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
