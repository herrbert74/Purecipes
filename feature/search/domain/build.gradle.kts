plugins {
	id("convention.domain")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.search.domain"
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
