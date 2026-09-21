import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmJsTargetDsl
import java.util.Properties

plugins {
	id("convention.kmp")
	id("convention.compose")
	alias(libs.plugins.buildKonfig)
	alias(libs.plugins.kotlin.serialization)
	id("dev.zacsweers.metro")
}

private val supportedBuildTypes = setOf("debug", "staging", "release")

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

private fun Project.mixpanelProjectToken(buildType: String = currentPurecipesBuildType()): String {
	return providers.gradleProperty("purecipes.mixpanelProjectToken.$buildType")
		.orElse(providers.gradleProperty("purecipes.mixpanelProjectToken"))
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

private fun Project.revenueCatApiKey(buildType: String = currentPurecipesBuildType()): String {
	return providers.gradleProperty("purecipes.revenueCatApiKey.ios")
		.orElse(providers.environmentVariable("PURECIPES_REVENUECAT_IOS_API_KEY"))
		.orElse(providers.gradleProperty("purecipes.revenueCatApiKey.$buildType"))
		.orElse(providers.gradleProperty("purecipes.revenueCatApiKey"))
		.orElse(providers.gradleProperty("PURECIPES_REVENUECAT_API_KEY"))
		.orElse(providers.environmentVariable("PURECIPES_REVENUECAT_API_KEY"))
		.orNull
		.orEmpty()
}

private fun Project.admobIosAppId(): String {
	return providers.gradleProperty("purecipes.adMobIosAppId")
		.orElse(providers.gradleProperty("PURECIPES_ADMOB_IOS_APP_ID"))
		.orElse(providers.environmentVariable("PURECIPES_ADMOB_IOS_APP_ID"))
		.orNull
		.orEmpty()
		.ifBlank { "ca-app-pub-3940256099942544~1458002511" }
}

private fun Project.admobIosBannerAdUnitId(): String {
	return providers.gradleProperty("purecipes.adMobIosBannerAdUnitId")
		.orElse(providers.gradleProperty("PURECIPES_ADMOB_IOS_BANNER_AD_UNIT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_ADMOB_IOS_BANNER_AD_UNIT_ID"))
		.orNull
		.orEmpty()
		.ifBlank { "ca-app-pub-3940256099942544/2934735716" }
}

private fun Project.admobIosInterstitialAdUnitId(): String {
	return providers.gradleProperty("purecipes.adMobIosInterstitialAdUnitId")
		.orElse(providers.gradleProperty("PURECIPES_ADMOB_IOS_INTERSTITIAL_AD_UNIT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_ADMOB_IOS_INTERSTITIAL_AD_UNIT_ID"))
		.orNull
		.orEmpty()
		.ifBlank { "ca-app-pub-3940256099942544/4411468910" }
}

private fun Project.currentPurecipesBuildType(): String {
	val requestedBuildType = androidBuildTypeFromTasks()
		?: providers.gradleProperty("purecipes.buildType").orNull
		?: System.getenv("PURECIPES_BUILD_TYPE")

	return requestedBuildType
		?.lowercase()
		?.takeIf { it in supportedBuildTypes }
		?: "debug"
}

private fun Project.purecipesDebugBackendHost(): String {
	providers.gradleProperty("purecipes.debugBackendHost").orNull
		?.takeIf { it.isNotBlank() }
		?.let { return it }

	val localPropertiesFile = rootProject.file("local.properties")
	return if (!localPropertiesFile.exists()) {
		""
	} else {
		val properties = Properties()
		localPropertiesFile.inputStream().use { properties.load(it) }
		properties.getProperty("purecipes.debugBackendHost").orEmpty()
	}
}

private fun Project.androidBuildTypeFromTasks(): String? {
	val taskRequests = gradle.startParameter.taskRequests.toString()
	val match = Regex("(?:assemble|bundle|install|compile|test|lint|connected)\\w*(Debug|Staging|Release)")
		.find(taskRequests)
		?: Regex("\\b(Debug|Staging|Release)\\b").find(taskRequests)

	return match?.groupValues?.last()?.lowercase()
}

buildkonfig {
	packageName = "app.purecipes.umbrella"

	defaultConfigs {
		buildConfigField(STRING, "purecipesBuildType", currentPurecipesBuildType())
		buildConfigField(STRING, "versionName", libs.versions.versionName.get())
		buildConfigField(STRING, "versionCode", libs.versions.versionCode.get())
		buildConfigField(STRING, "purecipesDebugBackendHost", purecipesDebugBackendHost())
		buildConfigField(STRING, "purecipesGoogleWebClientId", googleWebClientId())
		buildConfigField(STRING, "purecipesGaMeasurementId", gaMeasurementId())
		buildConfigField(STRING, "purecipesMixpanelProjectToken", mixpanelProjectToken())
		buildConfigField(STRING, "purecipesUsercentricsSettingsId", usercentricsSettingsId())
		buildConfigField(STRING, "purecipesRevenueCatApiKey", revenueCatApiKey())
		buildConfigField(STRING, "purecipesAdMobAppId", admobIosAppId())
		buildConfigField(STRING, "purecipesAdMobBannerAdUnitId", admobIosBannerAdUnitId())
		buildConfigField(STRING, "purecipesAdMobInterstitialAdUnitId", admobIosInterstitialAdUnitId())
	}
}

kotlin {
	android {
		namespace = "app.purecipes.umbrella"
		compilerOptions {
			jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
		}
	}

	targets.named<KotlinWasmJsTargetDsl>("wasmJs") {
		browser {
			commonWebpackConfig {
				outputFileName = "umbrella.js"
			}
		}
	}

	listOf("iosArm64", "iosSimulatorArm64").forEach { targetName ->
		targets.named<KotlinNativeTarget>(targetName) {
			binaries.framework {
				baseName = "umbrella"
				isStatic = true
				export(project(":feature:ads:domain"))
				export(project(":feature:analytics:domain"))
				export(project(":feature:sharing:domain"))
				export(project(":feature:main"))
				export(project(":feature:auth:domain"))
				export(project(":feature:featurerequests:domain"))
				export(project(":feature:library:domain"))
				export(project(":feature:newrecipe:domain"))
				export(project(":feature:onboarding:domain"))
				export(project(":feature:recipedetails:domain"))
				export(project(":feature:search:domain"))
				export(project(":feature:subscription:domain"))
				export(project(":shared:domain"))
				export(project(":shared:data"))
			}
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:ads:domain"))
				api(project(":feature:ads:data"))
				api(project(":feature:analytics:domain"))
				api(project(":feature:analytics:data"))
				api(project(":feature:sharing:domain"))
				api(project(":feature:sharing:data"))
				api(project(":feature:auth:domain"))
				api(project(":feature:auth:data"))
				api(project(":feature:main"))
				implementation(project(":feature:ads:ui"))
				implementation(project(":feature:analytics:ui"))
				implementation(project(":feature:auth:ui"))
				implementation(project(":feature:cooking:ui"))
				implementation(project(":feature:featurerequests:ui"))
				implementation(project(":feature:library:ui"))
				implementation(project(":feature:newrecipe:ui"))
				implementation(project(":feature:onboarding:ui"))
				implementation(project(":feature:recipedetails:ui"))
				implementation(project(":feature:search:ui"))
				implementation(project(":feature:settings:ui"))
				implementation(project(":feature:subscription:ui"))
				api(project(":feature:featurerequests:domain"))
				api(project(":feature:featurerequests:data"))
				api(project(":feature:library:domain"))
				api(project(":feature:library:data"))
				api(project(":feature:newrecipe:domain"))
				api(project(":feature:newrecipe:data"))
				api(project(":feature:onboarding:domain"))
				api(project(":feature:onboarding:data"))
				api(project(":feature:recipedetails:domain"))
				api(project(":feature:recipedetails:data"))
				api(project(":feature:search:domain"))
				api(project(":feature:search:data"))
				api(project(":feature:subscription:data"))
				api(project(":feature:subscription:domain"))
				api(project(":feature:measurement:data"))
				api(project(":feature:settings:data"))
				api(project(":shared:data"))
				api(project(":shared:domain"))

				implementation(libs.kotlinx.coroutinesCore)
				implementation(libs.kotlinx.serializationJson)
				implementation(libs.metrox.viewmodel)
			}
		}
		androidMain {
			dependencies {
				implementation(libs.androidx.core)
				implementation(libs.kotlinx.coroutinesAndroid)
			}
		}
		iosMain {
			dependencies {
				implementation(libs.crashkios.crashlytics)
				implementation(libs.gitlive.firebaseAuth)
				implementation(libs.ktor.clientCore)
				implementation(libs.ktor.clientDarwin)
				implementation(libs.ktor.clientContentNegotiation)
				implementation(libs.ktor.serializationKotlinxJson)
			}
		}
		// iosArm64Main
		// iosSimulatorArm64Main
		wasmJsMain {
			dependencies {
				implementation(libs.ktor.clientCore)
				implementation(libs.ktor.clientContentNegotiation)
				implementation(libs.ktor.serializationKotlinxJson)
			}
		}
	}
}

val syncIosVersion = tasks.register("syncIosVersion") {
	group = "ios"
	description = "Write MARKETING_VERSION and CURRENT_PROJECT_VERSION from the version catalog into Versions.xcconfig"
	val versionName = libs.versions.versionName.get()
	val versionCode = libs.versions.versionCode.get()
	val outputFile = rootProject.layout.projectDirectory.file("iosApp/PurecipesIOSApp/Config/Versions.xcconfig")
	inputs.property("versionName", versionName)
	inputs.property("versionCode", versionCode)
	doLast {
		val xcconfig = outputFile.asFile
		xcconfig.parentFile.mkdirs()
		xcconfig.writeText(
			"MARKETING_VERSION = $versionName\nCURRENT_PROJECT_VERSION = $versionCode\n",
		)
	}
}

tasks.configureEach {
	if (name == "embedAndSignAppleFrameworkForXcode") {
		dependsOn(syncIosVersion)
	}
}
