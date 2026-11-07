import SwiftUI

/// A button's emphasis, as on the web: one primary action per view.
public enum MortarButtonVariant: Sendable, CaseIterable {
    case primary, secondary, ghost, danger
    /// The brand's flat accent under on-accent text.
    case accent
}

/// A control's size. Medium follows the theme's density and never drops
/// below the 44-point touch target; small and large are fixed.
public enum MortarControlSize: Sendable, CaseIterable {
    case sm, md, lg
}

/// Mortar's button look for any SwiftUI `Button`:
/// `Button("Save", action: save).buttonStyle(.mortar(.primary))`.
public struct MortarButtonStyle: ButtonStyle {
    public var variant: MortarButtonVariant
    public var size: MortarControlSize
    public var isLoading: Bool

    public init(
        _ variant: MortarButtonVariant = .secondary,
        size: MortarControlSize = .md,
        isLoading: Bool = false
    ) {
        self.variant = variant
        self.size = size
        self.isLoading = isLoading
    }

    public func makeBody(configuration: Configuration) -> some View {
        MortarButtonBody(
            label: configuration.label,
            isPressed: configuration.isPressed,
            style: self
        )
    }
}

extension ButtonStyle where Self == MortarButtonStyle {
    /// Mortar's button look.
    public static func mortar(
        _ variant: MortarButtonVariant = .secondary,
        size: MortarControlSize = .md,
        isLoading: Bool = false
    ) -> MortarButtonStyle {
        MortarButtonStyle(variant, size: size, isLoading: isLoading)
    }
}

private struct MortarButtonBody<Label: View>: View {
    let label: Label
    let isPressed: Bool
    let style: MortarButtonStyle
    private var mortar = MortarContext()
    @Environment(\.isEnabled) private var isEnabled

    init(label: Label, isPressed: Bool, style: MortarButtonStyle) {
        self.label = label
        self.isPressed = isPressed
        self.style = style
    }

    private var metrics: (height: Double, pad: Double, fontSize: Double) {
        let density = mortar.theme.density
        switch style.size {
        case .sm: return (30, 10, 13)
        case .md: return (max(density.control, MortarTokens.touchTarget), density.pad, density.fontSize)
        case .lg: return (44, 20, 15)
        }
    }

    private var colours: (fill: Color, text: Color, border: Color?) {
        let p = mortar.palette
        if !isEnabled { return (p[\.sunken], p[\.muted], nil) }
        let pressedFill = isPressed ? p[\.sunken] : nil
        switch style.variant {
        case .primary:
            return (isPressed ? p[\.primaryActive] : p[\.primary], p[\.onPrimary], p[\.primary])
        case .secondary:
            return (pressedFill ?? p[\.surface], p[\.ink], p[\.lineStrong])
        case .ghost:
            return (pressedFill ?? .clear, p[\.secondary], nil)
        case .danger:
            return (pressedFill ?? .clear, p[\.danger], p[\.danger])
        case .accent:
            return (p[\.accent], p[\.onAccent], nil)
        }
    }

    var body: some View {
        let (height, pad, fontSize) = metrics
        let (fill, text, border) = colours
        let shape = RoundedRectangle(cornerRadius: MortarTokens.radius)
        HStack(spacing: 8) {
            if style.isLoading {
                ProgressView()
                    .controlSize(.small)
                    .tint(text)
            }
            label
        }
        .font(mortar.font(fontSize, .semibold))
        .foregroundStyle(text)
        .lineLimit(1)
        .padding(.horizontal, pad)
        .padding(.vertical, 4)
        .frame(minWidth: mortar.scaled(height), minHeight: mortar.scaled(height))
        .background {
            shape.fill(fill)
            if style.variant == .accent && isPressed && isEnabled {
                shape.fill(mortar.palette.accentWash.opacity(0.28))
            }
        }
        .overlay {
            if !isEnabled {
                shape.strokeBorder(
                    mortar.palette[\.lineStrong],
                    style: StrokeStyle(lineWidth: 1, dash: [3, 2])
                )
            } else if let border {
                shape.strokeBorder(border, lineWidth: 1)
            }
        }
        .contentShape(shape)
        .offset(y: isPressed && isEnabled ? 1 : 0)
        .allowsHitTesting(!style.isLoading)
        .accessibilityValue(style.isLoading ? "Loading" : "")
    }
}

/// A button with Mortar's look: a title, an optional SF Symbol, and a
/// loading state that keeps the button in place but ignores taps.
public struct MortarButton: View {
    private let title: LocalizedStringKey
    private let systemImage: String?
    private let iconOnly: Bool
    private let style: MortarButtonStyle
    private let role: ButtonRole?
    private let action: () -> Void

    /// - Parameter iconOnly: Shows only the symbol; the title stays the
    ///   accessible name.
    public init(
        _ title: LocalizedStringKey,
        systemImage: String? = nil,
        variant: MortarButtonVariant = .secondary,
        size: MortarControlSize = .md,
        isLoading: Bool = false,
        iconOnly: Bool = false,
        role: ButtonRole? = nil,
        action: @escaping () -> Void
    ) {
        self.title = title
        self.systemImage = systemImage
        self.iconOnly = iconOnly && systemImage != nil
        self.style = MortarButtonStyle(variant, size: size, isLoading: isLoading)
        self.role = role
        self.action = action
    }

    public var body: some View {
        Button(role: role, action: action) {
            if let systemImage {
                if iconOnly {
                    Label(title, systemImage: systemImage).labelStyle(.iconOnly)
                } else {
                    Label(title, systemImage: systemImage)
                }
            } else {
                Text(title)
            }
        }
        .buttonStyle(style)
    }
}
