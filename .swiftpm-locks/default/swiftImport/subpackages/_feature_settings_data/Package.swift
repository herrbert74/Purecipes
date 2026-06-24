// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_feature_settings_data",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "_feature_settings_data",
      type: .none,
      targets: ["_feature_settings_data"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_feature_settings_data",
      dependencies: [
      ]
    )
  ]
)
