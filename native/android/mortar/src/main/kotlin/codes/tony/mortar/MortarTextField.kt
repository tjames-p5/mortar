package codes.tony.mortar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * A labelled text field with an optional hint and error, as the web Input.
 * The label, hint and error stay visible; TalkBack reads the label as the
 * field's name, then "required", the error and the hint.
 *
 * @param placeholder Shown while empty. Never a substitute for [label].
 * @param secure A password field.
 */
@Composable
public fun MortarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    hint: String? = null,
    error: String? = null,
    required: Boolean = false,
    secure: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val p = Mortar.palette
    val density = Mortar.brand.density
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(MortarTokens.radius.dp)
    val border = when {
        error != null -> p.danger
        focused -> p.focus
        else -> p.lineStrong
    }
    val description = listOfNotNull(label, if (required) "required" else null, hint).joinToString(", ")
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.clearAndSetSemantics {}) {
            Text(label, style = mortarText(13f, FontWeight.SemiBold), color = mortarColour(p.ink))
            if (required) Text(" *", style = mortarText(13f, FontWeight.SemiBold), color = mortarColour(p.danger))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            textStyle = mortarText(density.fontSize).copy(color = mortarColour(if (enabled) p.ink else p.muted)),
            cursorBrush = SolidColor(mortarColour(p.primary)),
            visualTransformation = if (secure) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (secure) keyboardOptions.copy(keyboardType = KeyboardType.Password) else keyboardOptions,
            interactionSource = interaction,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = description
                    if (error != null) error(error)
                },
            decorationBox = { field ->
                Box(
                    Modifier
                        .heightIn(min = maxOf(density.control.dp, TouchTarget))
                        .background(mortarColour(if (enabled) p.surface else p.sunken), shape)
                        .border(if (focused || error != null) 2.dp else 1.dp, mortarColour(border), shape)
                        .padding(horizontal = density.pad.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(placeholder, style = mortarText(density.fontSize), color = mortarColour(p.muted))
                    }
                    field()
                }
            },
        )
        if (error != null) {
            Row(
                Modifier.clearAndSetSemantics {},
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Info, null, tint = mortarColour(p.danger), modifier = Modifier.size(14.dp))
                Text(error, style = mortarText(12f), color = mortarColour(p.danger))
            }
        }
        if (hint != null) {
            Text(hint, style = mortarText(12f), color = mortarColour(p.muted), modifier = Modifier.clearAndSetSemantics {})
        }
    }
}
