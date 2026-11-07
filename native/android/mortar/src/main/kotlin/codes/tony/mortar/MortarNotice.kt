package codes.tony.mortar

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

/**
 * A message about the screen or a task, in a status tone, with optional
 * actions and a dismiss button.
 *
 * @param announce TalkBack reads the notice when it appears: politely, or at
 *   once for [MortarTone.Danger].
 * @param onDismiss Shows a dismiss button that calls it.
 * @param dismissLabel The dismiss button's accessible name.
 */
@Composable
public fun MortarNotice(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    tone: MortarTone = MortarTone.Neutral,
    announce: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    dismissLabel: String = "Dismiss",
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    val p = Mortar.palette
    val colour = tone.colour(p)
    val fontSize = Mortar.brand.density.fontSize
    val shape = RoundedCornerShape(MortarTokens.radius.dp)
    Row(
        modifier
            .background(mortarColour(p.surface), shape)
            .border(1.dp, mortarColour(p.line), shape)
            .padding(16.dp)
            .semantics {
                if (announce) {
                    liveRegion = if (tone == MortarTone.Danger) LiveRegionMode.Assertive else LiveRegionMode.Polite
                }
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = mortarText(fontSize, FontWeight.SemiBold), color = colour)
            if (message != null) Text(message, style = mortarText(fontSize), color = colour)
            if (actions != null) {
                CompositionLocalProvider(
                    LocalContentColor provides colour,
                    LocalTextStyle provides mortarText(fontSize, FontWeight.SemiBold)
                        .copy(color = colour, textDecoration = TextDecoration.Underline),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), content = actions)
                }
            }
        }
        if (onDismiss != null) {
            Box(
                Modifier
                    .offset(x = 12.dp, y = (-12).dp)
                    .size(TouchTarget)
                    .clickable(onClickLabel = null, role = Role.Button, onClick = onDismiss)
                    .semantics { this.contentDescription = dismissLabel },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Close, null, tint = colour, modifier = Modifier.size(16.dp))
            }
        }
    }
}
