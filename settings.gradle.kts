@file:Suppress("UnstableApiUsage")

includeBuild("build-logic")

val uiTestTagPackage = "app.purecipes.shared.testfixtures.uitest"
val knownUiTestTags = listOf(
	"UiTestEssential",
	"UiTestAuth",
	"UiTestCooking",
	"UiTestFeatureRequests",
	"UiTestHome",
	"UiTestLibrary",
	"UiTestMain",
	"UiTestNewRecipe",
	"UiTestOnboarding",
	"UiTestRecipeDetails",
	"UiTestSearch",
	"UiTestSettings",
	"UiTestSubscription",
)
val annotationProperty = "android.testInstrumentationRunnerArguments.annotation"
val uiTestTagsProperty = startParameter.projectProperties["uiTestTags"]
if (!uiTestTagsProperty.isNullOrBlank() && !startParameter.projectProperties.containsKey(annotationProperty)) {
	val requested = uiTestTagsProperty
		.split(',')
		.map { it.trim() }
		.filter { it.isNotEmpty() }
	if (requested.none { it == "UiTestAll" }) {
		val unknown = requested.filterNot { it in knownUiTestTags }
		require(unknown.isEmpty()) {
			"Unknown uiTestTags: ${unknown.joinToString()}. Known: ${knownUiTestTags.joinToString()} UiTestAll"
		}
		val selected = (requested + "UiTestEssential").toSet()
		val resolved = knownUiTestTags
			.filter { it in selected }
			.joinToString(",") { "$uiTestTagPackage.$it" }
		val projectProperties = LinkedHashMap(startParameter.projectProperties)
		projectProperties[annotationProperty] = resolved
		startParameter.setProjectProperties(projectProperties)
		gradle.beforeProject {
			extensions.extraProperties[annotationProperty] = resolved
		}
	}
}

pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
	id("com.autonomousapps.build-health") version ("3.20.1")
	id("org.jetbrains.kotlin.jvm") version ("2.4.21") apply false
	id("com.android.application") version ("9.5.0") apply false
	id("org.jetbrains.kotlin.android") version ("2.4.21") apply false
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		google()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

rootProject.name = "Purecipes"
include(":app")
include(":backend")
include(":enrichment")
include(":umbrella")
include(":base:kotlin")
include(":feature:ads:data")
include(":feature:ads:domain")
include(":feature:ads:ui")
include(":feature:analytics:data")
include(":feature:analytics:domain")
include(":feature:analytics:ui")
include(":feature:auth:data")
include(":feature:auth:domain")
include(":feature:auth:ui")
include(":feature:featurerequests:data")
include(":feature:featurerequests:domain")
include(":feature:featurerequests:ui")
include(":feature:home:data")
include(":feature:home:domain")
include(":feature:home:ui")
include(":feature:measurement:data")
include(":feature:measurement:domain")
include(":feature:main")
include(":feature:newrecipe:data")
include(":feature:newrecipe:domain")
include(":feature:newrecipe:ui")
include(":feature:onboarding:data")
include(":feature:onboarding:domain")
include(":feature:onboarding:ui")
include(":feature:cooking:ui")
include(":feature:library:data")
include(":feature:library:domain")
include(":feature:library:ui")
include(":feature:recipedetails:data")
include(":feature:recipedetails:domain")
include(":feature:recipedetails:ui")
include(":feature:search:data")
include(":feature:search:domain")
include(":feature:search:ui")
include(":feature:sharing:data")
include(":feature:sharing:domain")
include(":feature:sharing:ui")
include(":feature:settings:data")
include(":feature:settings:domain")
include(":feature:settings:ui")
include(":feature:subscription:data")
include(":feature:subscription:domain")
include(":feature:subscription:ui")
include(":shared:data")
include(":rules")
include(":shared:dataTestFixtures")
include(":shared:domain")
include(":shared:testfixtures")
include(":shared:ui")
include(":tools:play-publish")
include(":tools:store-screenshots")
