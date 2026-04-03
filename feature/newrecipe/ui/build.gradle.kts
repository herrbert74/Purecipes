plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.newrecipe.ui"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:analytics:domain"))
				api(project(":feature:newrecipe:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
			}
		}
	}
}
