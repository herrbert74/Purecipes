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
    .package(path: "subpackages/io_github_mirzemehdi_kmpauth_firebase_facebook_3_0_0_alpha04"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpauth_firebase_google_3_0_0_alpha04"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpauth_firebase_core_3_0_0_alpha04"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpauth_facebook_3_0_0_alpha04"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpauth_google_3_0_0_alpha04"),
    .package(path: "subpackages/io_github_mirzemehdi_kmpnotifier_push_firebase_2_0_0")
  ],
  targets: [
    .target(
      name: "KotlinMultiplatformLinkedPackage",
      dependencies: [
        .product(name: "_feature_analytics_data", package: "_feature_analytics_data"),
        .product(name: "_feature_auth_data", package: "_feature_auth_data"),
        .product(name: "io_github_mirzemehdi_kmpauth_firebase_facebook_3_0_0_alpha04", package: "io_github_mirzemehdi_kmpauth_firebase_facebook_3_0_0_alpha04"),
        .product(name: "io_github_mirzemehdi_kmpauth_firebase_google_3_0_0_alpha04", package: "io_github_mirzemehdi_kmpauth_firebase_google_3_0_0_alpha04"),
        .product(name: "io_github_mirzemehdi_kmpauth_firebase_core_3_0_0_alpha04", package: "io_github_mirzemehdi_kmpauth_firebase_core_3_0_0_alpha04"),
        .product(name: "io_github_mirzemehdi_kmpauth_facebook_3_0_0_alpha04", package: "io_github_mirzemehdi_kmpauth_facebook_3_0_0_alpha04"),
        .product(name: "io_github_mirzemehdi_kmpauth_google_3_0_0_alpha04", package: "io_github_mirzemehdi_kmpauth_google_3_0_0_alpha04"),
        .product(name: "io_github_mirzemehdi_kmpnotifier_push_firebase_2_0_0", package: "io_github_mirzemehdi_kmpnotifier_push_firebase_2_0_0")
      ]
    )
  ]
)
