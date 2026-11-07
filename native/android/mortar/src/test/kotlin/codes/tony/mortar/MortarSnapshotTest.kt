package codes.tony.mortar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

/**
 * Each component's states on a phone in light, dark and twice the default
 * font size, and a branded screen on a tablet. Paparazzi renders on the JVM
 * with layoutlib, so the references in src/test/snapshots are the same on
 * every machine. Record with `gradle recordPaparazziDebug`.
 */
class MortarSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5, maxPercentDifference = 0.1)

    private fun check(
        device: DeviceConfig = DeviceConfig.PIXEL_5,
        variants: List<String> = listOf("light", "dark", "large-text"),
        brand: MortarBrand = MortarBrand.Neutral,
        content: @Composable () -> Unit,
    ) {
        for (variant in variants) {
            paparazzi.unsafeUpdateConfig(
                deviceConfig = device.copy(fontScale = if (variant == "large-text") 2f else 1f),
            )
            paparazzi.snapshot(name = variant) {
                MortarTheme(brand = brand, darkTheme = variant == "dark") {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(mortarColour(Mortar.palette.paper))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) { content() }
                }
            }
        }
    }

    @Test
    fun button() = check {
        for (variant in MortarButtonVariant.entries) {
            MortarButton("${variant.name} action", onClick = {}, variant = variant)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MortarButton("Small", onClick = {}, variant = MortarButtonVariant.Primary, size = MortarControlSize.Sm)
            MortarButton("Large", onClick = {}, variant = MortarButtonVariant.Primary, size = MortarControlSize.Lg)
        }
        MortarButton("Saving", onClick = {}, variant = MortarButtonVariant.Primary, loading = true)
        MortarButton("Disabled", onClick = {}, variant = MortarButtonVariant.Primary, enabled = false)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MortarButton("Share", onClick = {}, icon = Icons.Filled.Share)
            MortarButton("Delete", onClick = {}, variant = MortarButtonVariant.Danger, icon = Icons.Filled.Delete, iconOnly = true)
        }
    }

    @Test
    fun badge() = check {
        for (tone in MortarTone.entries) MortarBadge(tone.name, tone = tone)
    }

    @Test
    fun card() = check {
        MortarCard(
            title = "Billing",
            description = "Your plan renews on 1 December.",
            actions = { MortarButton("Change", onClick = {}, size = MortarControlSize.Sm) },
            footer = { Text("Invoices go to billing@example.com") },
        ) { Text("Team plan, 12 seats") }
        MortarCard(padding = MortarCardPadding.Compact, tone = MortarCardTone.Muted) {
            Text("A compact, muted card without a header.")
        }
    }

    @Test
    fun textField() = check {
        MortarTextField("", {}, "Email", placeholder = "you@example.com", hint = "We'll send a confirmation link.", required = true)
        MortarTextField("Hive", {}, "Workspace name", error = "That name is taken. Try another.")
        MortarTextField("hunter22", {}, "Password", secure = true)
        MortarTextField("MORTAR", {}, "Invite code", enabled = false)
    }

    @Test
    fun switch() = check {
        MortarSwitch(true, {}, "Email notifications", description = "A summary of activity each morning.")
        MortarSwitch(false, {}, "Product updates")
        MortarSwitch(true, {}, "Two-step sign-in", enabled = false)
    }

    @Test
    fun avatar() = check {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            for (size in MortarAvatarSize.entries) MortarAvatar("Ada Lovelace", size = size)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MortarAvatar("Grace Hopper", size = MortarAvatarSize.Lg, status = MortarAvatarStatus.Online)
            MortarAvatar("Alan Turing", size = MortarAvatarSize.Lg, shape = MortarAvatarShape.Square, status = MortarAvatarStatus.Offline)
        }
    }

    @Test
    fun notice() = check {
        for (tone in MortarTone.entries) {
            MortarNotice("${tone.name} notice", message = "Something happened that you should know about.", tone = tone)
        }
        MortarNotice(
            "Export ready",
            message = "Your file is ready to download.",
            tone = MortarTone.Success,
            onDismiss = {},
            actions = { Text("Download") },
        )
    }

    @Test
    fun emptyState() = check {
        MortarEmptyState(
            "No projects yet",
            description = "Projects you create or join appear here.",
            icon = Icons.Outlined.Info,
            action = { MortarButton("New project", onClick = {}, variant = MortarButtonVariant.Primary) },
        )
        MortarEmptyState("No results", description = "Try a different search.", dashed = true)
    }

    @Test
    fun brandOnTablet() {
        val brand = MortarBrand(
            light = MortarTokens.light.copy(primary = 0xFF1D4ED8, primaryActive = 0xFF1E40AF, focus = 0xFF1D4ED8),
            dark = MortarTokens.dark.copy(primary = 0xFF93C5FD, onPrimary = 0xFF0B1F4D, focus = 0xFF93C5FD),
        )
        check(device = DeviceConfig.PIXEL_C, variants = listOf("light", "dark"), brand = brand) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                MortarCard(
                    Modifier.width(420.dp),
                    title = "Profile",
                    description = "Shown to your team.",
                    footer = {
                        MortarButton("Save", onClick = {}, variant = MortarButtonVariant.Primary)
                    },
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MortarAvatar("Ada Lovelace", size = MortarAvatarSize.Lg, status = MortarAvatarStatus.Online)
                            MortarBadge("Admin", tone = MortarTone.Info)
                        }
                        MortarTextField("Ada", {}, "Display name")
                        MortarSwitch(true, {}, "Show my status")
                    }
                }
                MortarEmptyState(
                    "No activity",
                    Modifier.width(360.dp),
                    description = "Changes to your profile appear here.",
                    dashed = true,
                )
            }
        }
    }
}
