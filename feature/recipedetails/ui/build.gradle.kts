plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.androidLibrary)
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
				implementation(project(":shared:ui"))
				implementation(libs.jetbrains.composeFoundation)
				implementation(libs.jetbrains.composeMaterial3)
				implementation(libs.jetbrains.composeRuntime)
				implementation(libs.jetbrains.composeUi)
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.kotlinResult.result)
			}
		}
		commonTest {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		androidInstrumentedTest {
			dependencies {
				implementation(libs.androidx.composeUiTestJunit4Android)
				implementation(libs.androidx.testEspresso.core)
				implementation(libs.androidx.testExtJUnit)
				implementation(libs.androidx.testRunner)
			}
		}
	}
}

android {
	namespace = "com.purecipes.feature.recipedetails.ui"
	compileSdk = 36
	defaultConfig {
		minSdk = 24
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
}

dependencies {
	debugImplementation(libs.androidx.composeUiTestManifestAndroid)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
	compilerOptions.freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
}
