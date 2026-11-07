package codes.tony.mortar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

public enum class MortarAvatarSize { Xs, Sm, Md, Lg, Xl }
public enum class MortarAvatarShape { Circle, Square }
public enum class MortarAvatarStatus { Online, Offline }

/** The first letters of the first and last words of [name]. */
internal fun initials(name: String): String {
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return listOfNotNull(words.firstOrNull(), words.drop(1).lastOrNull())
        .joinToString("") { it.first().uppercase() }
}

/**
 * A person's picture, or their initials when there is none. Load the image
 * with your image library and pass its painter; pass null while it loads or
 * when it fails.
 *
 * @param decorative Hides the avatar from TalkBack, for when the name is shown beside it.
 */
@Composable
public fun MortarAvatar(
    name: String,
    modifier: Modifier = Modifier,
    image: Painter? = null,
    size: MortarAvatarSize = MortarAvatarSize.Md,
    shape: MortarAvatarShape = MortarAvatarShape.Circle,
    status: MortarAvatarStatus? = null,
    decorative: Boolean = false,
) {
    val p = Mortar.palette
    val (side, fontSize) = when (size) {
        MortarAvatarSize.Xs -> 20 to 9
        MortarAvatarSize.Sm -> 26 to 10
        MortarAvatarSize.Md -> 34 to 12
        MortarAvatarSize.Lg -> 52 to 18
        MortarAvatarSize.Xl -> 72 to 24
    }
    val clip = if (shape == MortarAvatarShape.Circle) CircleShape else RoundedCornerShape(MortarTokens.radius.dp)
    Box(
        modifier
            .size(side.dp)
            .clearAndSetSemantics {
                if (!decorative) {
                    contentDescription = name
                    role = Role.Image
                    if (status != null) stateDescription = if (status == MortarAvatarStatus.Online) "Online" else "Offline"
                }
            },
    ) {
        Box(
            Modifier.matchParentSize().clip(clip).background(mortarColour(p.sunken)),
            contentAlignment = Alignment.Center,
        ) {
            if (image != null) {
                Image(image, null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
            } else {
                // Fixed size: the avatar does not grow with the font scale.
                Text(
                    initials(name),
                    style = TextStyle(
                        fontFamily = Mortar.brand.bodyFont,
                        fontSize = (fontSize / androidx.compose.ui.platform.LocalDensity.current.fontScale).sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = mortarColour(p.ink),
                )
            }
        }
        if (status != null) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset(1.dp, 1.dp)
                    .size(10.dp)
                    .background(mortarColour(if (status == MortarAvatarStatus.Online) p.success else p.lineStrong), CircleShape)
                    .border(2.dp, mortarColour(p.surface), CircleShape),
            )
        }
    }
}
