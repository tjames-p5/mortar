import SwiftUI

/// A message about the page or a task, in a status tone, with optional
/// actions and a dismiss button.
public struct MortarNotice<Actions: View>: View {
    private let title: LocalizedStringResource
    private let message: LocalizedStringResource?
    private let tone: MortarTone
    private let announce: Bool
    private let onDismiss: (() -> Void)?
    private let actions: Actions
    private var mortar = MortarContext()

    /// - Parameters:
    ///   - announce: VoiceOver reads the notice when it appears.
    ///   - onDismiss: Shows a dismiss button that calls it.
    public init(
        _ title: LocalizedStringResource,
        message: LocalizedStringResource? = nil,
        tone: MortarTone = .neutral,
        announce: Bool = false,
        onDismiss: (() -> Void)? = nil,
        @ViewBuilder actions: () -> Actions
    ) {
        self.title = title
        self.message = message
        self.tone = tone
        self.announce = announce
        self.onDismiss = onDismiss
        self.actions = actions()
    }

    public var body: some View {
        let p = mortar.palette
        let colour = tone.colour(in: p)
        HStack(alignment: .top, spacing: 8) {
            VStack(alignment: .leading, spacing: 6) {
                Text(title).font(mortar.font(mortar.theme.density.fontSize, .semibold))
                if let message {
                    Text(message).font(mortar.font(mortar.theme.density.fontSize))
                }
                if Actions.self != EmptyView.self {
                    HStack(spacing: 16) { actions }
                        .font(mortar.font(mortar.theme.density.fontSize, .semibold))
                        .buttonStyle(.plain)
                        .underline()
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            if let onDismiss {
                Button(action: onDismiss) {
                    Image(systemName: "xmark")
                        .font(.system(size: 13, weight: .semibold))
                        .frame(width: MortarTokens.touchTarget, height: MortarTokens.touchTarget)
                        .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
                .accessibilityLabel(Text("Dismiss"))
                .padding(-12)
            }
        }
        .foregroundStyle(colour)
        .padding(16)
        .background(RoundedRectangle(cornerRadius: MortarTokens.radius).fill(p[\.surface]))
        .overlay(RoundedRectangle(cornerRadius: MortarTokens.radius).strokeBorder(p[\.line], lineWidth: 1))
        .accessibilityElement(children: .contain)
        .onAppear {
            guard announce else { return }
            let words = [title, message].compactMap { $0 }.map { String(localized: $0) }
            AccessibilityNotification.Announcement(words.joined(separator: ". ")).post()
        }
    }
}

extension MortarNotice where Actions == EmptyView {
    public init(
        _ title: LocalizedStringResource,
        message: LocalizedStringResource? = nil,
        tone: MortarTone = .neutral,
        announce: Bool = false,
        onDismiss: (() -> Void)? = nil
    ) {
        self.init(title, message: message, tone: tone, announce: announce, onDismiss: onDismiss) {
            EmptyView()
        }
    }
}
