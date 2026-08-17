plugins {
	id("convention.domain")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.library.domain"
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
