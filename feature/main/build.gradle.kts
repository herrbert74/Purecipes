plugins {
	id("convention.ui")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "com.purecipes.feature.main"
	}

	sourceSets {
		commonMain {
			dependencies {
				implementation(project(":feature:cooking:ui"))
				api(project(":feature:recipedetails:domain"))
				implementation(project(":feature:recipedetails:ui"))
				api(project(":feature:search:domain"))
				implementation(project(":feature:search:ui"))
				api(project(":shared:data"))
				implementation(libs.jetbrains.androidXNavigation3Ui)
				implementation(libs.kotlinx.serializationJson)
			}
		}
	}
}
