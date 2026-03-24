plugins {
	id("convention.kmp")
	id("convention.compose")
	alias(libs.plugins.androidKotlinMultiPlatformLibrary)
}

kotlin {
	android {
		namespace = "com.purecipes.feature.search.ui"
		compileSdk = 36
		minSdk = 24
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:search:domain"))
				implementation(project(":shared:ui"))
				implementation(libs.jetbrains.androidXLifecycleViewmodel)
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
