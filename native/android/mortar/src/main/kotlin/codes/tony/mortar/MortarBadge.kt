package codes.tony.mortar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** A short status label: a pill with a dot in its tone's colour. */
@Composable
public fun MortarBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: MortarTone = MortarTone.Neutral,
) {
    val colour = tone.colour(Mortar.palette)
    Row(
        modifier = modifier
            .border(1.dp, mortarColour(Mortar.palette.line), CircleShape)
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clearAndSetSemantics { contentDescription = text },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).background(colour, CircleShape))
        Text(text, style = mortarText(12f, FontWeight.SemiBold), color = colour)
    }
}
