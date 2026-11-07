import SwiftUI

/// What a view shows when it has nothing yet: a title, a reason and the
/// action that fills it.
public struct MortarEmptyState<Action: View>: View {
    public enum Alignment: Sendable { case center, leading }

    private let title: Text
    private let description: Text?
    private let systemImage: String?
    private let dashed: Bool
    private let alignment: Alignment
    private let action: Action
    private var mortar = MortarContext()

    public init(
        _ title: LocalizedStringKey,
        description: LocalizedStringKey? = nil,
        systemImage: String? = nil,
        dashed: Bool = false,
        alignment: Alignment = .center,
        @ViewBuilder action: () -> Action
    ) {
        self.title = Text(title)
        self.description = description.map { Text($0) }
        self.systemImage = systemImage
        self.dashed = dashed
        self.alignment = alignment
        self.action = action()
    }

    public var body: some View {
        let p = mortar.palette
        let horizontal: HorizontalAlignment = alignment == .center ? .center : .leading
        VStack(alignment: horizontal, spacing: 0) {
            if let systemImage {
                Image(systemName: systemImage)
                    .font(.system(size: 22))
                    .foregroundStyle(p[\.secondary])
                    .frame(width: 48, height: 48)
                    .background(RoundedRectangle(cornerRadius: MortarTokens.radiusLg).fill(p[\.sunken]))
                    .padding(.bottom, 16)
                    .accessibilityHidden(true)
            }
            title
                .font(mortar.font(18, .semibold, heading: true))
                .foregroundStyle(p[\.ink])
                .accessibilityAddTraits(.isHeader)
            if let description {
                description
                    .font(mortar.font(mortar.theme.density.fontSize))
                    .foregroundStyle(p[\.muted])
                    .padding(.top, 10)
            }
            if Action.self != EmptyView.self {
                action.padding(.top, 20)
            }
        }
        .multilineTextAlignment(alignment == .center ? .center : .leading)
        .frame(maxWidth: .infinity, alignment: alignment == .center ? .center : .leading)
        .padding(.vertical, dashed ? 48 : 64)
        .padding(.horizontal, alignment == .leading && !dashed ? 0 : 24)
        .overlay {
            if dashed {
                RoundedRectangle(cornerRadius: MortarTokens.radiusLg)
                    .strokeBorder(p[\.lineStrong], style: StrokeStyle(lineWidth: 2, dash: [6, 4]))
            }
        }
    }
}

extension MortarEmptyState where Action == EmptyView {
    public init(
        _ title: LocalizedStringKey,
        description: LocalizedStringKey? = nil,
        systemImage: String? = nil,
        dashed: Bool = false,
        alignment: Alignment = .center
    ) {
        self.init(title, description: description, systemImage: systemImage,
                  dashed: dashed, alignment: alignment) { EmptyView() }
    }
}
