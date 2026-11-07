// swift-tools-version: 6.0
// Mortar for SwiftUI: iPhone, iPad, and Mac through "Designed for iPad".
// The sources live in native/apple; the npm package is the rest of this repo.
import PackageDescription

let package = Package(
    name: "Mortar",
    defaultLocalization: "en",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "Mortar", targets: ["Mortar"]),
    ],
    dependencies: [
        .package(url: "https://github.com/pointfreeco/swift-snapshot-testing", from: "1.17.0"),
    ],
    targets: [
        .target(name: "Mortar", path: "native/apple/Sources/Mortar"),
        .testTarget(
            name: "MortarTests",
            dependencies: [
                "Mortar",
                .product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
            ],
            path: "native/apple/Tests/MortarTests",
            exclude: ["__Snapshots__"]
        ),
    ]
)
