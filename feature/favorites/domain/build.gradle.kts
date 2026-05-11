plugins {
	id("convention.domain")
	id("convention.common-test")
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
