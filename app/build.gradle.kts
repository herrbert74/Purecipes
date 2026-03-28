plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.ksp)
	id("org.jetbrains.kotlin.plugin.compose")
	id("dev.zacsweers.metro")
}

private fun Project.googleWebClientId(): String {
	return providers.gradleProperty("purecipes.googleWebClientId")
		.orElse(providers.gradleProperty("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
		.orNull
		.orEmpty()
}

private fun String.asBuildConfigString(): String {
	return buildString {
		append('"')
		for (character in this@asBuildConfigString) {
			if (character == '"' || character == '\\') {
				append('\\')
			}
			append(character)
		}
		append('"')
	}
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
		buildConfigField("String", "PURECIPES_GOOGLE_WEB_CLIENT_ID", googleWebClientId().asBuildConfigString())

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
		create("staging") {
			initWith(getByName("release"))
			isDebuggable = true
			signingConfig = signingConfigs.getByName("debug")
			matchingFallbacks += listOf("release")
		}
	}
	kotlin {
		jvmToolchain(21)
	}
	buildFeatures {
		buildConfig = true
		compose = true
	}
}

dependencies {
	implementation(project(":feature:auth:data"))
	implementation(project(":feature:main"))
	implementation(project(":feature:recipedetails:data"))
	implementation(project(":feature:search:data"))
	implementation(project(":shared:data"))
	implementation(project(":shared:ui"))
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
	implementation(libs.metro.runtime)
}
