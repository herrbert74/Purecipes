plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
	applyDefaultHierarchyTemplate()
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}

	androidTarget()

	wasmJs {
		browser()
		binaries.executable()
	}

	iosArm64()
	iosSimulatorArm64()

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:recipedetails:domain"))
				implementation(project(":feature:recipedetails:ui"))
				api(project(":feature:search:domain"))
				implementation(project(":feature:search:ui"))
				api(project(":shared:data"))
				implementation(project(":shared:ui"))
				implementation(libs.jetbrains.composeFoundation)
				implementation(libs.jetbrains.composeMaterial3)
				implementation(libs.jetbrains.composeMaterialIconsExtended)
				implementation(libs.jetbrains.composeRuntime)
				implementation(libs.jetbrainsAndroidX.navigation3Ui)
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
				implementation(libs.androidx.activityCompose)
			}
		}
	}
}

android {
	namespace = "com.purecipes.feature.main"
	compileSdk = 36
	defaultConfig {
		minSdk = 24
	}
}
