import SwiftUI
import XCTest
@testable import Mortar

final class MortarThemeTests: XCTestCase {
    func testNeutralThemeUsesTheGeneratedTokens() {
        let theme = MortarTheme.neutral
        XCTAssertEqual(theme.palette(for: .light), MortarTokens.light)
        XCTAssertEqual(theme.palette(for: .dark), MortarTokens.dark)
        XCTAssertEqual(theme.density, MortarTokens.comfortable)
        XCTAssertEqual(MortarTokens.light.paper, 0xFFFFFF)
        XCTAssertEqual(MortarTokens.dark.paper, 0x000000)
    }

    func testTextScalesWithDynamicType() {
        XCTAssertEqual(DynamicTypeSize.large.mortarScale, 1)
        XCTAssertEqual(DynamicTypeSize.xSmall.mortarScale, 14.0 / 17)
        XCTAssertEqual(DynamicTypeSize.accessibility5.mortarScale, 53.0 / 17)
        let sizes = DynamicTypeSize.allCases.map(\.mortarScale)
        XCTAssertEqual(sizes, sizes.sorted(), "scales grow with the setting")
    }

    func testAccentWashMovesAwayFromTheText() {
        var palette = MortarTokens.light
        palette.onAccent = 0xFFFFFF
        XCTAssertEqual(palette.accentWash, .black)
        palette.onAccent = 0x111111
        XCTAssertEqual(palette.accentWash, .white)
        // Mid grey either side of oklab lightness 0.5.
        palette.onAccent = 0x5E5E5E
        XCTAssertEqual(palette.accentWash, .white)
        palette.onAccent = 0x6A6A6A
        XCTAssertEqual(palette.accentWash, .black)
    }

    func testAvatarInitials() {
        XCTAssertEqual(MortarAvatar(name: "Ada Lovelace").initials, "AL")
        XCTAssertEqual(MortarAvatar(name: "grace brewster murray hopper").initials, "GH")
        XCTAssertEqual(MortarAvatar(name: "Cher").initials, "C")
        XCTAssertEqual(MortarAvatar(name: "  ").initials, "")
    }
}
