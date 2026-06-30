// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_feature_settings_ui",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "_feature_settings_ui",
      type: .none,
      targets: ["_feature_settings_ui"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_feature_settings_ui",
      dependencies: [
      ]
    )
  ]
)
