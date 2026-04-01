plugins {
	id("convention.data")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "com.purecipes.feature.newrecipe.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:newrecipe:domain"))
					implementation(libs.kotlinResult.result)
					implementation(libs.ktor.clientCore)
			}
		}
	}
}
