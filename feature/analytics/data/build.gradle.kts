plugins {
	id("convention.data")
	id("org.jetbrains.kotlin.native.cocoapods")
}

kotlin {
	cocoapods {
		version = "1.0"
		summary = "Purecipes analytics data"
		homepage = "https://github.com/zsoltbertalan/Purecipes"
		ios.deploymentTarget = "26.0"
		podfile = project.file("../../../iosApp/PurecipesIOSApp/Podfile")
		pod("FirebaseAnalytics")
		pod("Mixpanel-swift")
		pod("Usercentrics")
		pod("UsercentricsUI")
	}

	android {
		namespace = "com.purecipes.feature.analytics.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:analytics:domain"))
				implementation(libs.kotlinx.coroutinesCore)
			}
		}
		androidMain {
			dependencies {
				implementation(project.dependencies.platform(libs.firebaseBom))
				implementation(libs.firebaseAnalytics)
				implementation(libs.mixpanelAndroid)
				implementation(libs.usercentricsUi)
			}
		}
	}
}