package codes.tony.mortar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** How much space a [MortarCard]'s body keeps around its content. */
public enum class MortarCardPadding { Default, Compact, None }

/** A [MortarCard]'s background: the surface, or sunken. */
public enum class MortarCardTone { Default, Muted }

/** A bordered surface with an optional title, header actions and footer. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun MortarCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    padding: MortarCardPadding = MortarCardPadding.Default,
    tone: MortarCardTone = MortarCardTone.Default,
    actions: (@Composable RowScope.() -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val p = Mortar.palette
    val shape = RoundedCornerShape(MortarTokens.radiusLg.dp)
    val line = mortarColour(p.line)
    val inset = when (padding) {
        MortarCardPadding.Default -> 20.dp
        MortarCardPadding.Compact -> 14.dp
        MortarCardPadding.None -> 0.dp
    }
    Column(
        modifier
            .clip(shape)
            .background(mortarColour(if (tone == MortarCardTone.Muted) p.sunken else p.surface))
            .border(1.dp, line, shape),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides mortarColour(p.ink),
            LocalTextStyle provides mortarText(Mortar.brand.density.fontSize),
        ) {
            if (title != null || description != null || actions != null) {
                FlowRow(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(Modifier.align(Alignment.CenterVertically).padding(end = 12.dp)) {
                        if (title != null) {
                            Text(
                                title,
                                style = mortarText(15f, FontWeight.SemiBold, heading = true),
                                modifier = Modifier.semantics { heading() },
                            )
                        }
                        if (description != null) {
                            Text(description, style = mortarText(13f), color = mortarColour(p.muted))
                        }
                    }
                    if (actions != null) {
                        Row(
                            Modifier.align(Alignment.CenterVertically),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            content = actions,
                        )
                    }
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(line))
            }
            Box(Modifier.fillMaxWidth().padding(inset)) { content() }
            if (footer != null) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(line))
                CompositionLocalProvider(
                    LocalContentColor provides mortarColour(p.secondary),
                    LocalTextStyle provides mortarText(13f),
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(mortarColour(p.sunken))
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                    ) { footer() }
                }
            }
        }
    }
}
