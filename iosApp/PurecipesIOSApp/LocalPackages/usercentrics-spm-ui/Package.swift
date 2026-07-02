// swift-tools-version:5.3
import PackageDescription

let package = Package(
	name: "UsercentricsUI",
	platforms: [
		.iOS(.v11),
		.tvOS(.v11),
	],
	products: [
		.library(
			name: "UsercentricsUI",
			targets: ["UsercentricsUI"]
		),
	],
	dependencies: [
		.package(path: "../usercentrics-spm-sdk"),
	],
	targets: [
		.binaryTarget(
			name: "UsercentricsUIFramework",
			path: "../XCFrameworks/UsercentricsUI.xcframework"
		),
		.target(
			name: "UsercentricsUI",
			dependencies: [
				.product(name: "Usercentrics", package: "usercentrics-spm-sdk"),
				"UsercentricsUIFramework",
			]
		),
	]
)
