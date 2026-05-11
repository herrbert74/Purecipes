plugins {
	id("convention.kmp")
}

kotlin {
	android {
		namespace = "app.purecipes.shared.datatestfixtures"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":shared:data"))
				api(project(":shared:domain"))
			}
		}
	}
}
