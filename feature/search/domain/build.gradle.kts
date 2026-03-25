plugins {
	id("convention.kmp")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.search.domain"
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
