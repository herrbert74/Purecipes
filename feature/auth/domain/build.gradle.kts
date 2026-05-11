plugins {
	id("convention.domain")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.auth.domain"
	}

	sourceSets {
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
				implementation(libs.kotlinx.coroutinesTest)
			}
		}
	}
}
