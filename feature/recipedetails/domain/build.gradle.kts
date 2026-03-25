plugins {
	id("convention.domain")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.recipedetails.domain"
		compileSdk = 36
		minSdk = 24
	}
}
