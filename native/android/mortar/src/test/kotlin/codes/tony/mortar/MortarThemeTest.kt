package codes.tony.mortar

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class MortarThemeTest {
    @Test
    fun neutralBrandUsesTheGeneratedTokens() {
        assertEquals(MortarTokens.light, MortarBrand.Neutral.light)
        assertEquals(MortarTokens.dark, MortarBrand.Neutral.dark)
        assertEquals(MortarTokens.comfortable, MortarBrand.Neutral.density)
        assertEquals(0xFFFFFFFFL, MortarTokens.light.paper)
        assertEquals(Color.White, mortarColour(MortarTokens.light.paper))
        assertEquals(Color(0xFF1D4ED8), mortarColour(0xFF1D4ED8L))
    }

    @Test
    fun touchTargetIsAndroidsLarger48() {
        assertEquals(48f, TouchTarget.value)
    }

    @Test
    fun accentWashMovesAwayFromTheText() {
        fun wash(onAccent: Long) = MortarTokens.light.copy(onAccent = onAccent).accentWash()
        assertEquals(Color.Black, wash(0xFFFFFFFFL))
        assertEquals(Color.White, wash(0xFF111111L))
        // Mid greys either side of oklab lightness 0.5.
        assertEquals(Color.White, wash(0xFF5E5E5EL))
        assertEquals(Color.Black, wash(0xFF6A6A6AL))
    }

    @Test
    fun avatarInitials() {
        assertEquals("AL", initials("Ada Lovelace"))
        assertEquals("GH", initials("grace brewster murray hopper"))
        assertEquals("C", initials("Cher"))
        assertEquals("", initials("  "))
    }
}
