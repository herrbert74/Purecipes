package convention

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
}

extensions.configure<KotlinMultiplatformExtension> {
	sourceSets {
		commonMain.dependencies {
			implementation(libs.jetbrains.androidXLifecycleViewmodelCompose)
			implementation(libs.jetbrains.composeFoundation)
			implementation(libs.jetbrains.composeMaterial3)
			implementation(libs.jetbrains.composeMaterialIconsExtended)
			implementation(libs.jetbrains.composeRuntime)
			implementation(libs.jetbrains.composeUi)
			implementation(libs.jetbrains.composeResources)
		}

		matching { it.name == "androidMain" }.all {
			dependencies {
				implementation(libs.androidx.activityCompose)
			}
		}

		matching { it.name == "androidDeviceTest" }.all {
			dependencies {
				implementation(libs.androidx.composeUiTestManifestAndroid)
				implementation(libs.androidx.composeUiTestJunit4Android)
				implementation(libs.androidx.testEspresso.core)
				implementation(libs.androidx.testExtJUnit)
				implementation(libs.androidx.testRunner)
			}
		}
	}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
	compilerOptions.freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
}

tasks.configureEach {
	if (name == "copyAndroidDeviceTestComposeResourcesToAndroidAssets" ||
		name == "prepareComposeResourcesTaskForAndroidDeviceTest"
	) {
		enabled = false
	}
}
