@file:Suppress("DEPRECATION")

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
	alias(libs.plugins.ksp)
	alias(libs.plugins.metro)
//	alias(libs.plugins.ktorfit)
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

	iosX64()
	iosArm64()
	iosSimulatorArm64()

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(compose.runtime)
				implementation(compose.foundation)
				implementation(compose.material3)
				implementation(compose.ui)
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.kotlinResult.result)
				implementation(libs.kotlinx.coroutinesCore)
				implementation(libs.kotlinx.serializationJson)
				implementation(libs.ktor.clientCore)
				implementation(libs.ktor.clientContentNegotiation)
				implementation(libs.ktor.serializationKotlinxJson)
				implementation(libs.ktorfit.lib)
				implementation(libs.ktorfit.annotations)
			}
		}
		val commonTest by getting {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		val androidMain by getting {
			dependencies {
				implementation(project(":base:kotlin"))
				implementation(libs.ktor.clientOkhttp)
			}
		}
		val iosMain by getting {
			dependencies {
				implementation(libs.ktor.clientDarwin)
			}
		}
//		val iosX64Main by getting
//		val iosArm64Main by getting
//		val iosSimulatorArm64Main by getting
		val wasmJsMain by getting {
			dependencies {
				implementation(libs.ktor.clientJs)
			}
		}
	}
}

dependencies {
	add("kspCommonMainMetadata", libs.ktorfit.ksp)
	add("kspAndroid", libs.ktorfit.ksp)
	add("kspIosX64", libs.ktorfit.ksp)
	add("kspIosArm64", libs.ktorfit.ksp)
	add("kspIosSimulatorArm64", libs.ktorfit.ksp)
	add("kspWasmJs", libs.ktorfit.ksp)
}

android {
	namespace = "com.purecipes.feature.search"
	compileSdk = 36
	defaultConfig {
		minSdk = 24
	}
}
