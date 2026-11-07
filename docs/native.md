# Native apps

Mortar is coming to iPhone, iPad and Android as a SwiftUI package and a Jetpack Compose library, tracked in [#95](https://github.com/tonycodes/mortar/issues/95). This page covers what exists so far: the design tokens both libraries are built on, and the first SwiftUI components.

## Tokens

SwiftUI and Compose cannot read `tokens.css`, so each library gets its tokens as literal values in a generated file:

| Platform | File                                                                      | Colours                                            |
| -------- | ------------------------------------------------------------------------- | -------------------------------------------------- |
| Apple    | `native/apple/Sources/Mortar/MortarTokens.swift`                          | `UInt32`, `0xRRGGBB`                               |
| Android  | `native/android/mortar/src/main/kotlin/codes/tony/mortar/MortarTokens.kt` | `Long`, `0xFFRRGGBB`, the form `Color(Long)` takes |

Each file holds:

- `MortarTokens.light` and `MortarTokens.dark`: a `MortarPalette` for each appearance. Its colours mirror the `--m-*` tokens: `paper`, `surface`, `sunken`, `line`, `lineStrong`, `ink`, `secondary`, `muted`, the four status colours and `onFeedback`, `primary` with `primaryHover`, `primaryActive` and `onPrimary`, `accent` and `onAccent`, `focus`, `link`, `selected`, and the code block colours.
- `MortarTokens.compact`, `comfortable` and `spacious`: a `MortarDensity` with the control height, padding, font size and row padding.
- The scales, named after their CSS properties: `space1` to `space12`, `radiusXs` to `radiusFull` (`radius` is `--m-radius`), `weightRegular` to `weightBold`, `trackingDisplay` to `trackingCaps` (in em), `leadingDisplay` to `leadingBody`, `icon`, `focusOffset`, `touchTarget` and `scrimOpacity`. Sizes are in points on Apple and dp on Android.

`touchTarget` is Mortar's 44. Material asks for 48dp on Android, so the Compose components will use the larger of the two.

## SwiftUI

Add the package in Xcode (File > Add Package Dependencies…) or in `Package.swift`, from this repository's URL and a release tag:

```swift
.package(url: "https://github.com/tonycodes/mortar", from: "0.17.0")
```

It needs iOS 17, iPadOS 17 or macOS 14. Theme the app once near its root, then use the components:

```swift
import Mortar

@main
struct HiveApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView().mortarTheme(.neutral)
        }
    }
}

MortarButton("Save", variant: .primary) { save() }
Button("Cancel", action: cancel).buttonStyle(.mortar(.ghost))
```

A brand is a `MortarTheme`: its light and dark `MortarPalette`s, font family names (registered by the app) and density. Every component reads the palette that matches the colour scheme, and scales its text with Dynamic Type.

| Component                                                  | Web equivalent | Notes                                                                                                                                                                     |
| ---------------------------------------------------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `MortarButton`, `.buttonStyle(.mortar(_:size:isLoading:))` | Button         | `primary`, `secondary`, `ghost`, `danger`, `accent`; `sm`, `md`, `lg`; loading; SF Symbol, icon-only with the title as its accessible name. Medium is at least 44pt tall. |
| `MortarBadge`                                              | Badge          | Tones `neutral`, `info`, `success`, `warning`, `danger`.                                                                                                                  |
| `MortarCard`                                               | Card           | Title, description, header actions, footer, `compact` or `none` padding, `muted` tone.                                                                                    |
| `MortarTextField`                                          | Input          | Label, prompt, hint, error, required marker, secure entry. VoiceOver reads the label as its name, then "required", the error and the hint.                                |
| `MortarSwitch`, `.toggleStyle(.mortar)`                    | Switch         | The whole row toggles; VoiceOver sees a standard switch.                                                                                                                  |
| `MortarAvatar`                                             | Avatar         | Image URL with initials while loading or on failure, five sizes, circle or square, online or offline status, decorative.                                                  |
| `MortarNotice`                                             | Notice         | Tone, message, actions, dismiss button, VoiceOver announcement.                                                                                                           |
| `MortarEmptyState`                                         | EmptyState     | SF Symbol, description, action, dashed, centred or leading.                                                                                                               |

### Snapshot tests

`native/apple/Tests/MortarTests` renders each component on an iPhone simulator in light, dark and a large accessibility text size, and a branded screen on iPad, with [swift-snapshot-testing](https://github.com/pointfreeco/swift-snapshot-testing). The **Mortar for SwiftUI** workflow runs them on macOS on every change to `Package.swift` or `native/apple`, and builds the package for Mac.

Rendering differs between iOS versions, so the reference images are recorded on CI's simulator, not on a developer's Mac. To add or update one, delete the old image (or add the test), push, and download the `mortar-swift-snapshots-<sha>` artifact from the failed run: it holds the newly recorded images and any failed comparisons. Review them, commit them under `__Snapshots__`, and push again.

## Brands

`nativeTheme(brand)` in `src/native-theme.ts` resolves a `BrandDefinition` to literal light and dark palettes the way `tokens.css` resolves it: omitted values keep the defaults or derive from primary, and `color-mix(in oklab, …)` tints are computed in JS. Brand colours must be literal and opaque (hex, `white`, `black`, `rgb()`, `hsl()`, `oklab()` or `oklch()`). Accents use the flat `accent`, not the gradient. Fonts become family names plus a generic family; the app bundles the fonts itself.

It isn't a public entry point yet. The Swift and Kotlin libraries will load a resolved brand at runtime.

## Changing tokens

`src/native-theme.ts` holds the defaults and scales; `tests/native.test.mjs` fails when they differ from `tokens.css`, or when a brand resolves differently from the CSS. After changing either, regenerate the files:

```sh
npm run native:tokens
```

The test also fails while the generated files are out of date, and compiles them with `swiftc` (Swift 6 language mode) and `kotlinc` when those are installed. CI requires both compilers.
