package codes.tony.mortar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.CornerRadius

/** A button's emphasis, as on the web: one primary action per screen. */
public enum class MortarButtonVariant { Primary, Secondary, Ghost, Danger, Accent }

/**
 * A control's size. Medium follows the brand's density and never drops below
 * Android's 48dp touch target; small and large are fixed.
 */
public enum class MortarControlSize { Sm, Md, Lg }

/**
 * A button with Mortar's look.
 *
 * @param loading Shows a spinner and ignores clicks, keeping the button focusable.
 * @param iconOnly Shows only [icon]; [text] stays the accessible name.
 */
@Composable
public fun MortarButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MortarButtonVariant = MortarButtonVariant.Secondary,
    size: MortarControlSize = MortarControlSize.Md,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    iconOnly: Boolean = false,
) {
    val p = Mortar.palette
    val density = Mortar.brand.density
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val (height, pad, fontSize) = when (size) {
        MortarControlSize.Sm -> Triple(30.dp, 10.dp, 13f)
        MortarControlSize.Md -> Triple(maxOf(density.control.dp, TouchTarget), density.pad.dp, density.fontSize)
        MortarControlSize.Lg -> Triple(48.dp, 20.dp, 15f)
    }
    val c = ::mortarColour
    val active = pressed && enabled && !loading
    val (fill, content, border) = when {
        !enabled -> Triple(c(p.sunken), c(p.muted), null)
        else -> when (variant) {
            MortarButtonVariant.Primary ->
                Triple(c(if (active) p.primaryActive else p.primary), c(p.onPrimary), c(p.primary))
            MortarButtonVariant.Secondary ->
                Triple(c(if (active) p.sunken else p.surface), c(p.ink), c(p.lineStrong))
            MortarButtonVariant.Ghost ->
                Triple(if (active) c(p.sunken) else Color.Transparent, c(p.secondary), null)
            MortarButtonVariant.Danger ->
                Triple(if (active) c(p.sunken) else Color.Transparent, c(p.danger), c(p.danger))
            MortarButtonVariant.Accent -> Triple(c(p.accent), c(p.onAccent), null)
        }
    }
    val radius = MortarTokens.radius.dp
    val shape = RoundedCornerShape(radius)
    val dashed = c(p.lineStrong)
    val wash = p.accentWash()
    Row(
        modifier = modifier
            .offset(y = if (active) 1.dp else 0.dp)
            .defaultMinSize(minWidth = height, minHeight = height)
            .clip(shape)
            .background(fill)
            .drawWithContent {
                drawContent()
                if (variant == MortarButtonVariant.Accent && active) drawRect(wash.copy(alpha = 0.28f))
                if (!enabled) {
                    val inset = 0.5.dp.toPx()
                    drawRoundRect(
                        color = dashed,
                        topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                        size = androidx.compose.ui.geometry.Size(this.size.width - 2 * inset, this.size.height - 2 * inset),
                        cornerRadius = CornerRadius(radius.toPx()),
                        style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))),
                    )
                }
            }
            .then(if (border != null) Modifier.border(1.dp, border, shape) else Modifier)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled && !loading,
                role = Role.Button,
                onClickLabel = null,
                onClick = onClick,
            )
            .semantics { if (loading) stateDescription = "Loading" }
            .padding(PaddingValues(horizontal = if (iconOnly && icon != null) 0.dp else pad, vertical = 4.dp)),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(13.dp), color = content, strokeWidth = 2.dp)
        }
        if (icon != null) {
            Icon(icon, contentDescription = if (iconOnly) text else null, tint = content, modifier = Modifier.size(16.dp))
        }
        if (!(iconOnly && icon != null)) {
            Text(
                text,
                style = mortarText(fontSize, FontWeight.SemiBold),
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
