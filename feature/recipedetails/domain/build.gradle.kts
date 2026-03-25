plugins {
	id("convention.kmp")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.recipedetails.domain"
		compileSdk = 36
		minSdk = 24
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":base:kotlin"))
				api(project(":shared:domain"))
			}
		}
	}
}
