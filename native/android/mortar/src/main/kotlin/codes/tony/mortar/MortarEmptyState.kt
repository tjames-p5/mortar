package codes.tony.mortar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Where a [MortarEmptyState] aligns its content. */
public enum class MortarEmptyStateAlignment { Center, Start }

/** What a screen shows when it has nothing yet: a title, a reason and the action that fills it. */
@Composable
public fun MortarEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
    dashed: Boolean = false,
    alignment: MortarEmptyStateAlignment = MortarEmptyStateAlignment.Center,
    action: (@Composable () -> Unit)? = null,
) {
    val p = Mortar.palette
    val center = alignment == MortarEmptyStateAlignment.Center
    val dash = mortarColour(p.lineStrong)
    val radius = MortarTokens.radiusLg.dp
    Column(
        modifier
            .fillMaxWidth()
            .drawBehind {
                if (dashed) {
                    val w = 2.dp.toPx()
                    drawRoundRect(
                        color = dash,
                        topLeft = Offset(w / 2, w / 2),
                        size = Size(size.width - w, size.height - w),
                        cornerRadius = CornerRadius(radius.toPx()),
                        style = Stroke(w, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))),
                    )
                }
            }
            .padding(vertical = if (dashed) 48.dp else 64.dp, horizontal = if (!center && !dashed) 0.dp else 24.dp),
        horizontalAlignment = if (center) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        if (icon != null) {
            Box(
                Modifier
                    .padding(bottom = 16.dp)
                    .size(48.dp)
                    .background(mortarColour(p.sunken), RoundedCornerShape(radius)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = mortarColour(p.secondary), modifier = Modifier.size(24.dp))
            }
        }
        val align = if (center) TextAlign.Center else TextAlign.Start
        Text(
            title,
            style = mortarText(18f, FontWeight.SemiBold, heading = true),
            color = mortarColour(p.ink),
            textAlign = align,
            modifier = Modifier.semantics { heading() },
        )
        if (description != null) {
            Text(
                description,
                style = mortarText(Mortar.brand.density.fontSize),
                color = mortarColour(p.muted),
                textAlign = align,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
        if (action != null) {
            Box(Modifier.padding(top = 20.dp)) { action() }
        }
    }
}
