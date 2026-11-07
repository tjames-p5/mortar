import Foundation
import SwiftUI

/// A brand for a view hierarchy: its palettes, fonts and density.
///
/// Set it once near the root with ``SwiftUI/View/mortarTheme(_:)``; every
/// Mortar component reads it, in the palette that matches the colour scheme.
public struct MortarTheme: Sendable, Equatable {
    public var light: MortarPalette
    public var dark: MortarPalette
    /// The body font's family name, or `nil` for the system font. The app
    /// bundles and registers the font itself.
    public var bodyFont: String?
    /// Headings and component titles; the body font when `nil`.
    public var headingFont: String?
    public var density: MortarDensity

    public init(
        light: MortarPalette = MortarTokens.light,
        dark: MortarPalette = MortarTokens.dark,
        bodyFont: String? = nil,
        headingFont: String? = nil,
        density: MortarDensity = MortarTokens.comfortable
    ) {
        self.light = light
        self.dark = dark
        self.bodyFont = bodyFont
        self.headingFont = headingFont
        self.density = density
    }

    /// Mortar's neutral brand.
    public static let neutral = MortarTheme()

    /// The palette for a colour scheme.
    public func palette(for scheme: ColorScheme) -> MortarPalette {
        scheme == .dark ? dark : light
    }

    /// A Mortar font size scaled for Dynamic Type, in the body or heading
    /// family. Mortar's sizes are the default (Large) text size's.
    public func font(
        size: Double,
        weight: Font.Weight = .regular,
        heading: Bool = false,
        dynamicTypeSize: DynamicTypeSize = .large
    ) -> Font {
        let scaled = size * dynamicTypeSize.mortarScale
        let family = heading ? (headingFont ?? bodyFont) : bodyFont
        guard let family else { return .system(size: scaled, weight: weight) }
        return .custom(family, fixedSize: scaled).weight(weight)
    }
}

extension DynamicTypeSize {
    /// The body text size at this setting over the default's (17pt), as
    /// Apple's text styles scale.
    var mortarScale: Double {
        let body: Double
        switch self {
        case .xSmall: body = 14
        case .small: body = 15
        case .medium: body = 16
        case .large: body = 17
        case .xLarge: body = 19
        case .xxLarge: body = 21
        case .xxxLarge: body = 23
        case .accessibility1: body = 28
        case .accessibility2: body = 33
        case .accessibility3: body = 40
        case .accessibility4: body = 47
        case .accessibility5: body = 53
        @unknown default: body = 17
        }
        return body / 17
    }
}

private struct MortarThemeKey: EnvironmentKey {
    static let defaultValue = MortarTheme.neutral
}

extension EnvironmentValues {
    /// The Mortar theme components read; ``MortarTheme/neutral`` by default.
    public var mortarTheme: MortarTheme {
        get { self[MortarThemeKey.self] }
        set { self[MortarThemeKey.self] = newValue }
    }
}

extension View {
    /// Themes Mortar components in this view and its descendants.
    public func mortarTheme(_ theme: MortarTheme) -> some View {
        environment(\.mortarTheme, theme)
    }
}

extension Color {
    /// A Mortar colour, `0xRRGGBB` in sRGB.
    public init(mortar hex: UInt32) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255,
            opacity: 1
        )
    }
}

extension MortarPalette {
    /// One of the palette's colours as a SwiftUI colour.
    public subscript(colour: KeyPath<MortarPalette, UInt32>) -> Color {
        Color(mortar: self[keyPath: colour])
    }

    /// Black or white, whichever moves away from `onAccent`: the wash laid
    /// over a pressed accent fill so its text contrast only rises.
    var accentWash: Color {
        let channel = { (shift: UInt32) -> Double in
            let v = Double((onAccent >> shift) & 0xFF) / 255
            return v <= 0.04045 ? v / 12.92 : pow((v + 0.055) / 1.055, 2.4)
        }
        let (r, g, b) = (channel(16), channel(8), channel(0))
        // Oklab lightness, as tokens.css's oklch(from … round(1 - l, 1) 0 0).
        let l = cbrt(0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b)
        let m = cbrt(0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b)
        let s = cbrt(0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b)
        let lightness = 0.2104542553 * l + 0.793617785 * m - 0.0040720468 * s
        return lightness < 0.5 ? .white : .black
    }
}

/// The status colours, shared by badges, notices and fields.
public enum MortarTone: Sendable, CaseIterable {
    case neutral, info, success, warning, danger

    func colour(in palette: MortarPalette) -> Color {
        switch self {
        case .neutral: palette[\.secondary]
        case .info: palette[\.info]
        case .success: palette[\.success]
        case .warning: palette[\.warning]
        case .danger: palette[\.danger]
        }
    }
}

/// Reads the theme, palette and text size every component needs.
struct MortarContext: DynamicProperty {
    @Environment(\.mortarTheme) var theme
    @Environment(\.colorScheme) var scheme
    @Environment(\.dynamicTypeSize) var dynamicTypeSize

    var palette: MortarPalette { theme.palette(for: scheme) }

    func font(_ size: Double, _ weight: Font.Weight = .regular, heading: Bool = false) -> Font {
        theme.font(size: size, weight: weight, heading: heading, dynamicTypeSize: dynamicTypeSize)
    }

    /// A length that grows with the text, such as a control's height.
    func scaled(_ length: Double) -> Double {
        length * max(1, dynamicTypeSize.mortarScale)
    }
}
