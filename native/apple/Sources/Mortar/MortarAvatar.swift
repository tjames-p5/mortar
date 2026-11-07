import SwiftUI

/// A person's picture, or their initials while it loads or when it fails.
public struct MortarAvatar: View {
    public enum Size: Sendable, CaseIterable { case xs, sm, md, lg, xl }
    public enum Shape: Sendable { case circle, square }
    public enum Status: Sendable { case online, offline }

    private let name: String
    private let image: URL?
    private let size: Size
    private let shape: Shape
    private let status: Status?
    private let decorative: Bool
    private var mortar = MortarContext()

    /// - Parameter decorative: Hides the avatar from VoiceOver, for when the
    ///   name is shown beside it.
    public init(
        name: String,
        image: URL? = nil,
        size: Size = .md,
        shape: Shape = .circle,
        status: Status? = nil,
        decorative: Bool = false
    ) {
        self.name = name
        self.image = image
        self.size = size
        self.shape = shape
        self.status = status
        self.decorative = decorative
    }

    private var metrics: (side: Double, fontSize: Double) {
        switch size {
        case .xs: (20, 9)
        case .sm: (26, 10)
        case .md: (34, 12)
        case .lg: (52, 18)
        case .xl: (72, 24)
        }
    }

    /// The first letters of the first and last words.
    var initials: String {
        let words = name.split(whereSeparator: \.isWhitespace)
        let letters = [words.first, words.count > 1 ? words.last : nil]
            .compactMap { $0?.first.map(String.init) }
        return letters.joined().uppercased()
    }

    public var body: some View {
        let p = mortar.palette
        let (side, fontSize) = metrics
        let clip = shape == .circle
            ? AnyShape(Circle())
            : AnyShape(RoundedRectangle(cornerRadius: MortarTokens.radius))
        ZStack {
            clip.fill(p[\.sunken])
            Text(verbatim: initials)
                .font(mortar.theme.font(size: fontSize, weight: .bold))
                .foregroundStyle(p[\.ink])
            if let image {
                AsyncImage(url: image) { phase in
                    if let picture = phase.image {
                        picture.resizable().scaledToFill()
                    }
                }
            }
        }
        .frame(width: side, height: side)
        .clipShape(clip)
        .overlay(alignment: .bottomTrailing) {
            if let status {
                Circle()
                    .fill(status == .online ? p[\.success] : p[\.lineStrong])
                    .frame(width: 10, height: 10)
                    .overlay(Circle().strokeBorder(p[\.surface], lineWidth: 2))
                    .offset(x: 1, y: 1)
            }
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(Text(verbatim: name))
        .accessibilityValue(status.map { $0 == .online ? "Online" : "Offline" } ?? "")
        .accessibilityAddTraits(.isImage)
        .accessibilityHidden(decorative)
    }
}
