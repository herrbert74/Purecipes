plugins {
	id("convention.kmp")
}

kotlin {
	android {
		namespace = "app.purecipes.shared.testfixtures"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":base:kotlin"))
				api(project(":feature:analytics:domain"))
				api(project(":feature:auth:domain"))
				api(project(":feature:favorites:domain"))
				api(project(":feature:newrecipe:domain"))
				api(project(":feature:recipedetails:domain"))
				api(project(":feature:search:domain"))
				api(project(":feature:settings:domain"))
				api(project(":shared:domain"))
				implementation(libs.kotlinResult.result)
				implementation(libs.kotlinx.coroutinesCore)
			}
		}
	}
}
