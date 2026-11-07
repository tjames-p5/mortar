import SwiftUI

/// A bordered surface with an optional title, header actions and footer.
public struct MortarCard<Content: View, Actions: View, Footer: View>: View {
    public enum Padding: Sendable { case `default`, compact, none }
    public enum Tone: Sendable { case `default`, muted }

    private let title: Text?
    private let description: Text?
    private let padding: Padding
    private let tone: Tone
    private let content: Content
    private let actions: Actions
    private let footer: Footer
    private var mortar = MortarContext()

    public init(
        _ title: LocalizedStringKey? = nil,
        description: LocalizedStringKey? = nil,
        padding: Padding = .default,
        tone: Tone = .default,
        @ViewBuilder content: () -> Content,
        @ViewBuilder actions: () -> Actions,
        @ViewBuilder footer: () -> Footer
    ) {
        self.title = title.map { Text($0) }
        self.description = description.map { Text($0) }
        self.padding = padding
        self.tone = tone
        self.content = content()
        self.actions = actions()
        self.footer = footer()
    }

    private var hasActions: Bool { Actions.self != EmptyView.self }
    private var hasFooter: Bool { Footer.self != EmptyView.self }
    private var inset: Double {
        switch padding {
        case .default: 20
        case .compact: 14
        case .none: 0
        }
    }

    public var body: some View {
        let p = mortar.palette
        let shape = RoundedRectangle(cornerRadius: MortarTokens.radiusLg)
        VStack(alignment: .leading, spacing: 0) {
            if title != nil || description != nil || hasActions {
                ViewThatFits(in: .horizontal) {
                    HStack(alignment: .center, spacing: 12) {
                        heading
                        Spacer(minLength: 0)
                        actionRow
                    }
                    VStack(alignment: .leading, spacing: 12) {
                        heading
                        actionRow
                    }
                }
                .padding(.vertical, 14)
                .padding(.horizontal, 20)
                .frame(maxWidth: .infinity, alignment: .leading)
                Rectangle().fill(p[\.line]).frame(height: 1)
            }
            content
                .font(mortar.font(mortar.theme.density.fontSize))
                .padding(inset)
                .frame(maxWidth: .infinity, alignment: .leading)
            if hasFooter {
                Rectangle().fill(p[\.line]).frame(height: 1)
                footer
                    .font(mortar.font(13))
                    .foregroundStyle(p[\.secondary])
                    .padding(.vertical, 12)
                    .padding(.horizontal, 20)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(p[\.sunken])
            }
        }
        .foregroundStyle(p[\.ink])
        .background(tone == .muted ? p[\.sunken] : p[\.surface])
        .clipShape(shape)
        .overlay(shape.strokeBorder(p[\.line], lineWidth: 1))
    }

    private var heading: some View {
        VStack(alignment: .leading, spacing: 2) {
            if let title {
                title
                    .font(mortar.font(15, .semibold, heading: true))
                    .accessibilityAddTraits(.isHeader)
            }
            if let description {
                description
                    .font(mortar.font(13))
                    .foregroundStyle(mortar.palette[\.muted])
            }
        }
    }

    private var actionRow: some View {
        HStack(spacing: 8) { actions }
    }
}

extension MortarCard where Actions == EmptyView, Footer == EmptyView {
    public init(
        _ title: LocalizedStringKey? = nil,
        description: LocalizedStringKey? = nil,
        padding: Padding = .default,
        tone: Tone = .default,
        @ViewBuilder content: () -> Content
    ) {
        self.init(title, description: description, padding: padding, tone: tone,
                  content: content, actions: { EmptyView() }, footer: { EmptyView() })
    }
}

extension MortarCard where Footer == EmptyView {
    public init(
        _ title: LocalizedStringKey? = nil,
        description: LocalizedStringKey? = nil,
        padding: Padding = .default,
        tone: Tone = .default,
        @ViewBuilder content: () -> Content,
        @ViewBuilder actions: () -> Actions
    ) {
        self.init(title, description: description, padding: padding, tone: tone,
                  content: content, actions: actions, footer: { EmptyView() })
    }
}

extension MortarCard where Actions == EmptyView {
    public init(
        _ title: LocalizedStringKey? = nil,
        description: LocalizedStringKey? = nil,
        padding: Padding = .default,
        tone: Tone = .default,
        @ViewBuilder content: () -> Content,
        @ViewBuilder footer: () -> Footer
    ) {
        self.init(title, description: description, padding: padding, tone: tone,
                  content: content, actions: { EmptyView() }, footer: footer)
    }
}
