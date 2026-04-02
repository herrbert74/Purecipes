plugins {
	id("convention.domain")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.newrecipe.domain"
		compileSdk = 36
		minSdk = 24
	}
}
