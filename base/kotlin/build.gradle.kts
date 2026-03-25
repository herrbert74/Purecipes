plugins {
	id("convention.kmp")
	id("dev.zacsweers.metro")
}

kotlin {
	android {
		namespace = "com.purecipes.base.kotlin"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(libs.kotlinResult.result)
				api(libs.kotlinx.coroutinesCore)
				api(libs.metro.runtime)
			}
		}
	}
}
