plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.metro)
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
				api(project(":feature:search:domain"))
				implementation(project(":shared:data"))
				implementation(libs.kotlinResult.result)
			}
		}
		commonTest {
			dependencies {
				implementation(kotlin("test"))
				implementation(libs.kotlinx.coroutinesTest)
			}
		}
	}
}

android {
	namespace = "com.purecipes.feature.search.data"
	compileSdk = 36
	defaultConfig {
		minSdk = 24
	}
}
