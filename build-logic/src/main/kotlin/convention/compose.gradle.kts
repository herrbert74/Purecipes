package convention

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
}

extensions.configure<KotlinMultiplatformExtension> {
	sourceSets {
		androidMain.dependencies {
			implementation(libs.androidx.composeFoundationLayout)
			implementation(libs.androidx.composeFoundation)
			implementation(libs.androidx.composeMaterial3)
			implementation(libs.androidx.composeMaterialIconsCore)
			api(libs.androidx.composeRuntime)
			implementation(libs.androidx.composeUiText)
			api(libs.androidx.composeUi)
			if (project.parent?.name == "shared" || project.parent?.name == "newrecipe") {
				implementation(libs.androidx.activityCompose)
			}
		}
		commonMain.dependencies {
			implementation(libs.jetbrains.androidXLifecycleViewmodelCompose)
			implementation(libs.jetbrains.composeFoundation)
			implementation(libs.jetbrains.composeMaterial3)
			implementation(libs.jetbrains.composeMaterialIconsExtended)
			implementation(libs.jetbrains.composeRuntime)
			implementation(libs.jetbrains.composeUi)
			implementation(libs.jetbrains.composeResources)
			implementation(libs.jetbrains.composeUiToolingPreview)
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
	compilerOptions.freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
	compilerOptions.freeCompilerArgs.add("-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi")
}

tasks.configureEach {
	if (name == "copyAndroidDeviceTestComposeResourcesToAndroidAssets" ||
		name == "prepareComposeResourcesTaskForAndroidDeviceTest"
	) {
		enabled = false
	}
}

dependencies {
	add("androidRuntimeClasspath", libs.jetbrains.composeUiTooling)
}
