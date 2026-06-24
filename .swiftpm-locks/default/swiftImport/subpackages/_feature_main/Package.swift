// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_feature_main",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "_feature_main",
      type: .none,
      targets: ["_feature_main"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_feature_main",
      dependencies: [
      ]
    )
  ]
)
