import SwiftUI

/// Mortar's switch look for any SwiftUI `Toggle`:
/// `Toggle("Wi-Fi", isOn: $on).toggleStyle(.mortar)`. The whole row toggles.
public struct MortarToggleStyle: ToggleStyle {
    public init() {}

    public func makeBody(configuration: Configuration) -> some View {
        MortarToggleBody(configuration: configuration)
    }
}

extension ToggleStyle where Self == MortarToggleStyle {
    /// Mortar's switch look.
    public static var mortar: MortarToggleStyle { MortarToggleStyle() }
}

private struct MortarToggleBody: View {
    let configuration: ToggleStyleConfiguration
    private var mortar = MortarContext()
    @Environment(\.isEnabled) private var isEnabled

    init(configuration: ToggleStyleConfiguration) {
        self.configuration = configuration
    }

    var body: some View {
        let p = mortar.palette
        let on = configuration.isOn
        Button {
            configuration.isOn.toggle()
        } label: {
            HStack(alignment: .center, spacing: 12) {
                configuration.label
                    .frame(maxWidth: .infinity, alignment: .leading)
                Capsule()
                    .fill(on ? p[\.primary] : p[\.sunken])
                    .overlay(Capsule().strokeBorder(on ? p[\.primary] : p[\.lineStrong], lineWidth: 1))
                    .frame(width: 34, height: 20)
                    .overlay(alignment: on ? .trailing : .leading) {
                        Circle()
                            .fill(on ? p[\.onPrimary] : p[\.muted])
                            .frame(width: 14, height: 14)
                            .padding(3)
                    }
                    .animation(.snappy(duration: 0.15), value: on)
            }
            .frame(minHeight: MortarTokens.touchTarget)
            .contentShape(Rectangle())
            .opacity(isEnabled ? 1 : 0.5)
        }
        .buttonStyle(.plain)
        .accessibilityRepresentation {
            Toggle(isOn: configuration.$isOn) { configuration.label }
        }
    }
}

/// A labelled switch with an optional description under the label.
public struct MortarSwitch: View {
    private let label: Text
    private let description: Text?
    @Binding private var isOn: Bool
    private var mortar = MortarContext()

    public init(_ label: LocalizedStringKey, isOn: Binding<Bool>, description: LocalizedStringKey? = nil) {
        self.label = Text(label)
        self._isOn = isOn
        self.description = description.map { Text($0) }
    }

    public var body: some View {
        Toggle(isOn: $isOn) {
            VStack(alignment: .leading, spacing: 2) {
                label
                    .font(mortar.font(mortar.theme.density.fontSize, .medium))
                    .foregroundStyle(mortar.palette[\.ink])
                if let description {
                    description
                        .font(mortar.font(13))
                        .foregroundStyle(mortar.palette[\.muted])
                }
            }
        }
        .toggleStyle(.mortar)
    }
}
