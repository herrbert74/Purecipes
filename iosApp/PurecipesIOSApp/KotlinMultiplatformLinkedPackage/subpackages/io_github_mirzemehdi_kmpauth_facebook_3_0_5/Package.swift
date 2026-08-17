// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "io_github_mirzemehdi_kmpauth_facebook_3_0_5",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "io_github_mirzemehdi_kmpauth_facebook_3_0_5",
      type: .none,
      targets: ["io_github_mirzemehdi_kmpauth_facebook_3_0_5"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/facebook/facebook-ios-sdk.git",
      from: "18.1.0"
    )
  ],
  targets: [
    .target(
      name: "io_github_mirzemehdi_kmpauth_facebook_3_0_5",
      dependencies: [
        .product(
          name: "FacebookCore",
          package: "facebook-ios-sdk"
        ),
        .product(
          name: "FacebookLogin",
          package: "facebook-ios-sdk"
        )
      ]
    )
  ]
)
