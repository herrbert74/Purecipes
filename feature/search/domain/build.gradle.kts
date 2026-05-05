plugins {
	id("convention.domain")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.search.domain"
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
