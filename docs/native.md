# Native apps

Mortar is coming to iPhone, iPad and Android as a SwiftUI package and a Jetpack Compose library, tracked in [#95](https://github.com/tonycodes/mortar/issues/95). This page covers what exists so far: the design tokens both libraries are built on.

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

## Brands

`nativeTheme(brand)` in `src/native-theme.ts` resolves a `BrandDefinition` to literal light and dark palettes the way `tokens.css` resolves it: omitted values keep the defaults or derive from primary, and `color-mix(in oklab, …)` tints are computed in JS. Brand colours must be literal and opaque (hex, `white`, `black`, `rgb()`, `hsl()`, `oklab()` or `oklch()`). Accents use the flat `accent`, not the gradient. Fonts become family names plus a generic family; the app bundles the fonts itself.

It isn't a public entry point yet. The Swift and Kotlin libraries will load a resolved brand at runtime.

## Changing tokens

`src/native-theme.ts` holds the defaults and scales; `tests/native.test.mjs` fails when they differ from `tokens.css`, or when a brand resolves differently from the CSS. After changing either, regenerate the files:

```sh
npm run native:tokens
```

The test also fails while the generated files are out of date, and compiles them with `swiftc` (Swift 6 language mode) and `kotlinc` when those are installed. CI requires both compilers.
