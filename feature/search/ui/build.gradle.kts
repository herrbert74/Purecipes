plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.search.ui"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:analytics:domain"))
				api(project(":feature:search:domain"))
				api(project(":feature:settings:domain"))
				api(project(":shared:domain"))
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
