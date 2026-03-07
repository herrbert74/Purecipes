import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
	alias(libs.plugins.metro)
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
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach {
		it.binaries.framework {
			baseName = "umbrella"
			isStatic = true
			export(project(":feature:search"))
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:search"))
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
		// iosX64Main
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
