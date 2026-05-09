plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.googleServices)
	alias(libs.plugins.crashlytics)
	alias(libs.plugins.ksp)
	id("org.jetbrains.kotlin.plugin.compose")
	id("dev.zacsweers.metro")
}

android {
	namespace = "com.purecipes"
	compileSdk {
		version = release(36)
	}

	defaultConfig {
		applicationId = "app.purecipes"
		minSdk = 24
		targetSdk = libs.versions.targetSdkVersion.get().toInt()
		versionCode = libs.versions.versionCode.get().toInt()
		versionName = libs.versions.versionName.get()
		buildConfigField("String", "PURECIPES_GOOGLE_WEB_CLIENT_ID", googleWebClientId().asBuildConfigString())
		buildConfigField("String", "PURECIPES_GA_MEASUREMENT_ID", gaMeasurementId().asBuildConfigString())
		buildConfigField("String", "PURECIPES_MIXPANEL_PROJECT_TOKEN", mixpanelProjectToken().asBuildConfigString())
		buildConfigField("String", "PURECIPES_USERCENTRICS_SETTINGS_ID", usercentricsSettingsId().asBuildConfigString())

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	signingConfigs {
		create("release") {
			releaseSigningStoreFile().takeIf { it.isNotBlank() }?.let {
				storeFile = file(it)
			}
			storePassword = releaseSigningStorePassword()
			keyAlias = releaseSigningKeyAlias()
			keyPassword = releaseSigningKeyPassword()
		}
	}

	buildTypes {
		debug {
			applicationIdSuffix = ".debug"
		}
		release {
			isMinifyEnabled = false
			lint.checkReleaseBuilds = false
			signingConfig = signingConfigs.getByName("release")
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
		create("staging") {
			initWith(getByName("release"))
			applicationIdSuffix = ".staging"
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
	implementation(project(":feature:analytics:data"))
	implementation(project(":feature:main"))
	implementation(project(":feature:favorites:data"))
	implementation(project(":feature:newrecipe:data"))
	implementation(project(":feature:recipedetails:data"))
	implementation(project(":feature:search:data"))
	implementation(project(":feature:settings:data"))
	implementation(project(":shared:data"))
	implementation(project(":shared:ui"))
	implementation(project.dependencies.platform(libs.firebaseBom))
	implementation(libs.firebaseCrashlytics)
	implementation(libs.androidx.core)
	implementation(libs.androidx.lifecycleRuntime)
	implementation(libs.androidx.activityCompose)
	implementation(libs.kmpauth.facebook)
	implementation(libs.kmpnotifier)
	implementation(platform(libs.androidx.composeBom))
	implementation(libs.metro.runtime)
}

private fun Project.releaseSigningStoreFile(): String {
	return providers.gradleProperty("purecipes.storeFile")
		.orElse(providers.gradleProperty("purecipes.signing.storeFile"))
		.orElse(providers.gradleProperty("PURECIPES_SIGNING_STORE_FILE"))
		.orElse(providers.environmentVariable("PURECIPES_SIGNING_STORE_FILE"))
		.orNull
		.orEmpty()
}

private fun Project.releaseSigningStorePassword(): String {
	return providers.gradleProperty("purecipes.storePassword")
		.orElse(providers.gradleProperty("purecipes.signing.storePassword"))
		.orElse(providers.gradleProperty("PURECIPES_SIGNING_STORE_PASSWORD"))
		.orElse(providers.environmentVariable("PURECIPES_SIGNING_STORE_PASSWORD"))
		.orNull
		.orEmpty()
}

private fun Project.releaseSigningKeyAlias(): String {
	return providers.gradleProperty("purecipes.keyAlias")
		.orElse(providers.gradleProperty("purecipes.signing.keyAlias"))
		.orElse(providers.gradleProperty("PURECIPES_SIGNING_KEY_ALIAS"))
		.orElse(providers.environmentVariable("PURECIPES_SIGNING_KEY_ALIAS"))
		.orNull
		.orEmpty()
}

private fun Project.releaseSigningKeyPassword(): String {
	return providers.gradleProperty("purecipes.keyPassword")
		.orElse(providers.gradleProperty("purecipes.signing.keyPassword"))
		.orElse(providers.gradleProperty("PURECIPES_SIGNING_KEY_PASSWORD"))
		.orElse(providers.environmentVariable("PURECIPES_SIGNING_KEY_PASSWORD"))
		.orNull
		.orEmpty()
}

private fun Project.googleWebClientId(): String {
	return providers.gradleProperty("purecipes.googleWebClientId")
		.orElse(providers.gradleProperty("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
		.orNull
		.orEmpty()
}

private fun Project.gaMeasurementId(): String {
	return providers.gradleProperty("purecipes.gaMeasurementId")
		.orElse(providers.gradleProperty("PURECIPES_GA_MEASUREMENT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_GA_MEASUREMENT_ID"))
		.orNull
		.orEmpty()
}

private fun Project.mixpanelProjectToken(): String {
	return providers.gradleProperty("purecipes.mixpanelProjectToken")
		.orElse(providers.gradleProperty("PURECIPES_MIXPANEL_PROJECT_TOKEN"))
		.orElse(providers.environmentVariable("PURECIPES_MIXPANEL_PROJECT_TOKEN"))
		.orNull
		.orEmpty()
}

private fun Project.usercentricsSettingsId(): String {
	return providers.gradleProperty("purecipes.usercentricsSettingsId")
		.orElse(providers.gradleProperty("PURECIPES_USERCENTRICS_SETTINGS_ID"))
		.orElse(providers.environmentVariable("PURECIPES_USERCENTRICS_SETTINGS_ID"))
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
