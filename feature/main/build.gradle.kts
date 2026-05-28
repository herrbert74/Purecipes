plugins {
	id("convention.ui")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.main"
		withDeviceTestBuilder {
			sourceSetTreeName = "androidDeviceTest"
		}.configure {
			instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:analytics:data"))
				api(project(":feature:analytics:domain"))
				implementation(project(":feature:analytics:ui"))
				api(project(":feature:auth:data"))
				api(project(":feature:auth:domain"))
				implementation(project(":feature:auth:ui"))
				implementation(project(":feature:cooking:ui"))
				api(project(":feature:favorites:data"))
				api(project(":feature:favorites:domain"))
				implementation(project(":feature:favorites:ui"))
				api(project(":feature:newrecipe:data"))
				api(project(":feature:newrecipe:domain"))
				implementation(project(":feature:newrecipe:ui"))
				api(project(":feature:recipedetails:data"))
				api(project(":feature:recipedetails:domain"))
				implementation(project(":feature:recipedetails:ui"))
				api(project(":feature:search:data"))
				api(project(":feature:search:domain"))
				implementation(project(":feature:search:ui"))
				api(project(":feature:sharing:data"))
				api(project(":feature:sharing:domain"))
				implementation(project(":feature:sharing:ui"))
				api(project(":feature:measurement:data"))
				api(project(":feature:measurement:domain"))
				api(project(":feature:settings:data"))
				api(project(":feature:settings:domain"))
				api(project(":shared:data"))
				api(project(":shared:domain"))
				implementation(project(":feature:settings:ui"))
				implementation(libs.jetbrains.androidXNavigation3Ui)
				implementation(libs.kotlinx.serializationJson)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
			}
		}
		named("androidDeviceTest") {
			dependencies {
				implementation(libs.androidx.activityCompose)
				implementation(project(":shared:testfixtures"))
			}
		}
	}
}
