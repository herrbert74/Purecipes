// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_umbrella",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "_umbrella",
      type: .none,
      targets: ["_umbrella"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_umbrella",
      dependencies: [
      ]
    )
  ]
)
