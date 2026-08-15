// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "KotlinMultiplatformLinkedPackage",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "KotlinMultiplatformLinkedPackage",
      type: .none,
      targets: ["KotlinMultiplatformLinkedPackage"]
    )
  ],
  dependencies: [
    .package(path: "subpackages/_feature_ads_data"),
    .package(path: "subpackages/_feature_ads_ui"),
    .package(path: "subpackages/_feature_analytics_data"),
    .package(path: "subpackages/_feature_auth_data"),
    .package(path: "subpackages/_feature_library_data"),
    .package(path: "subpackages/_feature_library_ui"),
    .package(path: "subpackages/_feature_main"),
    .package(path: "subpackages/_feature_measurement_data"),
    .package(path: "subpackages/_feature_newrecipe_data"),
    .package(path: "subpackages/_feature_recipedetails_data"),
    .package(path: "subpackages/_feature_recipedetails_ui"),
    .package(path: "subpackages/_feature_search_data"),
    .package(path: "subpackages/_feature_search_ui"),
    .package(path: "subpackages/_feature_settings_data"),
    .package(path: "subpackages/_feature_settings_ui"),
    .package(path: "subpackages/_feature_sharing_data"),
    .package(path: "subpackages/_feature_subscription_data"),
    .package(path: "subpackages/_shared_data"),
    .package(path: "subpackages/_shared_dataTestFixtures"),
    .package(path: "subpackages/_umbrella"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpnotifier_push_firebase_2_0_0")
  ],
  targets: [
    .target(
      name: "KotlinMultiplatformLinkedPackage",
      dependencies: [
        .product(name: "_feature_ads_data", package: "_feature_ads_data"),
        .product(name: "_feature_ads_ui", package: "_feature_ads_ui"),
        .product(name: "_feature_analytics_data", package: "_feature_analytics_data"),
        .product(name: "_feature_auth_data", package: "_feature_auth_data"),
        .product(name: "_feature_library_data", package: "_feature_library_data"),
        .product(name: "_feature_library_ui", package: "_feature_library_ui"),
        .product(name: "_feature_main", package: "_feature_main"),
        .product(name: "_feature_measurement_data", package: "_feature_measurement_data"),
        .product(name: "_feature_newrecipe_data", package: "_feature_newrecipe_data"),
        .product(name: "_feature_recipedetails_data", package: "_feature_recipedetails_data"),
        .product(name: "_feature_recipedetails_ui", package: "_feature_recipedetails_ui"),
        .product(name: "_feature_search_data", package: "_feature_search_data"),
        .product(name: "_feature_search_ui", package: "_feature_search_ui"),
        .product(name: "_feature_settings_data", package: "_feature_settings_data"),
        .product(name: "_feature_settings_ui", package: "_feature_settings_ui"),
        .product(name: "_feature_sharing_data", package: "_feature_sharing_data"),
        .product(name: "_feature_subscription_data", package: "_feature_subscription_data"),
        .product(name: "_shared_data", package: "_shared_data"),
        .product(name: "_shared_dataTestFixtures", package: "_shared_dataTestFixtures"),
        .product(name: "_umbrella", package: "_umbrella"),
        .product(name: "io_github_mirzemehdi_kmpnotifier_push_firebase_2_0_0", package: "io_github_mirzemehdi_kmpnotifier_push_firebase_2_0_0")
      ]
    )
  ]
)
