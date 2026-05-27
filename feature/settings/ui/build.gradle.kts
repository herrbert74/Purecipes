plugins {
	id("convention.ui")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.settings.ui"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:settings:domain"))
				implementation(libs.jetbrains.androidXNavigation3Ui)
				implementation(libs.kotlinx.serializationJson)
			}
		}
	}
}
