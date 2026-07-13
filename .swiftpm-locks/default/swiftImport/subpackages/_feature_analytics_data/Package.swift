// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_feature_analytics_data",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "_feature_analytics_data",
      type: .none,
      targets: ["_feature_analytics_data"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/firebase/firebase-ios-sdk.git",
      exact: "12.16.0"
    )
  ],
  targets: [
    .target(
      name: "_feature_analytics_data",
      dependencies: [
        .product(
          name: "FirebaseAnalytics",
          package: "firebase-ios-sdk"
        ),
        .product(
          name: "FirebaseCrashlytics",
          package: "firebase-ios-sdk"
        )
      ]
    )
  ]
)
