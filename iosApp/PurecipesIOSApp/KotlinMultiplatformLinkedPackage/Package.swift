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
    .package(path: "subpackages/_feature_analytics_data"),
    .package(path: "subpackages/_feature_auth_data"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpauth_firebase_facebook_3_0_5"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpauth_firebase_3_0_5"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpauth_facebook_3_0_5"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpauth_google_3_0_5"),
    .package(path: "subpackages/dev_gitlive_firebase_auth_3_0_0_alpha01"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpnotifier_push_firebase_2_0_1"),
    .package(path: "subpackages/dev_gitlive_firebase_app_3_0_0_alpha01")
  ],
  targets: [
    .target(
      name: "KotlinMultiplatformLinkedPackage",
      dependencies: [
        .product(name: "_feature_analytics_data", package: "_feature_analytics_data"),
        .product(name: "_feature_auth_data", package: "_feature_auth_data"),
        .product(name: "io_github_mirzemehdi_kmpauth_firebase_facebook_3_0_5", package: "io_github_mirzemehdi_kmpauth_firebase_facebook_3_0_5"),
        .product(name: "io_github_mirzemehdi_kmpauth_firebase_3_0_5", package: "io_github_mirzemehdi_kmpauth_firebase_3_0_5"),
        .product(name: "io_github_mirzemehdi_kmpauth_facebook_3_0_5", package: "io_github_mirzemehdi_kmpauth_facebook_3_0_5"),
        .product(name: "io_github_mirzemehdi_kmpauth_google_3_0_5", package: "io_github_mirzemehdi_kmpauth_google_3_0_5"),
        .product(name: "dev_gitlive_firebase_auth_3_0_0_alpha01", package: "dev_gitlive_firebase_auth_3_0_0_alpha01"),
        .product(name: "io_github_mirzemehdi_kmpnotifier_push_firebase_2_0_1", package: "io_github_mirzemehdi_kmpnotifier_push_firebase_2_0_1"),
        .product(name: "dev_gitlive_firebase_app_3_0_0_alpha01", package: "dev_gitlive_firebase_app_3_0_0_alpha01")
      ]
    )
  ]
)
