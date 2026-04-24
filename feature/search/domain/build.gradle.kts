plugins {
	id("convention.domain")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.search.domain"
		compileSdk = 36
		minSdk = 24
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
