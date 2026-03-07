plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.ksp)
	alias(libs.plugins.ktorfit)
	alias(libs.plugins.metro)
}

/**
 * Temporary fix for Ktorfit KSP issue, which is still not resolved in Ktorfit 2.7.2
 * https://github.com/Foso/Ktorfit/issues/1010
 */
ktorfit {
	compilerPluginVersion.set("2.3.3")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
	applyDefaultHierarchyTemplate()
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}

	androidTarget {
		// publishLibraryVariants("release")
	}

	wasmJs {
		browser()
		binaries.executable()
	}

	iosX64()
	iosArm64()
	iosSimulatorArm64()

	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlinx.serializationJson)
				implementation(libs.ktor.clientCore)
				implementation(libs.ktor.clientContentNegotiation)
				implementation(libs.ktor.serializationKotlinxJson)
				implementation(libs.ktorfit.lib)
				implementation(libs.ktorfit.annotations)
			}
		}
		commonTest {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		androidMain {
			dependencies {
				implementation(project(":base:kotlin"))
				implementation(libs.ktor.clientOkhttp)
			}
		}
		iosMain {
			dependencies {
				implementation(libs.ktor.clientDarwin)
			}
		}
		wasmJsMain {
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
	namespace = "com.purecipes.shared.data"
	compileSdk = 36

	defaultConfig {
		minSdk = 24
	}
}
