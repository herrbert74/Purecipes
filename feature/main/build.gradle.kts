plugins {
	id("convention.kmp")
	alias(libs.plugins.androidKotlinMultiPlatformLibrary)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
}

kotlin {
	android {
		namespace = "com.purecipes.feature.main"
		compileSdk = 36
		minSdk = 24
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:recipedetails:domain"))
				implementation(project(":feature:recipedetails:ui"))
				api(project(":feature:search:domain"))
				implementation(project(":feature:search:ui"))
				api(project(":shared:data"))
				implementation(project(":shared:ui"))
				implementation(libs.jetbrains.androidXLifecycleViewmodel)
				implementation(libs.jetbrains.androidXLifecycleViewmodelCompose)
				implementation(libs.jetbrains.composeFoundation)
				implementation(libs.jetbrains.composeMaterial3)
				implementation(libs.jetbrains.composeMaterialIconsExtended)
				implementation(libs.jetbrains.composeRuntime)
				implementation(libs.jetbrains.androidXNavigation3Ui)
				implementation(libs.kotlinx.coroutinesCore)
				implementation(libs.kotlinx.serializationJson)
			}
		}
		androidMain {
			dependencies {
				implementation(libs.androidx.activityCompose)
			}
		}
	}
}
