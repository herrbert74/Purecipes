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
				api(project(":feature:analytics:domain"))
				implementation(project(":feature:analytics:ui"))
				api(project(":feature:auth:domain"))
				implementation(project(":feature:auth:ui"))
				implementation(project(":feature:cooking:ui"))
				api(project(":feature:favorites:domain"))
				implementation(project(":feature:favorites:ui"))
				api(project(":feature:newrecipe:domain"))
				implementation(project(":feature:newrecipe:ui"))
				api(project(":feature:recipedetails:domain"))
				implementation(project(":feature:recipedetails:ui"))
				api(project(":feature:search:domain"))
				implementation(project(":feature:search:ui"))
				api(project(":feature:settings:domain"))
				api(project(":shared:data"))
				api(project(":shared:domain"))
				implementation(project(":feature:settings:ui"))
				implementation(libs.jetbrains.androidXNavigation3Ui)
				implementation(libs.kotlinx.serializationJson)
			}
		}
	}
}
