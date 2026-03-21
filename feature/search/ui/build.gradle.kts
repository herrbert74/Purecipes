plugins {
	id("convention.kmp")
	alias(libs.plugins.androidKotlinMultiPlatformLibrary)
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
	android {
        namespace = "com.purecipes.feature.search.ui"
        compileSdk = 36
        minSdk = 24
    }

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
				implementation(project(":shared:ui"))
				implementation(libs.jetbrains.androidXLifecycleViewmodel)
				implementation(libs.jetbrains.androidXLifecycleViewmodelCompose)
				implementation(libs.jetbrains.composeFoundation)
				implementation(libs.jetbrains.composeMaterial3)
				implementation(libs.jetbrains.composeRuntime)
				implementation(libs.jetbrains.composeUi)
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.kotlinResult.result)
				implementation(libs.kotlinx.coroutinesCore)
			}
		}
		commonTest {
			dependencies {
				implementation(libs.kotlinx.coroutinesTest)
			}
		}
	}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
	compilerOptions.freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
}
