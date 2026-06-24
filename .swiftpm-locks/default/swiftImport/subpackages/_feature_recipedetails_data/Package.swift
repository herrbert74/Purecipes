// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_feature_recipedetails_data",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "_feature_recipedetails_data",
      type: .none,
      targets: ["_feature_recipedetails_data"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_feature_recipedetails_data",
      dependencies: [
      ]
    )
  ]
)
