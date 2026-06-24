// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_shared_data",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "_shared_data",
      type: .none,
      targets: ["_shared_data"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_shared_data",
      dependencies: [
      ]
    )
  ]
)
