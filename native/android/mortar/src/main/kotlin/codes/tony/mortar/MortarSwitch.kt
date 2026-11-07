package codes.tony.mortar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A labelled switch with an optional description under the label. The whole
 * row toggles, and TalkBack reads it as a switch.
 */
@Composable
public fun MortarSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
) {
    val p = Mortar.palette
    val knob by animateDpAsState(if (checked) 17.dp else 3.dp, tween(150), label = "knob")
    Row(
        modifier
            .heightIn(min = TouchTarget)
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
            .alpha(if (enabled) 1f else 0.5f),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                label,
                style = mortarText(Mortar.brand.density.fontSize, FontWeight.Medium),
                color = mortarColour(p.ink),
            )
            if (description != null) {
                Text(description, style = mortarText(13f), color = mortarColour(p.muted))
            }
        }
        Box(
            Modifier
                .size(width = 34.dp, height = 20.dp)
                .background(mortarColour(if (checked) p.primary else p.sunken), CircleShape)
                .border(1.dp, mortarColour(if (checked) p.primary else p.lineStrong), CircleShape),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                Modifier
                    .offset(x = knob)
                    .size(14.dp)
                    .background(mortarColour(if (checked) p.onPrimary else p.muted), CircleShape),
            )
        }
    }
}
