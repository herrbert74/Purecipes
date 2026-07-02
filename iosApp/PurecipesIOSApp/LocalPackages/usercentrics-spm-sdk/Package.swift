// swift-tools-version:5.3
import PackageDescription

let package = Package(
	name: "Usercentrics",
	platforms: [
		.iOS(.v11),
		.tvOS(.v11),
	],
	products: [
		.library(
			name: "Usercentrics",
			targets: ["Usercentrics"]
		),
	],
	targets: [
		.binaryTarget(
			name: "Usercentrics",
			path: "../XCFrameworks/Usercentrics.xcframework"
		),
	]
)
