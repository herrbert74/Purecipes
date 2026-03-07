plugins {
	alias(libs.plugins.androidApplication) apply false
	alias(libs.plugins.androidLibrary) apply false
	alias(libs.plugins.kotlin.android) apply false
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.jetBrainsCompose) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.kotlin.jvm) apply false
	alias(libs.plugins.android.kotlin.multiplatform.library) apply false
	alias(libs.plugins.android.lint) apply false
	id("convention.detekt")
}
