package codes.tony.mortar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/** A Mortar text style in the brand's body or heading font. Sizes are sp, so they follow the font scale. */
@Composable
@ReadOnlyComposable
internal fun mortarText(
    size: Float,
    weight: FontWeight = FontWeight.Normal,
    heading: Boolean = false,
    lineHeight: TextUnit = TextUnit.Unspecified,
): TextStyle {
    val brand = Mortar.brand
    return TextStyle(
        fontFamily = if (heading) brand.headingFont ?: brand.bodyFont else brand.bodyFont,
        fontSize = size.sp,
        fontWeight = weight,
        lineHeight = lineHeight,
    )
}
