plugins {
	id("convention.data")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.auth.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:auth:domain"))
					implementation(project(":shared:data"))
			}
		}
	}
}
