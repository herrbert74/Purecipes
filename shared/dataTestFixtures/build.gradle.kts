plugins {
	id("convention.kmp")
}

kotlin {
	android {
		namespace = "com.purecipes.shared.datatestfixtures"
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
