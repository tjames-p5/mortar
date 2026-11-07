import SwiftUI

/// A short status label: a pill with a dot in its tone's colour.
public struct MortarBadge: View {
    private let title: Text
    private let tone: MortarTone
    private var mortar = MortarContext()

    public init(_ title: LocalizedStringKey, tone: MortarTone = .neutral) {
        self.title = Text(title)
        self.tone = tone
    }

    public init(verbatim title: String, tone: MortarTone = .neutral) {
        self.title = Text(verbatim: title)
        self.tone = tone
    }

    public var body: some View {
        let colour = tone.colour(in: mortar.palette)
        HStack(spacing: 6) {
            Circle()
                .fill(colour)
                .frame(width: 6, height: 6)
                .accessibilityHidden(true)
            title
        }
        .font(mortar.font(12, .semibold))
        .foregroundStyle(colour)
        .padding(.vertical, 2)
        .padding(.horizontal, 8)
        .overlay(Capsule().strokeBorder(mortar.palette[\.line], lineWidth: 1))
        .accessibilityElement(children: .combine)
    }
}
