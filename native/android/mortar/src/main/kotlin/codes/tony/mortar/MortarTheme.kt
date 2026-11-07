package codes.tony.mortar

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlin.math.cbrt
import kotlin.math.pow

/**
 * A brand for a composition: its palettes, fonts and density. Pass it to
 * [MortarTheme] once near the root.
 *
 * @property bodyFont The body font, or null for the system font.
 * @property headingFont Headings and component titles; the body font when null.
 */
@Immutable
public data class MortarBrand(
    public val light: MortarPalette = MortarTokens.light,
    public val dark: MortarPalette = MortarTokens.dark,
    public val bodyFont: FontFamily? = null,
    public val headingFont: FontFamily? = null,
    public val density: MortarDensity = MortarTokens.comfortable,
) {
    public companion object {
        /** Mortar's neutral brand. */
        public val Neutral: MortarBrand = MortarBrand()
    }
}

private val LocalMortarBrand = staticCompositionLocalOf { MortarBrand.Neutral }
private val LocalMortarPalette = staticCompositionLocalOf { MortarTokens.light }

/**
 * Themes Mortar components in [content], in the brand's dark palette when
 * [darkTheme] is true.
 */
@Composable
public fun MortarTheme(
    brand: MortarBrand = MortarBrand.Neutral,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalMortarBrand provides brand,
        LocalMortarPalette provides if (darkTheme) brand.dark else brand.light,
        content = content,
    )
}

/** The current brand and palette, inside a [MortarTheme]. */
public object Mortar {
    public val brand: MortarBrand
        @Composable @ReadOnlyComposable get() = LocalMortarBrand.current

    public val palette: MortarPalette
        @Composable @ReadOnlyComposable get() = LocalMortarPalette.current
}

/** One of the palette's colours, 0xFFRRGGBB, as a Compose colour. */
internal fun mortarColour(argb: Long): Color = Color(argb)

/** Android's minimum touch target, larger than Mortar's 44. */
internal val TouchTarget = maxOf(MortarTokens.touchTarget, 48f).dp

/**
 * Black or white, whichever moves away from on-accent: the wash laid over a
 * pressed accent fill so its text contrast only rises.
 */
internal fun MortarPalette.accentWash(): Color {
    fun channel(shift: Int): Double {
        val v = ((onAccent shr shift) and 0xFF).toDouble() / 255
        return if (v <= 0.04045) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    }
    val (r, g, b) = Triple(channel(16), channel(8), channel(0))
    // Oklab lightness, as tokens.css's oklch(from … round(1 - l, 1) 0 0).
    val l = cbrt(0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b)
    val m = cbrt(0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b)
    val s = cbrt(0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b)
    val lightness = 0.2104542553 * l + 0.793617785 * m - 0.0040720468 * s
    return if (lightness < 0.5) Color.White else Color.Black
}

/** The status colours, shared by badges, notices and fields. */
public enum class MortarTone {
    Neutral, Info, Success, Warning, Danger;

    internal fun colour(palette: MortarPalette): Color = mortarColour(
        when (this) {
            Neutral -> palette.secondary
            Info -> palette.info
            Success -> palette.success
            Warning -> palette.warning
            Danger -> palette.danger
        },
    )
}
