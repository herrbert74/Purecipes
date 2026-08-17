// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "io_github_mirzemehdi_kmpauth_facebook_3_0_0_alpha04",
  platforms: [
    .iOS("26.0")
  ],
  products: [
    .library(
      name: "io_github_mirzemehdi_kmpauth_facebook_3_0_0_alpha04",
      type: .none,
      targets: ["io_github_mirzemehdi_kmpauth_facebook_3_0_0_alpha04"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/facebook/facebook-ios-sdk.git",
      from: "18.0.0"
    )
  ],
  targets: [
    .target(
      name: "io_github_mirzemehdi_kmpauth_facebook_3_0_0_alpha04",
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
