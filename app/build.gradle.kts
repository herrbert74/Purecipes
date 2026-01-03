plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.ksp)
	alias(libs.plugins.kotlin.composeCompiler)
}

android {
	namespace = "com.purecipes"
	compileSdk {
		version = release(36)
	}

	defaultConfig {
		applicationId = "com.purecipes"
		minSdk = 24
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	kotlin {
		jvmToolchain(21)
	}
	buildFeatures {
		compose = true
	}
}

dependencies {
	implementation(libs.androidx.core)
	implementation(libs.androidx.lifecycleRuntime)
	implementation(libs.androidx.activityCompose)
	implementation(platform(libs.androidx.composeBom))
	implementation(libs.androidx.composeUi)
	implementation(libs.androidx.composeUiGraphics)
	implementation(libs.androidx.composeUiToolingPreview)
	implementation(libs.androidx.composeMaterial3)
	implementation(libs.androidx.composeMaterialIconsCore)
	implementation(libs.androidx.composeMaterialIconsExtended)
}