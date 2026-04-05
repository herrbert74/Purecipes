plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.favorites.ui"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:favorites:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
			}
		}
	}
}
