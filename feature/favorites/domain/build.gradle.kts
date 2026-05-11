plugins {
	id("convention.domain")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.favorites.domain"
	}

	sourceSets {
		commonTest {
			dependencies {
				implementation(libs.kotlinx.coroutinesTest)
				implementation(project(":shared:testfixtures"))
			}
		}
	}
}
