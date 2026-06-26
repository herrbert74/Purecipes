// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_feature_auth_data",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "_feature_auth_data",
      type: .none,
      targets: ["_feature_auth_data"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/firebase/firebase-ios-sdk.git",
      exact: "12.14.0"
    )
  ],
  targets: [
    .target(
      name: "_feature_auth_data",
      dependencies: [
        .product(
          name: "FirebaseAuth",
          package: "firebase-ios-sdk"
        )
      ]
    )
  ]
)
