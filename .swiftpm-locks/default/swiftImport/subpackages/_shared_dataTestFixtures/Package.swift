// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_shared_dataTestFixtures",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "_shared_dataTestFixtures",
      type: .none,
      targets: ["_shared_dataTestFixtures"]
    )
  ],
  dependencies: [
  ],
  targets: [
    .target(
      name: "_shared_dataTestFixtures",
      dependencies: [
      ]
    )
  ]
)
